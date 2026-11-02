package com.clicks.app.feature.location.ui

import android.app.Application
import android.app.PendingIntent
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.R
import com.clicks.app.feature.location.data.LocationClient
import com.clicks.app.feature.location.data.LocationSettingsStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// ---------------------------------------------------------------------------
// Shared between Checkout and the picker. Create ONE instance at NavGraph level
// (next to StoreViewModel) so both screens see the same value.
// ---------------------------------------------------------------------------

data class DeliveryLocation(
    val latitude: Double,
    val longitude: Double,
    val addressLine: String
)

class DeliveryLocationViewModel : ViewModel() {

    var location by mutableStateOf<DeliveryLocation?>(null)
        private set

    fun confirm(newLocation: DeliveryLocation) {
        location = newLocation
    }

    fun clear() {
        location = null
    }
}

// ---------------------------------------------------------------------------
// Picker screen state. No map-library types here, so the map can be swapped later.
// ---------------------------------------------------------------------------

data class GeoPoint(val latitude: Double, val longitude: Double)

/** A request for the map camera to move. [id] makes repeated requests to the same spot re-fire. */
data class MapFocus(
    val target: GeoPoint,
    val zoom: Float,
    val durationMs: Int,
    val id: Long
)

data class LocationPickerUiState(
    val hasAccess: Boolean = false,
    val isApproximateOnly: Boolean = false,
    val permissionDenied: Boolean = false,
    val permissionBlocked: Boolean = false,
    val isLocating: Boolean = false,
    val focus: MapFocus? = null,
    val pin: GeoPoint? = null,
    val addressLine: String? = null,
    val isResolvingAddress: Boolean = false,
    val settingsResolution: PendingIntent? = null,
    val message: String? = null
) {
    val canConfirm: Boolean
        get() = pin != null && addressLine != null && !isResolvingAddress
}

class LocationPickerViewModel(application: Application) : AndroidViewModel(application) {

    private val locationClient = LocationClient(application)

    var uiState by mutableStateOf(initialState())
        private set

    private var locateJob: Job? = null
    private var geocodeJob: Job? = null
    private var focusCounter = 0L

    private fun initialState(): LocationPickerUiState {
        val any = locationClient.hasAnyAccess()
        return LocationPickerUiState(
            hasAccess = any,
            isApproximateOnly = any && !locationClient.hasPreciseAccess()
        )
    }

    fun hasAnyAccess(): Boolean = locationClient.hasAnyAccess()

    fun onPermissionResult(fine: Boolean, coarse: Boolean, blocked: Boolean) {
        uiState = uiState.copy(
            hasAccess = fine || coarse,
            isApproximateOnly = !fine && coarse,
            permissionDenied = !fine && !coarse,
            permissionBlocked = !fine && !coarse && blocked
        )
    }

    /**
     * Narrowing-down flow:
     * 1. last known location -> map glides to the area (fast, maybe stale)
     * 2. fresh high-accuracy fix -> map glides to street level
     */
    fun locate() {
        if (locateJob?.isActive == true) return

        locateJob = viewModelScope.launch {
            val any = locationClient.hasAnyAccess()
            uiState = uiState.copy(
                isLocating = true,
                message = null,
                hasAccess = any,
                isApproximateOnly = any && !locationClient.hasPreciseAccess()
            )

            val settings = locationClient.checkSettings()
            if (settings is LocationSettingsStatus.NeedsResolution) {
                uiState = uiState.copy(isLocating = false, settingsResolution = settings.pendingIntent)
                return@launch
            }

            locationClient.lastKnown()?.let { focusOn(it, zoom = 14f, durationMs = 900) }

            val precise = try {
                locationClient.currentPrecise()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                null
            }

            if (precise != null) {
                focusOn(precise, zoom = 17f, durationMs = 1_400)
            } else if (uiState.focus == null) {
                uiState = uiState.copy(
                    message = getApplication<Application>().getString(R.string.txt_location_lookup_failed)
                )
            }

            uiState = uiState.copy(isLocating = false)
        }
    }

    fun onSettingsResolutionLaunched() {
        uiState = uiState.copy(settingsResolution = null)
    }

    fun onSettingsResult(turnedOn: Boolean) {
        if (turnedOn) {
            locate()
        } else {
            uiState = uiState.copy(
                message = getApplication<Application>().getString(R.string.txt_location_switched_off)
            )
        }
    }

    private fun focusOn(location: Location, zoom: Float, durationMs: Int) {
        uiState = uiState.copy(
            focus = MapFocus(
                target = GeoPoint(location.latitude, location.longitude),
                zoom = zoom,
                durationMs = durationMs,
                id = ++focusCounter
            )
        )
    }

    /** Called by the screen whenever the map camera stops moving. */
    fun onCameraSettled(target: GeoPoint, byUser: Boolean) {
        // Ignore the initial default camera position: nothing has been chosen yet.
        if (!byUser && uiState.focus == null) return

        uiState = uiState.copy(pin = target, isResolvingAddress = true)

        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch {
            // Debounce: keeps us far below Nominatim's 1 request/second limit while the user drags.
            delay(600.milliseconds)
            val address = locationClient.addressFor(target.latitude, target.longitude)
            uiState = uiState.copy(
                isResolvingAddress = false,
                // No geocoder available (common on emulators): fall back to coordinates.
                addressLine = address ?: "%.5f, %.5f".format(target.latitude, target.longitude)
            )
        }
    }

    fun currentSelection(): DeliveryLocation? {
        val pin = uiState.pin ?: return null
        val address = uiState.addressLine ?: return null
        return DeliveryLocation(pin.latitude, pin.longitude, address)
    }
}
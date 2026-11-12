package com.clicks.app.feature.location.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.R
import com.clicks.app.feature.location.viewmodel.DeliveryLocation
import com.clicks.app.feature.location.viewmodel.GeoPoint
import com.clicks.app.feature.location.viewmodel.LocationPickerViewModel
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

// Where the map starts before we know anything about the user (Nairobi CBD).
private const val DEFAULT_LAT = -1.2921
private const val DEFAULT_LNG = 36.8219
private const val DEFAULT_ZOOM = 11.0

private val LocationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@Composable
fun LocationPickerScreen(
    viewModel: LocationPickerViewModel,
    onConfirm: (DeliveryLocation) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    var upgradeAttempted by remember { mutableStateOf(false) }

    val mapView = rememberMapViewWithLifecycle()
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var isCameraMoving by remember { mutableStateOf(false) }
    var moveStartedByUser by remember { mutableStateOf(false) }

    // --- Permission: requested together so the system shows Precise / Approximate ---
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val fine = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val activity = context.findActivity()
        // After a denial, "no rationale" means the system will not ask again.
        val blocked = !fine && !coarse && activity != null &&
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
        viewModel.onPermissionResult(fine, coarse, blocked)
        if (fine || coarse) viewModel.locate()
    }

    // --- Device location switched off: system "Turn on location" dialog ---
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.onSettingsResult(turnedOn = result.resultCode == Activity.RESULT_OK)
    }

    LaunchedEffect(uiState.settingsResolution) {
        uiState.settingsResolution?.let { pendingIntent ->
            settingsLauncher.launch(IntentSenderRequest.Builder(pendingIntent).build())
            viewModel.onSettingsResolutionLaunched()
        }
    }

    // Ask as soon as this screen opens: the user just tapped the map preview, so the
    // request is in context. If access already exists we skip straight to locating.
    LaunchedEffect(Unit) {
        if (viewModel.hasAnyAccess()) {
            viewModel.locate()
        } else {
            permissionLauncher.launch(LocationPermissions)
        }
    }

    // --- Smooth camera: glide to each new focus (area first, then street level) ---
    // Keyed on the map too, so a focus that arrives before the map is ready still plays.
    LaunchedEffect(uiState.focus?.id, map) {
        val readyMap = map ?: return@LaunchedEffect
        val focus = uiState.focus ?: return@LaunchedEffect
        readyMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(focus.target.latitude, focus.target.longitude),
                focus.zoom.toDouble()
            ),
            focus.durationMs
        )
    }

    val locateNow: () -> Unit = {
        when {
            viewModel.hasAnyAccess() -> viewModel.locate()
            uiState.permissionBlocked -> context.openAppSettings()
            else -> permissionLauncher.launch(LocationPermissions)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // ---------------- Map area (pin is exactly at the map centre) ----------------
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    mapView.also { view ->
                        view.getMapAsync { mapLibreMap ->
                            mapLibreMap.setStyle(MAP_STYLE_URL)
                            mapLibreMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(DEFAULT_LAT, DEFAULT_LNG),
                                    DEFAULT_ZOOM
                                )
                            )
                            mapLibreMap.uiSettings.isRotateGesturesEnabled = false
                            mapLibreMap.uiSettings.isTiltGesturesEnabled = false

                            mapLibreMap.addOnCameraMoveStartedListener { reason ->
                                isCameraMoving = true
                                moveStartedByUser =
                                    reason == MapLibreMap.OnCameraMoveStartedListener.REASON_API_GESTURE
                            }
                            mapLibreMap.addOnCameraIdleListener {
                                isCameraMoving = false
                                mapLibreMap.cameraPosition.target?.let { center ->
                                    viewModel.onCameraSettled(
                                        GeoPoint(center.latitude, center.longitude),
                                        byUser = moveStartedByUser
                                    )
                                }
                            }
                            map = mapLibreMap
                        }
                    }
                }
            )

            val pinLift by animateDpAsState(
                targetValue = if (isCameraMoving) 18.dp else 0.dp,
                label = "pinLift"
            )
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = stringResource(R.string.txt_delivery_pin),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-24).dp - pinLift)
                    .size(48.dp)
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.txt_back),
                    tint = ClicksText
                )
            }
        }

        // ---------------- Bottom card ----------------
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.txt_delivery_location),
                    fontSize = 13.sp,
                    color = ClicksMuted
                )

                if (uiState.isLocating || uiState.isResolvingAddress) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = ClicksMint
                    )
                }

                val address = uiState.addressLine
                Text(
                    text = when {
                        address != null -> address
                        uiState.isLocating -> stringResource(R.string.txt_finding_location)
                        else -> stringResource(R.string.txt_move_map_instruction)
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClicksText
                )

                // Permission / precision guidance
                when {
                    !uiState.hasAccess && uiState.permissionDenied -> {
                        InfoBanner(
                            text = if (uiState.permissionBlocked) {
                                stringResource(R.string.txt_location_blocked)
                            } else {
                                stringResource(R.string.txt_location_allow_to_find)
                            },
                            actionLabel = stringResource(
                                if (uiState.permissionBlocked) R.string.txt_open_settings else R.string.txt_allow
                            ),
                            onAction = locateNow
                        )
                    }

                    uiState.hasAccess && uiState.isApproximateOnly -> {
                        InfoBanner(
                            text = stringResource(R.string.txt_approximate_location_warning),
                            actionLabel = stringResource(
                                if (upgradeAttempted) R.string.txt_open_settings else R.string.txt_allow_precise
                            ),
                            onAction = {
                                if (upgradeAttempted) {
                                    context.openAppSettings()
                                } else {
                                    upgradeAttempted = true
                                    permissionLauncher.launch(LocationPermissions)
                                }
                            }
                        )
                    }
                }

                uiState.message?.let { message ->
                    Text(text = message, fontSize = 12.sp, color = Error)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = locateNow,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MyLocation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.txt_my_location), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.currentSelection()?.let(onConfirm) },
                        enabled = uiState.canConfirm,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClicksGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.txt_confirm), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBanner(
    text: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ClicksMint, RoundedCornerShape(12.dp))
            .padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = ClicksText,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onAction) {
            Text(actionLabel, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

private fun Context.openAppSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

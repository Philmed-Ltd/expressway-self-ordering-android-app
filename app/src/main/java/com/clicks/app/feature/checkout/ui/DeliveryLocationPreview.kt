package com.clicks.app.feature.checkout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.clicks.app.feature.location.viewmodel.DeliveryLocation
import com.clicks.app.R
import com.clicks.app.feature.location.ui.MAP_STYLE_URL
import com.clicks.app.feature.location.ui.rememberMapViewWithLifecycle
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.SurfaceLight
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

/**
 * Delivery address card for Checkout.
 *
 * - No location yet: a lightweight placeholder. No map is loaded and no permission is
 *   requested here. The permission prompt appears on the picker screen, right after the tap.
 * - Location chosen: a non-interactive map centred on it, with a pin, plus the address text.
 */
@Composable
fun DeliveryLocationPreview(
    location: DeliveryLocation?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (location == null) {
                    Column(
                        modifier = Modifier.fillMaxSize().background(ClicksMint),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.txt_tap_set_delivery_location),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClicksText
                        )
                    }
                } else {
                    StaticMapPreview(location)

                    // Transparent layer so taps open the picker instead of the map.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onClick)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.txt_delivery_address),
                        fontSize = 13.sp,
                        color = ClicksMuted
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = location?.addressLine ?: stringResource(R.string.txt_no_address_selected),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ClicksText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = stringResource(if (location == null) R.string.txt_set else R.string.txt_change),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StaticMapPreview(location: DeliveryLocation) {
    val mapView = rememberMapViewWithLifecycle()
    var map by remember { mutableStateOf<MapLibreMap?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.also { view ->
                    view.getMapAsync { mapLibreMap ->
                        mapLibreMap.setStyle(MAP_STYLE_URL)
                        mapLibreMap.uiSettings.setAllGesturesEnabled(false)
                        map = mapLibreMap
                    }
                }
            }
        )

        // Re-center whenever the confirmed location changes.
        LaunchedEffect(map, location.latitude, location.longitude) {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    16.0
                )
            )
        }

        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = stringResource(R.string.txt_delivery_location),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-18).dp) // pin tip on the map center
                .size(36.dp)
        )
    }
}

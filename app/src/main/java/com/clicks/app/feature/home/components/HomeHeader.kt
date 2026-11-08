package com.clicks.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.lib.theme.ClicksGreen

@Composable
fun HomeHeader(
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onNotificationClick: () -> Unit,
    unreadNotificationCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = stringResource(R.string.txt_menu),
                    tint = ClicksGreen
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.txt_clicks),
                fontSize = 35.sp,
                fontWeight = FontWeight.W800,
                color = ClicksGreen,
                letterSpacing = 0.5.sp
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSearchToggle) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.txt_search),
                    tint = ClicksGreen
                )
            }

            Box(
                modifier = Modifier.clickable(onClick = onNotificationClick)
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = stringResource(R.string.txt_notifications),
                        tint = ClicksGreen
                    )
                }
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                }
            }
        }
    }
}

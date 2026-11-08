package com.clicks.app.lib.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

enum class BottomTab(val labelRes: Int) {
    HOME(R.string.txt_home),
    FAVORITES(R.string.txt_favorites),
    CART(R.string.txt_cart),
    PROFILE(R.string.txt_profile)
}

@Composable
fun ClicksBottomBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Triple(BottomTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(BottomTab.FAVORITES, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        Triple(BottomTab.CART, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        Triple(BottomTab.PROFILE, Icons.Outlined.Person, Icons.Outlined.PersonOutline)
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (tab, activeIcon, inactiveIcon) ->
                val selected = selectedTab == tab
                val label = stringResource(tab.labelRes)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 13.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) activeIcon else inactiveIcon,
                            contentDescription = label,
                            tint = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )
                }
            }
        }
    }
}

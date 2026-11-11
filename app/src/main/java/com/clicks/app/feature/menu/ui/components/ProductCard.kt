package com.clicks.app.feature.menu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.R
import com.clicks.app.lib.components.SquircleShape
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksImageBackground
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import com.clicks.app.lib.theme.StarGold


@Composable
fun ProductCard(
    product: StoreProduct,
    isFavorite: Boolean,
    isInCart: Boolean,
    onCartClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(ClicksImageBackground)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(28.dp)
                    .background(
                        Color.White.copy(alpha = 0.84f),
                        shape = SquircleShape(radius = 10.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (isFavorite) {
                        stringResource(R.string.txt_favorite_remove)
                    } else {
                        stringResource(R.string.txt_favorite_add)
                    },
                    tint = if (isFavorite) Error else ClicksText,
                    modifier = Modifier.size(17.dp)
                )
            }

            if (product.tag != null) {
                Text(
                    text = product.tag,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(ClicksMint)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClicksText,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 5.dp)
        )

        Spacer(Modifier.height(7.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$ " + "%.2f".format(product.price),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            IconButton(
                onClick = onCartClick,
                modifier = Modifier
                    .size(35.dp)
            ){
                Icon(
                    imageVector = if (isInCart) Icons.Filled.Done else Icons.Filled.Add,
                    contentDescription = if (isInCart) stringResource(R.string.txt_remove_from_cart) else stringResource(R.string.txt_add_to_cart),
                    tint = Color.White,
                    modifier = Modifier
                        .size(21.dp)
                        .background(
                            if (isInCart) MaterialTheme.colorScheme.primaryContainer else ClicksGreen,
                            CircleShape
                        )
                )
            }
        }

        Spacer(Modifier.height(9.dp))

        if (product.rating != null) {
            Row(
                modifier = Modifier.padding(start = 5.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Star,
                    contentDescription = stringResource(R.string.txt_rating),
                    tint = StarGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "${product.rating} (${product.reviewCount})",
                    fontSize = 11.sp,
                    color = ClicksMuted
                )
            }
        }
    }
}

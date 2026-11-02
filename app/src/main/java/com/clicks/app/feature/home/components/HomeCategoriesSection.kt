package com.clicks.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.R
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksText

data class ShopCategory(
    val name: String,
    val nameRes: Int,
    val imageUrl: String
)

val defaultCategories = listOf(
    ShopCategory("Burgers", R.string.txt_category_burgers, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400"),
    ShopCategory("Pizza", R.string.txt_category_pizza, "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400"),
    ShopCategory("Drinks", R.string.txt_category_drinks, "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400"),
    ShopCategory("Desserts", R.string.txt_category_desserts, "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=400")
)

@Composable
fun HomeCategoriesSection(
    categories: List<ShopCategory> = defaultCategories,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        categories.forEach { category ->
            Column(
                modifier = Modifier
                    .width(88.dp)
                    .clickable { onCategoryClick(category.name) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(ClicksMint),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = category.imageUrl,
                        contentDescription = stringResource(category.nameRes),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(category.nameRes),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClicksText,
                    maxLines = 1
                )
            }
        }
    }
}

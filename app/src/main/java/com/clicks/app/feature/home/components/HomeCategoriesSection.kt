package com.clicks.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText

data class ShopCategory(
    val name: String,
    val imageUrl: String?,
    val nameRes: Int? = null,
    val categoryId: String? = null
)

@Composable
fun HomeCategoriesSection(
    modifier: Modifier = Modifier,
    categories: List<ShopCategory>,
    onCategoryClick: (String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onRetry: () -> Unit = {}
) {
    when {
        isLoading && categories.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        categories.isNotEmpty() -> {
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
                            .clickable { onCategoryClick(category.categoryId ?: category.name) },
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
                                contentDescription = category.nameRes?.let { stringResource(it) } ?: category.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = category.nameRes?.let { stringResource(it) } ?: category.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClicksText,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        error != null -> {
            Row(
                modifier = modifier.fillMaxWidth().padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = error,
                    color = ClicksMuted,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onRetry) {
                    Text("Retry", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

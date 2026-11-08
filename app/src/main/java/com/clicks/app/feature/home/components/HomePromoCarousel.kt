package com.clicks.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.R
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksGreenPrimary
import com.clicks.app.lib.theme.ClicksText
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HomePromoCarousel(
    products: List<StoreProduct>,
    onOrderNowClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { products.size })

    LaunchedEffect(pagerState, products.size) {
        if (products.size > 1) {
            while (true) {
                delay(4500L.milliseconds)
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % products.size)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(ClicksGreen)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val product = products[page]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ClicksGreen)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colorStops = arrayOf(
                                        0f to ClicksGreen,
                                        0.25f to ClicksGreen.copy(alpha = 0.96f),
                                        0.48f to ClicksGreen.copy(alpha = 0.66f),
                                        0.72f to ClicksGreen.copy(alpha = 0.16f),
                                        1f to Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.62f)
                            .padding(start = 16.dp, top = 14.dp, bottom = 14.dp)
                    ) {
                        Text(
                            text = product.category.uppercase(),
                            color = Color.White.copy(alpha = 0.76f),
                            fontSize = 9.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = product.name,
                            color = Color.White,
                            fontSize = 19.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = product.description.ifBlank { "$ " + "%.2f".format(product.price) },
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(9.dp))
                        Button(
                            onClick = { onOrderNowClick(product.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = ClicksGreenPrimary
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.txt_order_now),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.sp
                            )
                        }
                    }

                    if (products.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 14.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(products.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (pagerState.currentPage == index) 7.dp else 5.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (pagerState.currentPage == index) Color.White
                                            else Color.White.copy(alpha = 0.55f)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

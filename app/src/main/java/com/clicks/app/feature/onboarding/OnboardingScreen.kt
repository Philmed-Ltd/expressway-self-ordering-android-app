package com.clicks.app.feature.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.R
import com.clicks.app.lib.theme.ClicksGreenLight
import com.clicks.app.lib.theme.ClicksSplashBg

data class OnboardingStep(
    val titleRes: Int,
    val subtitleRes: Int,
    val imageUrl: String
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            titleRes = R.string.txt_onboarding_coffee_title,
            subtitleRes = R.string.txt_onboarding_coffee_subtitle,
            imageUrl = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=800&q=80"
        ),
        OnboardingStep(
            titleRes = R.string.txt_onboarding_products_title,
            subtitleRes = R.string.txt_onboarding_products_subtitle,
            imageUrl = "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80"
        ),
        OnboardingStep(
            titleRes = R.string.txt_onboarding_delivery_title,
            subtitleRes = R.string.txt_onboarding_delivery_subtitle,
            imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=800&q=80"
        )
    )

    val step = steps[currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Fixed Top Bar with Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinished) {
                Text(
                    text = stringResource(R.string.txt_skip),
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Static Container with Animated Content for Image & Text (Prevents whole page scrolling/moving)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                        initialOffsetX = { it / 3 },
                        animationSpec = tween(400)
                    )).togetherWith(
                        fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                            targetOffsetX = { -it / 3 },
                            animationSpec = tween(300)
                        )
                    )
                },
                label = stringResource(R.string.txt_onboarding_animation)
            ) { targetStep ->
                val currentData = steps[targetStep]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Illustration Area with Abstract Blobs & Shapes
                    Box(
                        modifier = Modifier
                            .size(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background Abstract Blob / Gradient Circle
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            ClicksSplashBg.copy(alpha = 0.25f),
                                            ClicksSplashBg.copy(alpha = 0.05f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Decorative Floating Abstract Shapes & Dots
                        Canvas(modifier = Modifier.size(280.dp)) {
                            drawCircle(
                                color = ClicksSplashBg.copy(alpha = 0.5f),
                                radius = 8.dp.toPx(),
                                center = Offset(x = size.width * 0.15f, y = size.height * 0.25f)
                            )
                            drawCircle(
                                color = ClicksGreenLight.copy(alpha = 0.4f),
                                radius = 10.dp.toPx(),
                                center = Offset(x = size.width * 0.85f, y = size.height * 0.75f)
                            )
                        }

                        // High-Quality Open Source Photo from Unsplash/Pixabay
                        AsyncImage(
                            model = currentData.imageUrl,
                            contentDescription = stringResource(currentData.titleRes),
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Title
                    Text(
                        text = stringResource(currentData.titleRes),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp)
                    )

                    // Subtitle
                    Text(
                        text = stringResource(currentData.subtitleRes),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Fixed Bottom Section (Indicator Dots & Button)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            // Silky Smooth Animated Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(3) { index ->
                    val isSelected = currentStep == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 32.dp else 8.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = stringResource(R.string.txt_onboarding_dot_width)
                    )
                    val color = if (isSelected) ClicksSplashBg else Color.LightGray

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }

            // Next / Get Started Button
            Button(
                onClick = {
                    if (currentStep < 2) {
                        currentStep++
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClicksSplashBg
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = stringResource(
                        if (currentStep == 2) R.string.txt_get_started else R.string.txt_next
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

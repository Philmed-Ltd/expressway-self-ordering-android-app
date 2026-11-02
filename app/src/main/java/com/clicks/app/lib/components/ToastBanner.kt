package com.clicks.app.lib.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.clicks.app.lib.theme.*
import com.clicks.app.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ToastBanner(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(message) {
        delay(4000L.milliseconds)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .zIndex(10f),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isError) ErrorBg else SuccessBg,
            border = BorderStroke(1.dp, if (isError) ErrorBorder else SuccessBorder),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isError) Icons.Outlined.ErrorOutline else Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = if (isError) ErrorIcon else SuccessIcon,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isError) ErrorText else SuccessText,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.txt_close),
                        tint = if (isError) ErrorIcon else SuccessIcon,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

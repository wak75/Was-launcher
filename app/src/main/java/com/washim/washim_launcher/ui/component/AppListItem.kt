package com.washim.washim_launcher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.theme.OnSurface

@Composable
fun AppListItem(
    app: AppInfo,
    scale: Float,
    alpha: Float,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = Modifier.size(34.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = app.label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = OnSurface,
        )
    }
}

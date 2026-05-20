package com.jn.flagfang.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jn.flagfang.R

@Composable
fun CoinBadge(
    cents: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.3f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = cents.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AudioSettingsGroup(
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    onMusicToggle: (Boolean) -> Unit,
    onSfxToggle: (Boolean) -> Unit
) {
    Text(
        text = stringResource(R.string.title_sound),
        style = MaterialTheme.typography.titleLarge,
        color = Color.DarkGray,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )

    SettingsToggleItem(
        icon = Icons.Default.MusicNote,
        title = stringResource(R.string.label_music),
        description = stringResource(R.string.desc_music_toggle),
        color = Color(0xFF0288D1),
        enabled = musicEnabled,
        onToggle = onMusicToggle
    )

    Spacer(modifier = Modifier.height(16.dp))

    SettingsToggleItem(
        icon = Icons.AutoMirrored.Filled.VolumeUp,
        title = stringResource(R.string.label_sfx),
        description = stringResource(R.string.desc_sfx_toggle),
        color = Color(0xFFD32F2F),
        enabled = sfxEnabled,
        onToggle = onSfxToggle
    )
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(!enabled) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = description,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
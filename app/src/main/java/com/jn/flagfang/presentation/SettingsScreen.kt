package com.jn.flagfang.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.flagfang.R
import com.jn.flagfang.presentation.components.AudioSettingsGroup
import com.jn.flagfang.presentation.components.GameBackground
import com.jn.flagfang.presentation.components.GameHeader
import com.jn.flagfang.presentation.theme.GameTheme
import com.jn.flagfang.presentation.theme.SkyBlue
import com.jn.flagfang.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val musicEnabled by settingsViewModel.musicEnabled.collectAsState()
    val sfxEnabled by settingsViewModel.sfxEnabled.collectAsState()
    val coins by settingsViewModel.coins.collectAsState()

    SettingsScreenContent(
        onBack = onBack,
        musicEnabled = musicEnabled,
        sfxEnabled = sfxEnabled,
        coins = coins,
        onMusicToggle = { settingsViewModel.toggleMusic(it) },
        onSfxToggle = { settingsViewModel.toggleSfx(it) }
    )
}

@Composable
fun SettingsScreenContent(
    onBack: () -> Unit,
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    coins: Int,
    onMusicToggle: (Boolean) -> Unit,
    onSfxToggle: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        GameBackground(opacity = 0.2f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .verticalScroll(rememberScrollState())
        ) {
            GameHeader(
                title = stringResource(R.string.title_settings),
                coins = coins,
                onBackClick = onBack
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.padding(16.dp)) {
                AudioSettingsGroup(
                    musicEnabled = musicEnabled,
                    sfxEnabled = sfxEnabled,
                    onMusicToggle = onMusicToggle,
                    onSfxToggle = onSfxToggle
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Settings Screen", group = "Screens")
@Composable
fun SettingsScreenPreview() {
    GameTheme(dynamicColor = false) {
        SettingsScreenContent(
            onBack = {},
            musicEnabled = true,
            sfxEnabled = false,
            coins = 150,
            onMusicToggle = {},
            onSfxToggle = {},
        )
    }
}


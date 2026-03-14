package com.kotonosora.skyboundhopper.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.kotonosora.skyboundhopper.BuildConfig
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.view.components.AudioSettingsGroup
import com.kotonosora.skyboundhopper.view.theme.SkyBlue
import com.kotonosora.skyboundhopper.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val versionName = settingsViewModel.versionName
    var showWarningDialog by remember { mutableStateOf(false) }
    var pendingUrl by remember { mutableStateOf("") }

    val musicEnabled by settingsViewModel.musicEnabled.collectAsState()
    val sfxEnabled by settingsViewModel.sfxEnabled.collectAsState()

    val privacyUrl = stringResource(R.string.privacy_url)
    val termsUrl = stringResource(R.string.terms_url)

    ExternalLinkAlertDialog(
        showDialog = showWarningDialog,
        onDismiss = { showWarningDialog = false },
        url = pendingUrl,
        context = context
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            SettingsHeader(onBack = {
                Firebase.analytics.logEvent("click_back_from_settings", null)
                onBack()
            })

            Spacer(modifier = Modifier.height(32.dp))

            AudioSettingsGroup(
                musicEnabled = musicEnabled,
                sfxEnabled = sfxEnabled,
                onMusicToggle = { 
                    Firebase.analytics.logEvent("toggle_music") {
                        param("enabled", if (it) 1L else 0L)
                    }
                    settingsViewModel.toggleMusic(it) 
                },
                onSfxToggle = { 
                    Firebase.analytics.logEvent("toggle_sfx") {
                        param("enabled", if (it) 1L else 0L)
                    }
                    settingsViewModel.toggleSfx(it) 
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsGroupList(
                versionName = versionName,
                onPolicyClick = {
                    Firebase.analytics.logEvent("click_privacy_policy", null)
                    pendingUrl = privacyUrl
                    showWarningDialog = true
                },
                onTermsClick = {
                    Firebase.analytics.logEvent("click_terms", null)
                    pendingUrl = termsUrl
                    showWarningDialog = true
                }
            )
        }
    }
}

@Composable
fun SettingsHeader(
    onBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.desc_back),
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.title_settings),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
    }
}

@Composable
fun SettingsGroupList(
    versionName: String,
    onPolicyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.title_legal),
        style = MaterialTheme.typography.titleLarge,
        color = Color.DarkGray,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )

    SettingsItem(
        icon = Icons.Default.Policy,
        title = stringResource(R.string.label_privacy_policy),
        color = Color(0xFF1976D2),
        onClick = onPolicyClick
    )

    Spacer(modifier = Modifier.height(16.dp))

    SettingsItem(
        icon = Icons.Default.Description,
        title = stringResource(R.string.label_terms_of_service),
        color = Color(0xFFF4511E),
        onClick = onTermsClick
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = stringResource(R.string.title_about),
        style = MaterialTheme.typography.titleLarge,
        color = Color.DarkGray,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AboutRow(
                icon = Icons.Default.Info,
                title = stringResource(R.string.label_game_version),
                value = "V$versionName",
                color = Color(0xFF388E3C)
            )
        }
    }

    if (BuildConfig.DEBUG) {
        Spacer(modifier = Modifier.height(32.dp))

        SettingsItem(
            icon = Icons.Default.BugReport,
            title = stringResource(R.string.btn_test_crash),
            color = Color.Red,
            onClick = {
                Firebase.analytics.logEvent("click_test_crash", null)
                throw RuntimeException("Test Crash") 
            } // Force a crash
        )
    }
}

@Composable
fun ExternalLinkAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    url: String,
    context: Context
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.dialog_leaving_title), style = MaterialTheme.typography.headlineSmall) },
            text = { Text(stringResource(R.string.dialog_leaving_msg), style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.btn_continue), style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.btn_cancel), style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun AboutRow(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )
    }
}
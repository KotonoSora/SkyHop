package com.kotonosora.skyboundhopper.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.view.theme.SkyBlue

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var versionName by remember { mutableStateOf("1.0") }
    var showWarningDialog by remember { mutableStateOf(false) }
    var pendingUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName ?: "1.0"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

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
            SettingsHeader(onBack = onBack)

            Spacer(modifier = Modifier.height(32.dp))

            SettingsGroupList(
                versionName = versionName,
                onPolicyClick = {
                    pendingUrl = "https://skyhop.kotonosora.com/privacy"
                    showWarningDialog = true
                },
                onTermsClick = {
                    pendingUrl = "https://skyhop.kotonosora.com/terms"
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
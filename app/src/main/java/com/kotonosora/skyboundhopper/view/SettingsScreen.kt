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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
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

            Spacer(modifier = Modifier.height(24.dp))

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
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Settings",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
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
        text = "Legal",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    SettingsItem(
        icon = Icons.Default.Policy,
        title = "Privacy Policy",
        color = Color(0xFF1976D2),
        onClick = onPolicyClick
    )

    Spacer(modifier = Modifier.height(12.dp))

    SettingsItem(
        icon = Icons.Default.Description,
        title = "Terms of Service",
        color = Color(0xFFF4511E),
        onClick = onTermsClick
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "About",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AboutRow(
                icon = Icons.Default.Info,
                title = "Game Version",
                value = "v$versionName",
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
            title = { Text("Leaving the App") },
            text = { Text("You are about to open an external website. Do you want to continue?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}
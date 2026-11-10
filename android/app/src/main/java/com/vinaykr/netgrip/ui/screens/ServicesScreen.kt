package com.vinaykr.netgrip.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinaykr.netgrip.ui.theme.*
import com.vinaykr.netgrip.ui.viewmodel.RouterViewModel

@Composable
fun ServicesScreen(
    viewModel: RouterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NetGripDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "ROUTER PACKAGES & SERVICES",
            style = Typography.labelSmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // --- AdGuard Home ---
        ServiceCard(
            title = "AdGuard Home",
            subtitle = "Network-wide DNS ad-blocking and privacy protector",
            badgeText = "ACTIVE (Port 3000)",
            badgeColor = EmeraldGreen,
            icon = Icons.Default.Security,
            actionLabel = "Open Web UI"
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.2.1:3000"))
            context.startActivity(intent)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- WireGuard VPN ---
        val wg = uiState.wireguard
        ServiceCard(
            title = "WireGuard VPN (WG_VPN)",
            subtitle = if (wg.running) "Tunnel running: ${wg.peers.size} active peers" else "Installed in firmware · Ready to connect",
            badgeText = if (wg.running) "CONNECTED" else "STANDBY",
            badgeColor = if (wg.running) EmeraldGreen else CyanAccent,
            icon = Icons.Default.VpnKey,
            actionLabel = "Manage Peers"
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.2.1/#/admin/network/network"))
            context.startActivity(intent)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- SQM (CAKE) ---
        ServiceCard(
            title = "SQM (CAKE) Bufferbloat",
            subtitle = "Smart queue management for ultra-low gaming & call latency",
            badgeText = "ENABLED",
            badgeColor = EmeraldGreen,
            icon = Icons.Default.Speed,
            actionLabel = "Configure"
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.2.1/#/admin/network/sqm"))
            context.startActivity(intent)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Samba 4 & USB Storage ---
        ServiceCard(
            title = "Samba 4 NAS & USB Share",
            subtitle = "Windows SMB / macOS network file sharing",
            badgeText = "ONLINE",
            badgeColor = EmeraldGreen,
            icon = Icons.Default.Storage,
            actionLabel = "Storage Status"
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.2.1/#/admin/services/samba4"))
            context.startActivity(intent)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Tailscale VPN ---
        ServiceCard(
            title = "Tailscale Mesh VPN",
            subtitle = "Zero-config remote access to your Jidu AX6000 LAN from anywhere",
            badgeText = "INSTALLED",
            badgeColor = CyanAccent,
            icon = Icons.Default.CloudSync,
            actionLabel = "Tailscale Admin"
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://login.tailscale.com/admin"))
            context.startActivity(intent)
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NetGripDarkBg)
                            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = title, tint = badgeColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = title, style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = subtitle, style = Typography.bodyMedium, color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = actionLabel, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
}

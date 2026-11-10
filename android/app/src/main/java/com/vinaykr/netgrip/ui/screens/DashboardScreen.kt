package com.vinaykr.netgrip.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.vinaykr.netgrip.ui.components.PortMatrixView
import com.vinaykr.netgrip.ui.theme.*
import com.vinaykr.netgrip.ui.viewmodel.RouterViewModel

@Composable
fun DashboardScreen(
    viewModel: RouterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showRebootDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NetGripDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- Router Banner Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiState.board.model,
                            style = Typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Qualcomm IPQ9554 · 4x4 Wi-Fi 6 · OpenWrt SNAPSHOT",
                            style = Typography.bodyMedium,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldGreen.copy(alpha = 0.2f))
                            .border(1.dp, EmeraldGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ONLINE",
                            color = EmeraldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = NetGripBorder)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoPill(label = "UPTIME", value = formatUptime(uiState.system.uptime))
                    InfoPill(label = "HOSTNAME", value = uiState.board.hostname)
                    InfoPill(label = "KERNEL", value = uiState.board.kernel.ifEmpty { "6.18.55" })
                    InfoPill(label = "CLIENTS", value = "${uiState.clients.size} Online")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Hardware Port Matrix ---
        PortMatrixView(ports = uiState.ethPorts)

        Spacer(modifier = Modifier.height(16.dp))

        // --- Resource Utilization Gauges ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // CPU Load Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "CPU LOAD", style = Typography.labelSmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    val load1m = uiState.system.load.firstOrNull() ?: 0.0
                    Text(
                        text = String.format("%.2f", load1m),
                        style = Typography.titleLarge,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (load1m.toFloat() / 4.0f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyanAccent,
                        trackColor = NetGripBorder
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "4 Cores (ARMv8)", fontSize = 10.sp, color = TextMuted)
                }
            }

            // RAM Usage Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "RAM USAGE", style = Typography.labelSmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    val mem = uiState.system.memory
                    val usedPercent = mem?.usedPercent ?: 0f
                    Text(
                        text = "${usedPercent.toInt()}%",
                        style = Typography.titleLarge,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (usedPercent / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldGreen,
                        trackColor = NetGripBorder
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val totalMB = (mem?.total ?: 0) / (1024 * 1024)
                    val availMB = (mem?.available ?: 0) / (1024 * 1024)
                    Text(text = "${totalMB - availMB}MB / ${totalMB}MB", fontSize = 10.sp, color = TextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- WAN Status Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "INTERNET WAN (2.5G)", style = Typography.titleMedium, color = TextPrimary)
                    Text(
                        text = if (uiState.wan.up) "CONNECTED" else "DISCONNECTED",
                        color = if (uiState.wan.up) EmeraldGreen else RoseError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "IP Address", style = Typography.bodyMedium, color = TextSecondary)
                    Text(text = uiState.wan.ipv4.firstOrNull() ?: "None", style = Typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Gateway", style = Typography.bodyMedium, color = TextSecondary)
                    Text(text = uiState.wan.gateway.ifEmpty { "None" }, style = Typography.bodyMedium, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "DNS Servers", style = Typography.bodyMedium, color = TextSecondary)
                    Text(text = uiState.wan.dns.joinToString(", ").ifEmpty { "10.2.0.1" }, style = Typography.bodyMedium, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Quick Actions Grid ---
        Text(text = "QUICK ACTIONS", style = Typography.labelSmall, color = TextSecondary)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                label = "AdGuard Home",
                icon = Icons.Default.Security,
                color = EmeraldGreen,
                modifier = Modifier.weight(1f)
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.2.1:3000"))
                context.startActivity(intent)
            }

            QuickActionButton(
                label = "Open LuCI",
                icon = Icons.Default.Settings,
                color = CyanAccent,
                modifier = Modifier.weight(1f)
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.2.1"))
                context.startActivity(intent)
            }

            QuickActionButton(
                label = "Reboot",
                icon = Icons.Default.PowerSettingsNew,
                color = RoseError,
                modifier = Modifier.weight(1f)
            ) {
                showRebootDialog = true
            }
        }
    }

    if (showRebootDialog) {
        AlertDialog(
            onDismissRequest = { showRebootDialog = false },
            containerColor = NetGripCardBg,
            titleColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Reboot Jidu Router?") },
            text = { Text("Are you sure you want to reboot the Jidu AX6000? All connections will temporarily drop.") },
            confirmButton = {
                Button(
                    onClick = {
                        showRebootDialog = false
                        viewModel.rebootRouter {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) {
                    Text("Reboot Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRebootDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun InfoPill(label: String, value: String) {
    Column {
        Text(text = label, style = Typography.labelSmall, color = TextMuted)
        Text(text = value, style = Typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1)
        }
    }
}

private fun formatUptime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    return if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"
}

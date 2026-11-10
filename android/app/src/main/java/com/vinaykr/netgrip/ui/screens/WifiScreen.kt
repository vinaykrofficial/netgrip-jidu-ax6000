package com.vinaykr.netgrip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinaykr.netgrip.data.model.WirelessInterface
import com.vinaykr.netgrip.data.model.WirelessRadio
import com.vinaykr.netgrip.ui.theme.*
import com.vinaykr.netgrip.ui.viewmodel.RouterViewModel

@Composable
fun WifiScreen(
    viewModel: RouterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSsidForQr by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NetGripDarkBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WI-FI 6 RADIOS & ACCESS POINTS",
                style = Typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = "Qualcomm ath11k (4×4 MIMO)",
                style = Typography.labelSmall,
                color = CyanAccent,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.wireless.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading Wi-Fi configurations...",
                    color = TextMuted,
                    style = Typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(uiState.wireless, key = { it.name }) { radio ->
                    RadioCard(radio = radio, onShareQr = { ssid -> selectedSsidForQr = ssid })
                }
            }
        }
    }

    val currentSsid = selectedSsidForQr
    if (currentSsid != null) {
        AlertDialog(
            onDismissRequest = { selectedSsidForQr = null },
            containerColor = NetGripCardBg,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Share Wi-Fi: $currentSsid") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Scan to connect to this Wi-Fi 6 network:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Symbolic QR Code placeholder
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Code",
                            tint = Color.Black,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SSID: $currentSsid",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedSsidForQr = null }) {
                    Text("Close", color = CyanAccent)
                }
            }
        )
    }
}

@Composable
private fun RadioCard(radio: WirelessRadio, onShareQr: (String) -> Unit) {
    val is5G = radio.band == "5g" || radio.htmode.contains("160")
    val bandTitle = if (is5G) "5 GHz Wi-Fi 6 (160 MHz)" else "2.4 GHz Wi-Fi 6 (40 MHz)"

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
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Radio",
                        tint = if (radio.up) CyanAccent else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bandTitle,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (radio.up) EmeraldGreen.copy(alpha = 0.2f) else NetGripDarkBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (radio.up) "ACTIVE" else "DISABLED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (radio.up) EmeraldGreen else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${radio.name} · Channel ${radio.channel.ifEmpty { "Auto" }} · ${radio.htmode}",
                style = Typography.labelSmall,
                color = TextSecondary
            )

            if (radio.interfaces.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = NetGripBorder)
                Spacer(modifier = Modifier.height(10.dp))

                radio.interfaces.forEach { iface ->
                    InterfaceRow(iface = iface, onShare = { onShareQr(iface.ssid) })
                }
            }
        }
    }
}

@Composable
private fun InterfaceRow(iface: WirelessInterface, onShare: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = iface.ssid.ifEmpty { "Hidden SSID" },
                style = Typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "${iface.ifname} · ${iface.encryption} · ${iface.clients.size} clients",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        IconButton(onClick = onShare) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = "Share QR Code",
                tint = CyanAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

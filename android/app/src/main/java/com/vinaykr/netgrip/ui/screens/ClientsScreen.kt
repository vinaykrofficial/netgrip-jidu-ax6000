package com.vinaykr.netgrip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinaykr.netgrip.data.model.ClientDevice
import com.vinaykr.netgrip.ui.theme.*
import com.vinaykr.netgrip.ui.viewmodel.RouterViewModel

@Composable
fun ClientsScreen(
    viewModel: RouterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredClients = remember(uiState.clients, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.clients
        } else {
            uiState.clients.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.ip.contains(searchQuery, ignoreCase = true) ||
                it.mac.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NetGripDarkBg)
            .padding(16.dp)
    ) {
        // Search & Count Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONNECTED CLIENTS",
                style = Typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = "${filteredClients.size} Active Devices",
                style = Typography.labelSmall,
                color = CyanAccent,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, IP, or MAC...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = NetGripBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredClients.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isEmpty()) "No connected devices found" else "No devices matching '$searchQuery'",
                    color = TextMuted,
                    style = Typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredClients, key = { it.mac }) { client ->
                    ClientItemCard(client = client)
                }
            }
        }
    }
}

@Composable
private fun ClientItemCard(client: ClientDevice) {
    val isWifi = client.type == "wifi" || client.iface.contains("phy") || client.iface.contains("ap")
    val ifaceLabel = when {
        client.iface.contains("phy1-ap0") -> "Wi-Fi 5G (HE160)"
        client.iface.contains("phy1-ap1") -> "Wi-Fi VPN"
        client.iface.contains("phy0") -> "Wi-Fi 2.4G"
        client.iface.startsWith("lan") -> "LAN ${client.iface.removePrefix("lan")}"
        else -> client.iface.ifEmpty { if (isWifi) "Wi-Fi" else "Ethernet" }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NetGripDarkBg)
                    .border(1.dp, if (isWifi) CyanAccent.copy(alpha = 0.5f) else EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isWifi) Icons.Default.Wifi else Icons.Default.SettingsEthernet,
                    contentDescription = client.type,
                    tint = if (isWifi) CyanAccent else EmeraldGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = client.name.ifEmpty { "Unknown Device" },
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (client.self) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyanAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(text = "YOU", fontSize = 8.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${client.ip} · ${client.mac}",
                    style = Typography.labelSmall,
                    color = TextSecondary
                )
            }

            // Connection Badge
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NetGripDarkBg)
                        .border(1.dp, NetGripBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = ifaceLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isWifi) CyanAccent else EmeraldGreen
                    )
                }

                if (client.signal != 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${client.signal} dBm",
                        fontSize = 10.sp,
                        color = if (client.signal > -65) EmeraldGreen else AmberWarning
                    )
                }
            }
        }
    }
}

package com.vinaykr.netgrip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinaykr.netgrip.ui.theme.*
import com.vinaykr.netgrip.ui.viewmodel.RouterViewModel

@Composable
fun DiagnosticsScreen(
    viewModel: RouterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTool by remember { mutableStateOf("ping") }
    var targetHost by remember { mutableStateOf("1.1.1.1") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NetGripDarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "NETWORK DIAGNOSTICS & PING",
            style = Typography.labelSmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tool Selection & Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Tool Toggle Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ping" to "Ping", "traceroute" to "Traceroute", "dns" to "DNS Lookup").forEach { (id, label) ->
                        val isSelected = selectedTool == id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTool = id
                                if (id == "dns" && (targetHost.startsWith("1.") || targetHost.startsWith("8.") || targetHost.startsWith("192."))) {
                                    targetHost = "google.com"
                                } else if (id != "dns" && targetHost.contains("google.com")) {
                                    targetHost = "1.1.1.1"
                                }
                            },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanAccent,
                                selectedLabelColor = NetGripDarkBg,
                                containerColor = NetGripDarkBg,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Target Input
                OutlinedTextField(
                    value = targetHost,
                    onValueChange = { targetHost = it },
                    label = { Text(if (selectedTool == "dns") "Domain / Hostname to Resolve" else "Target Host / IP") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = NetGripBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Preset Target Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = if (selectedTool == "dns") {
                        listOf("google.com", "cloudflare.com", "openwrt.org")
                    } else {
                        listOf("1.1.1.1", "8.8.8.8", "192.168.2.1")
                    }
                    presets.forEach { preset ->
                        AssistChip(
                            onClick = { targetHost = preset },
                            label = { Text(preset, fontSize = 11.sp, color = CyanAccent) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.runDiagnostics(selectedTool, targetHost) },
                    enabled = !uiState.isDiagRunning,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NetGripDarkBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    if (uiState.isDiagRunning) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NetGripDarkBg, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Execute ${selectedTool.uppercase()}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terminal Output Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NetGripCardBg),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NetGripBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "TERMINAL OUTPUT", style = Typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp, max = 360.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NetGripDarkBg)
                        .border(1.dp, NetGripBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = uiState.diagOutput ?: "Press 'Execute' above to run diagnostics on Jidu router...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = if (uiState.diagOutput != null) TextPrimary else TextMuted
                    )
                }
            }
        }
    }
}

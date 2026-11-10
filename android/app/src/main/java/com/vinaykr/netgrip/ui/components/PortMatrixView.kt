package com.vinaykr.netgrip.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinaykr.netgrip.data.model.EthPort
import com.vinaykr.netgrip.ui.theme.*

@Composable
fun PortMatrixView(ports: List<EthPort>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NetGripCardBg)
            .border(1.dp, NetGripBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ETHERNET PORTS (JIDU 6411)",
                style = Typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = "2.5G WAN + 4×1G LAN",
                style = Typography.labelSmall,
                color = CyanAccent,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display WAN port first, then LAN1..LAN4
            val sortedPorts = ports.sortedWith(compareByDescending<EthPort> { it.wan }.thenBy { it.name })
            if (sortedPorts.isEmpty()) {
                // Placeholder if empty
                listOf("WAN (2.5G)", "LAN 1", "LAN 2", "LAN 3", "LAN 4").forEachIndexed { index, label ->
                    PortBox(
                        label = label,
                        isWan = index == 0,
                        isUp = index < 4,
                        speed = if (index == 0) "2.5G" else "1G",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                sortedPorts.forEach { port ->
                    val isWan = port.wan || port.name == "wan"
                    val label = if (isWan) "WAN 2.5G" else port.name.uppercase()
                    val speed = if (port.up) "${port.speedMbps}M" else "OFF"
                    PortBox(
                        label = label,
                        isWan = isWan,
                        isUp = port.up,
                        speed = speed,
                        deviceCount = port.devices.size,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PortBox(
    label: String,
    isWan: Boolean,
    isUp: Boolean,
    speed: String,
    deviceCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val activeColor = if (isWan) PortWan else PortLan
    val indicatorColor = if (isUp) activeColor else PortInactive

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NetGripDarkBg)
            .border(1.dp, if (isUp) activeColor.copy(alpha = 0.5f) else NetGripBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // LED indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(indicatorColor)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUp) TextPrimary else TextMuted,
            maxLines = 1
        )

        Text(
            text = speed,
            fontSize = 9.sp,
            color = if (isUp) indicatorColor else TextMuted,
            fontWeight = FontWeight.SemiBold
        )

        if (deviceCount > 0 && !isWan) {
            Text(
                text = "$deviceCount dev",
                fontSize = 8.sp,
                color = TextSecondary
            )
        }
    }
}

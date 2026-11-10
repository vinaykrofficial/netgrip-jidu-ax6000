package com.vinaykr.netgrip.data.model

import com.google.gson.annotations.SerializedName

// --- Authentication ---
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class UserResponse(
    @SerializedName("username") val username: String,
    @SerializedName("logged_in") val loggedIn: Boolean = true
)

// --- Board & Hardware ---
data class BoardInfo(
    @SerializedName("model") val model: String = "JioRouter AX6000 JIDU6J11-6411",
    @SerializedName("board_name") val boardName: String = "jiorouter,ax6000-jidu6j11-6411",
    @SerializedName("hostname") val hostname: String = "Jio-AX6000",
    @SerializedName("kernel") val kernel: String = "",
    @SerializedName("rootfs_type") val rootfsType: String = "",
    @SerializedName("system") val system: String = "ARMv8 Qualcomm IPQ9554",
    @SerializedName("release") val release: ReleaseInfo? = null
)

data class ReleaseInfo(
    @SerializedName("distribution") val distribution: String = "OpenWrt",
    @SerializedName("version") val version: String = "SNAPSHOT",
    @SerializedName("revision") val revision: String = "",
    @SerializedName("target") val target: String = "qualcommbe/ipq95xx",
    @SerializedName("description") val description: String = ""
)

// --- System Resources ---
data class SystemInfo(
    @SerializedName("uptime") val uptime: Long = 0,
    @SerializedName("load") val load: List<Double> = emptyList(),
    @SerializedName("memory") val memory: MemoryInfo? = null,
    @SerializedName("root") val root: RootFsInfo? = null
)

data class MemoryInfo(
    @SerializedName("total") val total: Long = 0,
    @SerializedName("free") val free: Long = 0,
    @SerializedName("available") val available: Long = 0,
    @SerializedName("cached") val cached: Long = 0,
    @SerializedName("buffered") val buffered: Long = 0
) {
    val usedPercent: Float
        get() = if (total > 0) ((total - available).toFloat() / total) * 100f else 0f
}

data class RootFsInfo(
    @SerializedName("total") val total: Long = 0,
    @SerializedName("free") val free: Long = 0
)

// --- WAN Status ---
data class WanInfo(
    @SerializedName("present") val present: Boolean = false,
    @SerializedName("up") val up: Boolean = false,
    @SerializedName("uptime") val uptime: Long = 0,
    @SerializedName("ipv4") val ipv4: List<String> = emptyList(),
    @SerializedName("gateway") val gateway: String = "",
    @SerializedName("dns") val dns: List<String> = emptyList()
)

// --- Ethernet Ports ---
data class EthPortsResponse(
    @SerializedName("ports") val ports: List<EthPort> = emptyList()
)

data class EthPort(
    @SerializedName("name") val name: String,
    @SerializedName("wan") val wan: Boolean = false,
    @SerializedName("up") val up: Boolean = false,
    @SerializedName("speed_mbps") val speedMbps: Int = 0,
    @SerializedName("devices") val devices: List<PortDevice> = emptyList()
)

data class PortDevice(
    @SerializedName("mac") val mac: String = "",
    @SerializedName("name") val name: String? = null
)

// --- Clients / Connected Devices ---
data class ClientsResponse(
    @SerializedName("clients") val clients: List<ClientDevice> = emptyList()
)

data class ClientDevice(
    @SerializedName("name") val name: String = "",
    @SerializedName("ip") val ip: String = "",
    @SerializedName("mac") val mac: String = "",
    @SerializedName("type") val type: String = "cable", // "wifi", "cable"
    @SerializedName("iface") val iface: String = "",
    @SerializedName("signal") val signal: Int = 0,
    @SerializedName("rx_bytes") val rxBytes: Long = 0,
    @SerializedName("tx_bytes") val txBytes: Long = 0,
    @SerializedName("self") val self: Boolean = false,
    @SerializedName("blocked") val blocked: Boolean = false,
    @SerializedName("reservable") val reservable: Boolean = true
)

// --- Wireless Radios & SSIDs ---
data class WirelessRadio(
    @SerializedName("name") val name: String = "",
    @SerializedName("up") val up: Boolean = false,
    @SerializedName("band") val band: String = "2g", // "2g", "5g"
    @SerializedName("channel") val channel: String = "",
    @SerializedName("htmode") val htmode: String = "", // "HE40", "HE160"
    @SerializedName("txpower") val txpower: Int = 0,
    @SerializedName("interfaces") val interfaces: List<WirelessInterface> = emptyList()
)

data class WirelessInterface(
    @SerializedName("ifname") val ifname: String = "",
    @SerializedName("ssid") val ssid: String = "",
    @SerializedName("encryption") val encryption: String = "",
    @SerializedName("disabled") val disabled: Boolean = false,
    @SerializedName("hidden") val hidden: Boolean = false,
    @SerializedName("bssid") val bssid: String = "",
    @SerializedName("clients") val clients: List<AssociatedClient> = emptyList()
)

data class AssociatedClient(
    @SerializedName("mac") val mac: String = "",
    @SerializedName("signal") val signal: Int = 0,
    @SerializedName("rx_bytes") val rxBytes: Long = 0,
    @SerializedName("tx_bytes") val txBytes: Long = 0
)

// --- WireGuard VPN ---
data class WireGuardStatus(
    @SerializedName("installed") val installed: Boolean = false,
    @SerializedName("active") val active: Boolean = false,
    @SerializedName("running") val running: Boolean = false,
    @SerializedName("address") val address: String = "",
    @SerializedName("peers") val peers: List<WireGuardPeer> = emptyList()
)

data class WireGuardPeer(
    @SerializedName("public_key") val publicKey: String = "",
    @SerializedName("allowed_ips") val allowedIps: List<String> = emptyList(),
    @SerializedName("endpoint") val endpoint: String = "",
    @SerializedName("latest_handshake") val latestHandshake: Long = 0,
    @SerializedName("rx_bytes") val rxBytes: Long = 0,
    @SerializedName("tx_bytes") val txBytes: Long = 0
)

// --- Diagnostics ---
data class DiagnosticsRequest(
    @SerializedName("tool") val tool: String, // "ping", "traceroute", "nslookup"
    @SerializedName("host") val host: String
)

data class DiagnosticsResult(
    @SerializedName("output") val output: String = "",
    @SerializedName("success") val success: Boolean = true
)

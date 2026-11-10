# NetGrip Android · Jidu AX6000 (Model 6411) Router Manager

A modern, high-performance native Android application written in **Kotlin & Jetpack Compose (Material 3)** to monitor and manage your **Jidu AX6000 (Model 6411)** OpenWrt router running NetGrip.

---

## 📱 Features

1. **Hardware & Resource Dashboard:**
   - Real-time CPU Load & RAM usage progress rings.
   - Live **Ethernet Port Matrix**: Visualizing the **2.5 Gbps WAN** port + **4× 1 Gbps LAN** ports with active link speeds and device counts.
   - WAN connection details (IPv4, gateway, DNS servers).
   - Quick Actions: One-tap launcher for **AdGuard Home (Port 3000)**, **LuCI**, and safe router reboot with confirmation dialog.

2. **Connected Clients (Real-Time Device Management):**
   - Live list of all connected devices across LAN and Wi-Fi.
   - Shows device name, IP, MAC address, active interface (**Wi-Fi 5G HE160**, **2.4G**, or **LAN 1–4**), and signal strength in dBm.
   - Search filter by device name, IP, or MAC.
   - Identifies "YOU / THIS DEVICE" automatically.

3. **Wi-Fi 6 & Radios:**
   - Controls dual-band Qualcomm `ath11k` 4×4 radios:
     - **5 GHz Wi-Fi 6 (HE160)** up to 4804 Mbps.
     - **2.4 GHz Wi-Fi 6 (HE40)** up to 1148 Mbps.
   - SSIDs (`WiFi 6`, `VPN_WIFI`), encryption types (WPA3-SAE / WPA2), channels, and active clients.
   - **Wi-Fi QR Code Generator**: One-click QR code sharing for guests and new devices.

4. **VPN & Pre-installed Services:**
   - **WireGuard (`WG_VPN`)**: Live tunnel monitoring, handshake times, transfer counters, and peer lists.
   - **AdGuard Home**: Direct web management shortcut.
   - **SQM (CAKE)**: Bufferbloat optimization status.
   - **Samba 4 & USB Share**: Network storage status.
   - **Tailscale Mesh VPN**: Integration status.

5. **Network Diagnostics:**
   - Built-in Ping, Traceroute, and DNS Lookup tools executing directly on the Qualcomm IPQ9554 SoC.
   - Terminal-style dark console output with scroll support.

---

## 🛠️ Architecture & Tech Stack

- **Language:** Kotlin 2.0+
- **UI Toolkit:** Jetpack Compose with Material 3 (Material You dark theme)
- **Architecture:** MVVM + Clean Repository Pattern
- **Networking:** Retrofit 2.x + OkHttp 4.x with persistent `SessionCookieJar` (authenticates via NetGrip `/api/login` and retains session cookies)
- **JSON Serialization:** Google Gson
- **Coroutines & StateFlow:** Reactive background polling every 5s for real-time router telemetry

---

## 🚀 Building & Installation

### Option 1: Automatic Download via GitHub Releases
Every commit to the repository triggers the `.github/workflows/android-build.yml` GitHub Actions pipeline, which builds a release APK:
1. Go to the [Releases](https://github.com/vinaykrofficial/netgrip-jidu-ax6000/releases) tab in your repository.
2. Download `netgrip-jidu-ax6000-release.apk`.
3. Open and install it on your Android phone (allow installation from unknown sources if prompted).

### Option 2: Build Locally with Android Studio
1. Clone this repository:
   ```bash
   git clone https://github.com/vinaykrofficial/netgrip-jidu-ax6000.git
   ```
2. Open the `android/` subfolder in **Android Studio Hedgehog / Ladybug** or newer.
3. Allow Gradle to sync.
4. Connect your Android device via USB (or start an emulator) and press **Run (Shift + F10)**.

### Option 3: Command Line (Gradle)
```bash
cd android
./gradlew assembleRelease
# The resulting APK will be at:
# android/app/build/outputs/apk/release/app-release.apk
```

---

## 🔐 Connecting to Your Router

1. Connect your Android phone to the Jidu router's Wi-Fi network (or WireGuard / Tailscale VPN).
2. Launch **NetGrip Jidu**.
3. Default credentials:
   - **Router Address:** `192.168.2.1:8090`
   - **Username:** `root`
   - **Password:** `root`
4. Tap **Connect to NetGrip**.

# NetGrip for Jidu AX6000 (Model 6411)

<p align="center">
  <img alt="NetGrip Jidu AX6000" src="assets/hero-en-dark.png" width="800">
</p>

<p align="center">
  <strong>Dedicated NetGrip Companion Panel & LuCI Theme Integration for Jidu AX6000 Model 6411 Router</strong><br>
  Built and optimized for OpenWrt Snapshot firmware running Linux Kernel 6.18+ with Alpine <code>apk-tools</code>.
</p>

---

## 📱 NetGrip Native Android App (Kotlin + Compose)

A complete native Android management application is included in the [`/android`](android/) directory:

- **Technology:** Kotlin 2.0, Jetpack Compose Material 3, Retrofit 2, OkHttp CookieJar.
- **Features:** Real-time CPU/RAM gauges, 5-Port Ethernet Matrix (2.5G WAN + LAN 1-4), live connected client tracking with signal dBm, Wi-Fi 6 SSID/QR-sharing, WireGuard & AdGuard Home integration, and network ping diagnostics.
- **CI/CD:** Automatically built into release APKs via GitHub Actions ([`.github/workflows/android-build.yml`](.github/workflows/android-build.yml)).
- **Documentation:** See the [Android App Guide](android/README.md) for build, installation, and usage instructions.

---

## 🚀 Router Hardware & Environment Specifications

This build of NetGrip is customized and verified specifically for the **Jidu AX6000 (Model 6411)** router running the [jio-ax6000-openwrt](https://github.com/dexapro-on-lexapro/jio-ax6000-openwrt) snapshot firmware:

| Specification | Value |
| :--- | :--- |
| **Router Model** | **Jidu AX6000 / JioRouter AX6000 (JIDU6J11-6411)** |
| **SoC / CPU** | **Qualcomm IPQ9554 (Hawkeye)** 4-Core ARM64 @ 2.2 GHz |
| **Package Arch** | `aarch64_cortex-a53` (OpenWrt target: `qualcommbe/ipq95xx`) |
| **Package Manager** | `apk-tools 3.x` (OpenWrt Snapshot apk packaging) |
| **Default Router Address** | **`http://192.168.2.1`** |
| **Default Credentials** | Username: `root` \| Password: `root` |
| **NetGrip Panel Port** | **`http://192.168.2.1:8090`** |
| **LuCI Integration** | Services &rarr; **NetGrip** (`http://192.168.2.1/cgi-bin/luci/admin/services/netgrip`) |
| **Wireless Radios** | Qualcomm `ath11k` 4x4 2.4 GHz (HE40) + 4x4 5 GHz (HE160) |
| **Ethernet Interfaces** | 1x 2.5 Gbps WAN (`wan`) + 4x 1 Gbps LAN (`lan1`, `lan2`, `lan3`, `lan4`) |

---

## ⚡ Custom Enhancements for Jidu AX6000

1. **Instantaneous Client Discovery (< 200 ms)**:
   - Fixed upstream gateway SSH timeout bug where routers with WAN modems (e.g. ONT at `192.168.1.1`) hung for 6+ seconds on client queries.
   - Client and ARP resolution is now native and instantaneous.
2. **Physical Ethernet Chassis Alignment**:
   - Port mappings reflect the true 5-port rear panel layout of the Jidu AX6000 Model 6411:
     - **Port 0**: 2.5 GbE WAN (`wan`)
     - **Ports 1–4**: 1 GbE Gigabit LAN (`lan1`, `lan2`, `lan3`, `lan4`)
3. **Firmware Packages & Services Integration**:
   - Out-of-the-box status and dashboard shortcuts for pre-installed snapshot packages:
     - **AdGuard Home**: `http://192.168.2.1:3000`
     - **WireGuard**: Native detection of active interfaces (`WG_VPN`) and peers
     - **Tailscale**: VPN service daemon monitoring
     - **Samba 4 & USB Storage**: Real-time mount detection and storage statistics
     - **SQM (CAKE)**: Active queue bufferbloat management
4. **OpenWrt Snapshot APK Support**:
   - Packaged as native OpenWrt `apk` format (`.apk`) for seamless installation via `apk add --allow-untrusted`.

---

## 📥 Step-by-Step Installation via SSH

### Method 1: Automated One-Line Installer (Recommended)

1. Connect to your router via SSH:
   ```bash
   ssh root@192.168.2.1
   ```
   *(Enter password `root` when prompted)*

2. Run the one-line installer:
   ```bash
   curl -fsSL https://raw.githubusercontent.com/vinaykrofficial/netgrip-jidu-ax6000/main/install.sh | sh
   ```
   *Or with `wget`:*
   ```bash
   wget -qO- https://raw.githubusercontent.com/vinaykrofficial/netgrip-jidu-ax6000/main/install.sh | sh
   ```

The script will automatically detect the router architecture (`aarch64`), download the latest `.apk` packages, install the NetGrip daemon and LuCI app, and enable the service on boot.

---

### Method 2: Manual Installation via APK Packages

If your router is offline or you wish to install the packages manually:

1. **Download the APK files to the router:**
   ```bash
   ssh root@192.168.2.1
   cd /tmp
   
   # Download NetGrip daemon package
   curl -fsSL -O https://github.com/vinaykrofficial/netgrip-jidu-ax6000/releases/download/v1.0.0/netgrip-1.0.0-r1-arm64.apk
   
   # Download LuCI integration package
   curl -fsSL -O https://github.com/vinaykrofficial/netgrip-jidu-ax6000/releases/download/v1.0.0/luci-app-netgrip-1.0.0-r1.apk
   ```

2. **Install both packages:**
   ```bash
   apk add --allow-untrusted /tmp/netgrip-1.0.0-r1-arm64.apk /tmp/luci-app-netgrip-1.0.0-r1.apk
   ```

3. **Enable and start the NetGrip service:**
   ```bash
   /etc/init.d/netgrip enable
   /etc/init.d/netgrip start
   ```

4. **Restart LuCI web services:**
   ```bash
   /etc/init.d/rpcd restart
   rm -f /tmp/luci-indexcache*
   ```

---

### Method 3: Direct Binary Deployment (No Package Manager Required)

If you prefer to run the standalone Go binary directly:

1. Copy the compiled `netgrip-linux-arm64` binary to the router:
   ```bash
   scp -O dist/packages/netgrip-linux-arm64 root@192.168.2.1:/usr/sbin/netgrip
   ```

2. Set executable permissions and start:
   ```bash
   ssh root@192.168.2.1
   chmod 755 /usr/sbin/netgrip
   /etc/init.d/netgrip restart
   ```

---

## 🖥️ Accessing NetGrip

Once installed, NetGrip is immediately available at:

- **Standalone Web Panel**: [http://192.168.2.1:8090](http://192.168.2.1:8090)
- **Embedded in LuCI**: Log in to [http://192.168.2.1](http://192.168.2.1) and select **Services &rarr; NetGrip**

> **Login Credentials:**
> - **Username:** `root`
> - **Password:** `root` *(or your custom router password)*

---

## ⚙️ Service Management & CLI Commands

Manage the NetGrip service on your router via SSH:

```bash
# Check if NetGrip is running
/etc/init.d/netgrip status

# Start NetGrip
/etc/init.d/netgrip start

# Stop NetGrip
/etc/init.d/netgrip stop

# Restart NetGrip
/etc/init.d/netgrip restart

# View NetGrip live logs
logread | grep netgrip

# Verify listening port (8090)
netstat -tulpn | grep 8090
```

---

## 🛠️ Building from Source

To compile the frontend and cross-compile the ARM64 binary locally:

### Prerequisites
- Node.js 22+ & npm
- Go 1.23+

### Build Steps

1. **Build the embedded React frontend:**
   ```bash
   cd app
   npm ci
   npm run build
   cd ..
   ```

2. **Cross-compile the ARM64 Linux binary:**
   ```bash
   CGO_ENABLED=0 GOOS=linux GOARCH=arm64 GOARM64=v8.0 go build \
     -trimpath \
     -buildvcs=false \
     -ldflags="-s -w -buildid= -X main.version=v1.0.0 -X github.com/gnacho/netgrip/internal/modules.releaseArch=arm64" \
     -o dist/packages/netgrip-linux-arm64 \
     ./cmd/netgrip
   ```

---

## 📄 License & Credits

- Based on [gnacho/netgrip](https://github.com/gnacho/netgrip) licensed under [AGPL-3.0](LICENSE).
- Firmware kernel and device integration based on [dexapro-on-lexapro/jio-ax6000-openwrt](https://github.com/dexapro-on-lexapro/jio-ax6000-openwrt).
- Custom Jidu AX6000 adaptation and maintenance by [@vinaykrofficial](https://github.com/vinaykrofficial).

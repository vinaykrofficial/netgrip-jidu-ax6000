package com.vinaykr.netgrip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinaykr.netgrip.data.model.*
import com.vinaykr.netgrip.data.repository.RouterRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class RouterUiState(
    val routerUrl: String = "192.168.2.1:8090",
    val username: String = "root",
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val board: BoardInfo = BoardInfo(),
    val system: SystemInfo = SystemInfo(),
    val wan: WanInfo = WanInfo(),
    val ethPorts: List<EthPort> = emptyList(),
    val clients: List<ClientDevice> = emptyList(),
    val wireless: List<WirelessRadio> = emptyList(),
    val wireguard: WireGuardStatus = WireGuardStatus(),
    val diagOutput: String? = null,
    val isDiagRunning: Boolean = false
)

class RouterViewModel(
    private val repository: RouterRepository = RouterRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouterUiState())
    val uiState: StateFlow<RouterUiState> = _uiState.asStateFlow()

    init {
        // Start background polling when logged in
        viewModelScope.launch {
            while (isActive) {
                if (_uiState.value.isLoggedIn) {
                    refreshAll(silent = true)
                }
                delay(5000)
            }
        }
    }

    fun updateRouterUrl(url: String) {
        _uiState.update { it.copy(routerUrl = url, errorMessage = null) }
    }

    fun updateUsername(name: String) {
        _uiState.update { it.copy(username = name, errorMessage = null) }
    }

    fun login(password: String) {
        val url = _uiState.value.routerUrl
        val user = _uiState.value.username

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(url, user, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoggedIn = true, isLoading = false) }
                refreshAll()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Connection failed"
                    )
                }
            }
        }
    }

    fun logout() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    fun refreshAll(silent: Boolean = false) {
        val url = _uiState.value.routerUrl
        viewModelScope.launch {
            if (!silent) _uiState.update { it.copy(isLoading = true) }

            val boardDeferred = async { repository.fetchBoard(url) }
            val sysDeferred = async { repository.fetchSystem(url) }
            val wanDeferred = async { repository.fetchWan(url) }
            val ethDeferred = async { repository.fetchEthPorts(url) }
            val clientsDeferred = async { repository.fetchClients(url) }
            val wifiDeferred = async { repository.fetchWireless(url) }
            val wgDeferred = async { repository.fetchWireGuard(url) }

            val boardRes = boardDeferred.await()
            val sysRes = sysDeferred.await()
            val wanRes = wanDeferred.await()
            val ethRes = ethDeferred.await()
            val clientsRes = clientsDeferred.await()
            val wifiRes = wifiDeferred.await()
            val wgRes = wgDeferred.await()

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    board = boardRes.getOrDefault(current.board),
                    system = sysRes.getOrDefault(current.system),
                    wan = wanRes.getOrDefault(current.wan),
                    ethPorts = ethRes.getOrDefault(current.ethPorts),
                    clients = clientsRes.getOrDefault(current.clients),
                    wireless = wifiRes.getOrDefault(current.wireless),
                    wireguard = wgRes.getOrDefault(current.wireguard)
                )
            }
        }
    }

    fun runDiagnostics(tool: String, host: String) {
        val url = _uiState.value.routerUrl
        viewModelScope.launch {
            _uiState.update { it.copy(isDiagRunning = true, diagOutput = "Running $tool on $host...") }
            val res = repository.runDiagnostics(url, tool, host)
            if (res.isSuccess) {
                _uiState.update { it.copy(isDiagRunning = false, diagOutput = res.getOrNull()?.output ?: "No output") }
            } else {
                _uiState.update {
                    it.copy(
                        isDiagRunning = false,
                        diagOutput = "Error: " + (res.exceptionOrNull()?.localizedMessage ?: "Command failed")
                    )
                }
            }
        }
    }

    fun rebootRouter(onComplete: (Boolean) -> Unit) {
        val url = _uiState.value.routerUrl
        viewModelScope.launch {
            val res = repository.reboot(url)
            onComplete(res.isSuccess)
        }
    }
}

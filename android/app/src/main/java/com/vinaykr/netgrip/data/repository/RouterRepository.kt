package com.vinaykr.netgrip.data.repository

import com.vinaykr.netgrip.data.api.ApiClient
import com.vinaykr.netgrip.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RouterRepository {

    suspend fun login(baseUrl: String, user: String, pass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            val response = service.login(LoginRequest(user, pass))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Login failed: HTTP ${response.code()} (Check credentials)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchBoard(baseUrl: String): Result<BoardInfo> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            Result.success(service.getBoard())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSystem(baseUrl: String): Result<SystemInfo> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            Result.success(service.getSystem())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchWan(baseUrl: String): Result<WanInfo> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            Result.success(service.getWan())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchEthPorts(baseUrl: String): Result<List<EthPort>> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            val res = service.getEthPorts()
            Result.success(res.ports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchClients(baseUrl: String): Result<List<ClientDevice>> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            val res = service.getClients()
            Result.success(res.clients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchWireless(baseUrl: String): Result<List<WirelessRadio>> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            Result.success(service.getWireless())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchWireGuard(baseUrl: String): Result<WireGuardStatus> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            Result.success(service.getWireGuard())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun runDiagnostics(baseUrl: String, tool: String, host: String): Result<DiagnosticsResult> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            Result.success(service.runDiagnostics(DiagnosticsRequest(tool, host)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reboot(baseUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            val res = service.rebootRouter()
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Reboot failed: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

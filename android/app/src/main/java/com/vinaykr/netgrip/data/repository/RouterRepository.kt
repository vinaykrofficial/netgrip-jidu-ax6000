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

    suspend fun runDiagnostics(baseUrl: String, tool: String, target: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(baseUrl)
            val request = when (tool) {
                "ping" -> DiagnosticsRequest(test = "ping", host = target.trim(), count = 4)
                "traceroute" -> DiagnosticsRequest(test = "traceroute", host = target.trim())
                "dns" -> DiagnosticsRequest(test = "dns", query = target.trim(), resolver = "1.1.1.1")
                else -> DiagnosticsRequest(test = tool, host = target.trim())
            }
            val responseBody = service.runDiagnostics(request)
            val rawJson = responseBody.string()
            val formatted = formatDiagnostics(tool, target, rawJson)
            Result.success(formatted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatDiagnostics(tool: String, target: String, json: String): String {
        return try {
            val parser = com.google.gson.JsonParser.parseString(json)
            if (!parser.isJsonObject) return json
            val obj = parser.asJsonObject

            when (tool) {
                "ping" -> {
                    val host = obj.get("host")?.asString ?: target
                    val sent = obj.get("sent")?.asInt ?: 4
                    val received = obj.get("received")?.asInt ?: 0
                    val loss = obj.get("loss_pct")?.asDouble ?: 0.0
                    val min = obj.get("min_ms")?.asDouble ?: 0.0
                    val avg = obj.get("avg_ms")?.asDouble ?: 0.0
                    val max = obj.get("max_ms")?.asDouble ?: 0.0

                    val sb = StringBuilder()
                    sb.appendLine("--- $host ping statistics ---")
                    sb.appendLine("$sent packets transmitted, $received received, ${String.format("%.1f", loss)}% packet loss")
                    sb.appendLine("round-trip min/avg/max = $min/$avg/$max ms\n")

                    val samples = obj.getAsJsonArray("samples")
                    samples?.forEach { s ->
                        val item = s.asJsonObject
                        val seq = item.get("seq")?.asInt ?: 0
                        val time = item.get("time_ms")?.asDouble ?: 0.0
                        sb.appendLine("64 bytes from $host: icmp_seq=$seq time=${String.format("%.2f", time)} ms")
                    }
                    sb.toString().trimEnd()
                }
                "traceroute" -> {
                    val host = obj.get("host")?.asString ?: target
                    val sb = StringBuilder()
                    sb.appendLine("traceroute to $host, 30 hops max:")
                    val hops = obj.getAsJsonArray("hops")
                    hops?.forEach { h ->
                        val item = h.asJsonObject
                        val raw = item.get("raw")?.asString
                        if (raw != null) {
                            sb.appendLine(raw)
                        } else {
                            val hopNum = item.get("hop")?.asInt ?: 0
                            val hopHost = item.get("host")?.asString ?: "*"
                            sb.appendLine(String.format("%2d  %s", hopNum, hopHost))
                        }
                    }
                    sb.toString().trimEnd()
                }
                "dns" -> {
                    val raw = obj.get("raw")?.asString
                    if (!raw.isNullOrBlank()) {
                        raw
                    } else {
                        val query = obj.get("query")?.asString ?: target
                        val resolver = obj.get("resolver")?.asString ?: "1.1.1.1"
                        val sb = StringBuilder()
                        sb.appendLine("DNS lookup for $query via $resolver:")
                        val answers = obj.getAsJsonArray("answers")
                        answers?.forEach { a ->
                            val item = a.asJsonObject
                            val name = item.get("name")?.asString ?: ""
                            val value = item.get("value")?.asString ?: ""
                            sb.appendLine("Name: $name  Address: $value")
                        }
                        sb.toString().trimEnd()
                    }
                }
                else -> json
            }
        } catch (e: Exception) {
            json
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

package com.vinaykr.netgrip.data.api

import com.vinaykr.netgrip.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NetGripApiService {

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<Unit>

    @GET("/api/me")
    suspend fun getMe(): Response<UserResponse>

    @POST("/api/logout")
    suspend fun logout(): Response<Unit>

    @GET("/api/board")
    suspend fun getBoard(): BoardInfo

    @GET("/api/system")
    suspend fun getSystem(): SystemInfo

    @GET("/api/wan")
    suspend fun getWan(): WanInfo

    @GET("/api/ethports")
    suspend fun getEthPorts(): EthPortsResponse

    @GET("/api/clients")
    suspend fun getClients(): ClientsResponse

    @GET("/api/wireless")
    suspend fun getWireless(): List<WirelessRadio>

    @GET("/api/wireguard")
    suspend fun getWireGuard(): WireGuardStatus

    @POST("/api/diagnostics")
    suspend fun runDiagnostics(@Body request: DiagnosticsRequest): DiagnosticsResult

    @POST("/api/reboot")
    suspend fun rebootRouter(): Response<Unit>
}

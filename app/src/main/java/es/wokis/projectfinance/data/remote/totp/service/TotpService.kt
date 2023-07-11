package es.wokis.projectfinance.data.remote.totp.service

import es.wokis.projectfinance.data.remote.invoice.dto.AcknowledgeDTO
import es.wokis.projectfinance.data.remote.totp.dto.TOTPResponseDTO
import retrofit2.http.DELETE
import retrofit2.http.POST

interface TotpService {
    @POST("/api/user/2fa")
    suspend fun activateTotp(): TOTPResponseDTO

    @DELETE("/api/user/2fa")
    suspend fun removeTotp(): AcknowledgeDTO
}
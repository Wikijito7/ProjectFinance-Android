package es.wokis.projectfinance.data.remote.totp.mapper

import es.wokis.projectfinance.data.bo.totp.TOTPRequiredBO
import es.wokis.projectfinance.data.bo.totp.TOTPResponseBO
import es.wokis.projectfinance.data.remote.totp.dto.TOTPRequestDTO
import es.wokis.projectfinance.data.remote.totp.dto.TOTPResponseDTO

fun TOTPRequestDTO.toBO() = TOTPRequiredBO(
    authType = authType,
    timestamp = timestamp
)

fun TOTPResponseDTO.toBO() = TOTPResponseBO(
    encodedSecret = encodedSecret,
    totpUrl = totpUrl,
    words = words
)
package es.wokis.projectfinance.data.response

import es.wokis.projectfinance.data.bo.totp.TOTPRequiredBO

sealed class ErrorType(val errorMessage: String) {
    class NoConnectionError(errorMessage: String) : ErrorType(errorMessage)
    open class ServerError(val httpCode: Int, errorMessage: String) : ErrorType(errorMessage)
    class DataParseError(errorMessage: String) : ErrorType(errorMessage)
    class UnknownError(val exception: Throwable, errorMessage: String) : ErrorType(errorMessage)

    class TOTPRequiredError(val totpRequired: TOTPRequiredBO, httpCode: Int, errorMessage: String) :
        ServerError(httpCode, errorMessage)

    open class CustomError(errorMessage: String) : ErrorType(errorMessage)
}
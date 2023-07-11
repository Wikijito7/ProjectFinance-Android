package es.wokis.projectfinance.data.error

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.remote.totp.dto.TOTPRequestDTO
import es.wokis.projectfinance.data.remote.totp.mapper.toBO
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.IllegalFormatException
import java.util.concurrent.CancellationException

class RepositoryErrorManager(private val sharedPreferences: SharedPreferences) {
    suspend fun <T> wrap(block: suspend () -> T): Flow<AsyncResult<T>> = flow {
        emit(AsyncResult.Loading())
        try {
            emit(AsyncResult.Success(block()))

        } catch (exc: CancellationException) {
            // If is a CancellationException, we throw it as it's necessary for the framework
            // in order to remove the coroutine.
            throw exc

        } catch (exc: Exception) {
            emit(manageError(exc))

        } finally {
            sharedPreferences.edit {
                putLong(AppConstants.TOTP_TIMESTAMP, 0)
                putString(AppConstants.TOTP, EMPTY_TEXT)
            }
        }
    }

    private fun <T> manageError(exc: Exception): AsyncResult.Error<T> = AsyncResult.Error(
        when (exc) {
            is HttpException -> {
                exc.response()?.errorBody()?.parseError<TOTPRequestDTO>()?.let {
                    ErrorType.TOTPRequiredError(it.toBO(), exc.code(), exc.message())

                } ?: ErrorType.ServerError(
                    exc.code(), exc.message()
                )
            }

            is SocketTimeoutException, is UnknownHostException, is ConnectException -> ErrorType.NoConnectionError(
                "No connection available"
            )

            is IllegalFormatException -> ErrorType.DataParseError("Error parsing data")

            else -> ErrorType.UnknownError(exc, "Unknown error")
        }
    )
}

inline fun <reified T> ResponseBody.parseError(): T? {
    val gson = Gson()
    return try {
        gson.fromJson(charStream(), T::class.java)

    } catch (exc: Exception) {
        null
    }
}
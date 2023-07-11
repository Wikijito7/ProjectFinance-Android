package es.wokis.projectfinance.data.response

sealed class AsyncResult<T>(val data: T?, val debugMessage: String?) {
    class Loading<T>(data: T? = null) : AsyncResult<T>(data, null)
    class Success<T>(data: T?) : AsyncResult<T>(data, null)
    class Error<T>(val error: ErrorType, data: T? = null) : AsyncResult<T>(data, error.errorMessage)
}

fun <T, R> AsyncResult<T>.map(transform: (T) -> R): AsyncResult<R> = when (this) {
    is AsyncResult.Error -> AsyncResult.Error(error, this.data?.let { transform(it) })
    is AsyncResult.Success -> AsyncResult.Success(this.data?.let { transform(it) })
    is AsyncResult.Loading -> AsyncResult.Loading(this.data?.let { transform(it) })
}
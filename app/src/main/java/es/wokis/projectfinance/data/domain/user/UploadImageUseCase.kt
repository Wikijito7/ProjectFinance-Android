package es.wokis.projectfinance.data.domain.user

import android.net.Uri
import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface UploadImageUseCase {
    suspend operator fun invoke(uri: Uri): Flow<AsyncResult<Boolean>>
}

class UploadImageUseCaseImpl(private val userRepository: UserRepository) : UploadImageUseCase {

    override suspend fun invoke(uri: Uri): Flow<AsyncResult<Boolean>> = userRepository.uploadImage(uri)

}
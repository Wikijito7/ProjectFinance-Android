package es.wokis.projectfinance.data.remote.user

import android.net.Uri
import es.wokis.projectfinance.data.bo.user.AuthUserBO
import es.wokis.projectfinance.data.bo.user.ChangePassRequestBO
import es.wokis.projectfinance.data.bo.user.UpdateUserBO
import es.wokis.projectfinance.data.bo.user.UserBO
import es.wokis.projectfinance.data.datasource.user.UserRemoteDataSource
import es.wokis.projectfinance.data.remote.user.dto.GoogleAuthDTO
import es.wokis.projectfinance.data.remote.user.dto.RecoverPassRequestDTO
import es.wokis.projectfinance.data.remote.user.mapper.toBO
import es.wokis.projectfinance.data.remote.user.mapper.toDTO
import es.wokis.projectfinance.data.remote.user.service.UserService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class UserRemoteDataSourceImpl(private val service: UserService) : UserRemoteDataSource {

    override suspend fun login(user: AuthUserBO): String {
        val token = service.login(user.toDTO())
        return token.authToken
    }

    override suspend fun register(user: AuthUserBO): String =
        service.register(user.toDTO()).authToken

    override suspend fun loginWithGoogle(authToken: String): String =
        service.loginWithGoogle(GoogleAuthDTO(authToken)).authToken

    override suspend fun getUser(): UserBO = service.getUser().toBO()

    override suspend fun uploadImage(uri: Uri): Boolean {
        val file = uri.path?.let { File(it) } ?: throw IllegalStateException("Path is null")
        val requestFile: RequestBody = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestFile)
        return service.uploadImage(multipartBody).acknowledge
    }

    override suspend fun updateUser(updateUser: UpdateUserBO): Boolean =
        service.updateUser(updateUser.toDTO()).acknowledge

    override suspend fun signOut(): Boolean = service.signOut().acknowledge

    override suspend fun closeAllSessions(): Boolean = service.closeAllSessions().acknowledge

    override suspend fun changePass(changePass: ChangePassRequestBO): Boolean =
        service.changePass(changePass.toDTO()).acknowledge

    override suspend fun recoverPass(changePass: ChangePassRequestBO): Boolean =
        service.recoverPass(changePass.toDTO()).acknowledge

    override suspend fun requestRecoverPass(email: String): Boolean =
        service.requestRecoverPass(RecoverPassRequestDTO(email)).acknowledge

    override suspend fun requestRecoverEmail(): Boolean {
        service.requestRecoverEmail()
        return true
    }

}
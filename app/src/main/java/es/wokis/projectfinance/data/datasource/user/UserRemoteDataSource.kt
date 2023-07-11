package es.wokis.projectfinance.data.datasource.user

import android.net.Uri
import es.wokis.projectfinance.data.bo.user.AuthUserBO
import es.wokis.projectfinance.data.bo.user.ChangePassRequestBO
import es.wokis.projectfinance.data.bo.user.UpdateUserBO
import es.wokis.projectfinance.data.bo.user.UserBO

interface UserRemoteDataSource {
    suspend fun login(user: AuthUserBO): String
    suspend fun register(user: AuthUserBO): String
    suspend fun loginWithGoogle(authToken: String): String
    suspend fun getUser(): UserBO
    suspend fun uploadImage(uri: Uri): Boolean
    suspend fun updateUser(updateUser: UpdateUserBO): Boolean
    suspend fun signOut(): Boolean
    suspend fun closeAllSessions(): Boolean
    suspend fun changePass(changePass: ChangePassRequestBO): Boolean
    suspend fun recoverPass(changePass: ChangePassRequestBO): Boolean
    suspend fun requestRecoverPass(email: String): Boolean
    suspend fun requestRecoverEmail(): Boolean
}
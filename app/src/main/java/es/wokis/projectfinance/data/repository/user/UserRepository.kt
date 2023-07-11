package es.wokis.projectfinance.data.repository.user

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import es.wokis.projectfinance.data.bo.user.AuthUserBO
import es.wokis.projectfinance.data.bo.user.BadgeBO
import es.wokis.projectfinance.data.bo.user.ChangePassRequestBO
import es.wokis.projectfinance.data.bo.user.UpdateUserBO
import es.wokis.projectfinance.data.bo.user.UserBO
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.constants.AppConstants.USER_BEARER_TOKEN
import es.wokis.projectfinance.data.constants.AppConstants.USER_ID
import es.wokis.projectfinance.data.datasource.user.UserRemoteDataSource
import es.wokis.projectfinance.data.error.RepositoryErrorManager
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(user: AuthUserBO): Flow<AsyncResult<Boolean>>
    suspend fun register(user: AuthUserBO): Flow<AsyncResult<Boolean>>
    suspend fun loginWithGoogle(authToken: String): Flow<AsyncResult<Boolean>>
    suspend fun getUser(): Flow<AsyncResult<UserBO>>
    suspend fun uploadImage(uri: Uri): Flow<AsyncResult<Boolean>>
    suspend fun updateUser(updateUser: UpdateUserBO): Flow<AsyncResult<Boolean>>
    suspend fun signOut(): Flow<AsyncResult<Boolean>>
    suspend fun closeAllSessions(): Flow<AsyncResult<Boolean>>
    suspend fun changeUserPass(oldPass: String, newPass: String): Flow<AsyncResult<Boolean>>
    suspend fun recoverUserPass(recoverCode: String, newPass: String): Flow<AsyncResult<Boolean>>
    suspend fun requestRecoverPass(email: String): Flow<AsyncResult<Boolean>>
    suspend fun requestVerificationEmail(): Flow<AsyncResult<Boolean>>
    fun isUserLoggedIn(): Boolean
    fun saveCurrentUserId(id: String): Boolean
    fun getCurrentUserId(): String?
    fun getBadges(): List<BadgeBO>
}

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val sharedPreferences: SharedPreferences,
    private val repositoryErrorManager: RepositoryErrorManager
) : UserRepository {

    private var userBadges: List<BadgeBO> = emptyList()

    override suspend fun login(user: AuthUserBO): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            val token = userRemoteDataSource.login(user)
            saveToken(token)
        }

    override suspend fun register(user: AuthUserBO): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            val token = userRemoteDataSource.register(user)
            saveToken(token)
        }

    override suspend fun loginWithGoogle(authToken: String): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            val token = userRemoteDataSource.loginWithGoogle(authToken)
            saveToken(token)
        }

    override suspend fun getUser(): Flow<AsyncResult<UserBO>> = repositoryErrorManager.wrap {
        userRemoteDataSource.getUser().also {
            userBadges = it.badges
        }
    }

    override suspend fun uploadImage(uri: Uri): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            userRemoteDataSource.uploadImage(uri)
        }

    override suspend fun updateUser(updateUser: UpdateUserBO): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            userRemoteDataSource.updateUser(updateUser)
        }

    override suspend fun signOut(): Flow<AsyncResult<Boolean>> = repositoryErrorManager.wrap {
        userRemoteDataSource.signOut().also {
            sharedPreferences.edit {
                putString(USER_BEARER_TOKEN, null)
            }
        }
    }

    override suspend fun closeAllSessions(): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            userRemoteDataSource.closeAllSessions().also {
                sharedPreferences.edit {
                    putString(USER_BEARER_TOKEN, null)
                }
            }
        }

    override suspend fun changeUserPass(
        oldPass: String,
        newPass: String
    ): Flow<AsyncResult<Boolean>> = repositoryErrorManager.wrap {
        userRemoteDataSource.changePass(
            ChangePassRequestBO(oldPass, newPass = newPass)
        )
    }

    override suspend fun recoverUserPass(
        recoverCode: String,
        newPass: String
    ): Flow<AsyncResult<Boolean>> = repositoryErrorManager.wrap {
        userRemoteDataSource.recoverPass(
            ChangePassRequestBO(recoverCode = recoverCode, newPass = newPass)
        )
    }

    override suspend fun requestRecoverPass(email: String): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            userRemoteDataSource.requestRecoverPass(email)
        }

    override suspend fun requestVerificationEmail(): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            userRemoteDataSource.requestRecoverEmail()
        }

    override fun isUserLoggedIn(): Boolean =
        sharedPreferences.getString(USER_BEARER_TOKEN, EMPTY_TEXT).isNullOrBlank().not()

    override fun saveCurrentUserId(id: String): Boolean = if (id.isNotBlank()) {
        sharedPreferences.edit {
            putString(USER_ID, id)
        }
        true

    } else {
        false
    }

    override fun getCurrentUserId(): String? =
        sharedPreferences.getString(USER_ID, EMPTY_TEXT).takeUnless { it.isNullOrBlank() }

    override fun getBadges(): List<BadgeBO> = userBadges


    private fun saveToken(token: String) = if (token.isNotEmpty()) {
        sharedPreferences.edit {
            putString(USER_BEARER_TOKEN, token)
        }
        true

    } else {
        false
    }

}
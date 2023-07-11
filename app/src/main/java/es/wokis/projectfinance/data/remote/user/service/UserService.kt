package es.wokis.projectfinance.data.remote.user.service

import es.wokis.projectfinance.data.remote.invoice.dto.AcknowledgeDTO
import es.wokis.projectfinance.data.remote.user.dto.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface UserService {
    @POST("/api/login")
    suspend fun login(@Body user: AuthUserDTO): AuthResponseDTO

    @POST("/api/register")
    suspend fun register(@Body user: AuthUserDTO): AuthResponseDTO

    @POST("/api/google-auth")
    suspend fun loginWithGoogle(@Body authToken: GoogleAuthDTO): AuthResponseDTO

    @GET("/api/user")
    suspend fun getUser(): UserDTO

    @Multipart
    @POST("/api/user/image")
    suspend fun uploadImage(@Part image: MultipartBody.Part): AcknowledgeDTO

    @PUT("/api/user")
    suspend fun updateUser(@Body user: UpdateUserDTO): AcknowledgeDTO

    @POST("/api/logout")
    suspend fun signOut(): AcknowledgeDTO

    @POST("/api/recover")
    suspend fun requestRecoverPass(@Body recover: RecoverPassRequestDTO): AcknowledgeDTO

    @POST("/api/change-pass")
    suspend fun changePass(@Body changePass: ChangePassRequestDTO): AcknowledgeDTO

    @POST("/api/recover-pass")
    suspend fun recoverPass(@Body changePass: ChangePassRequestDTO): AcknowledgeDTO

    @DELETE("/api/sessions")
    suspend fun closeAllSessions(): AcknowledgeDTO

    @POST("/api/verify")
    suspend fun requestRecoverEmail() : ResponseBody?
}
package es.wokis.projectfinance.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.wokis.projectfinance.data.domain.category.*
import es.wokis.projectfinance.data.domain.clear.ClearOtherUserDataUseCase
import es.wokis.projectfinance.data.domain.clear.ClearOtherUserDataUseCaseImpl
import es.wokis.projectfinance.data.domain.invoice.*
import es.wokis.projectfinance.data.domain.reaction.*
import es.wokis.projectfinance.data.domain.totp.ActivateTotpUseCase
import es.wokis.projectfinance.data.domain.totp.ActivateTotpUseCaseImpl
import es.wokis.projectfinance.data.domain.totp.RemoveTotpUseCase
import es.wokis.projectfinance.data.domain.totp.RemoveTotpUseCaseImpl
import es.wokis.projectfinance.data.domain.totp.SaveTotpCodeUseCase
import es.wokis.projectfinance.data.domain.totp.SaveTotpCodeUseCaseImpl
import es.wokis.projectfinance.data.domain.totp.SetTotpEnabledUseCase
import es.wokis.projectfinance.data.domain.totp.SetTotpEnabledUseCaseImpl
import es.wokis.projectfinance.data.domain.user.*
import es.wokis.projectfinance.data.repository.category.CategoryRepository
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import es.wokis.projectfinance.data.repository.reaction.ReactionRepository
import es.wokis.projectfinance.data.repository.totp.TotpRepository
import es.wokis.projectfinance.data.repository.user.UserRepository

@InstallIn(SingletonComponent::class)
@Module
class DomainModule {

    @Provides
    fun provideGetInvoiceUseCase(repository: InvoiceRepository): GetInvoiceUseCase =
        GetInvoiceUseCaseImpl(repository)

    @Provides
    fun provideGetLastInvoicesUseCase(repository: InvoiceRepository): GetLastInvoicesUseCase =
        GetLastInvoicesUseCaseImpl(repository)

    @Provides
    fun provideAddInvoiceUseCase(repository: InvoiceRepository): AddInvoiceUseCase =
        AddInvoiceUseCaseImpl(repository)

    @Provides
    fun provideRemoveInvoiceUseCase(repository: InvoiceRepository): RemoveInvoiceUseCase =
        RemoveInvoiceUseCaseImpl(repository)

    @Provides
    fun provideUpdateInvoiceUseCase(repository: InvoiceRepository): UpdateInvoiceUseCase =
        UpdateInvoiceUseCaseImpl(repository)

    @Provides
    fun provideGetInvoiceByIdUseCase(repository: InvoiceRepository): GetInvoiceByIdUseCase =
        GetInvoiceByIdUseCaseImpl(repository)

    @Provides
    fun provideGetInvoiceBetweenUseCase(repository: InvoiceRepository): GetInvoiceBetweenUseCase =
        GetInvoiceBetweenUseCaseImpl(repository)

    @Provides
    fun provideInsertRemovedInvoiceUseCase(repository: InvoiceRepository): InsertRemovedInvoiceUseCase =
        InsertRemovedInvoiceUseCaseImpl(repository)

    @Provides
    fun provideGetCategoriesUseCase(repository: CategoryRepository): GetCategoriesUseCase =
        GetCategoriesUseCaseImpl(repository)

    @Provides
    fun provideGetCategoryByIdUseCase(repository: CategoryRepository): GetCategoryByIdUseCase =
        GetCategoryByIdUseCaseImpl(repository)

    @Provides
    fun provideCreateCategoryUseCase(repository: CategoryRepository): CreateCategoryUseCase =
        CreateCategoryUseCaseImpl(repository)

    @Provides
    fun provideDeleteCategoryUseCase(repository: CategoryRepository): DeleteCategoryUseCase =
        DeleteCategoryUseCaseImpl(repository)

    @Provides
    fun provideUpdateCategoryUseCase(repository: CategoryRepository): UpdateCategoryUseCase =
        UpdateCategoryUseCaseImpl(repository)

    @Provides
    fun provideCheckCategoriesUseCase(repository: CategoryRepository): CheckCategoriesUseCase =
        CheckCategoriesUseCaseImpl(repository)

    @Provides
    fun provideInsertRemovedCategoryUseCase(repository: CategoryRepository): InsertRemovedCategoryUseCase =
        InsertRemovedCategoryUseCaseImpl(repository)

    @Provides
    fun provideCheckInvoicesWithoutCategoryUseCase(repository: InvoiceRepository): CheckInvoicesWithoutCategoryUseCase =
        CheckInvoicesWithoutCategoryUseCaseImpl(repository)

    @Provides
    fun provideGetFilteredInvoiceUseCase(repository: InvoiceRepository): GetFilteredInvoiceUseCase =
        GetFilteredInvoiceUseCaseImpl(repository)

    @Provides
    fun provideSaveFiltersUseCase(repository: InvoiceRepository): SaveFiltersUseCase =
        SaveFiltersUseCaseImpl(repository)

    @Provides
    fun provideClearFiltersUseCase(repository: InvoiceRepository): ClearFiltersUseCase =
        ClearFiltersUseCaseImpl(repository)

    @Provides
    fun provideLoginUseCase(repository: UserRepository): LoginUseCase = LoginUseCaseImpl(repository)

    @Provides
    fun provideRegisterUseCase(repository: UserRepository): RegisterUseCase =
        RegisterUseCaseImpl(repository)

    @Provides
    fun provideLoginWithGoogleUseCase(repository: UserRepository): LoginWithGoogleUseCase =
        LoginWithGoogleUseCaseImpl(repository)

    @Provides
    fun provideGetNumberOfInvoiceUseCase(repository: InvoiceRepository): GetNumberOfInvoiceUseCase =
        GetNumberOfInvoiceUseCaseImpl(repository)

    @Provides
    fun provideGetUserUseCase(repository: UserRepository): GetUserUseCase =
        GetUserUseCaseImpl(repository)

    @Provides
    fun provideSignOutUseCase(repository: UserRepository): SignOutUseCase =
        SignOutUseCaseImpl(repository)

    @Provides
    fun provideCloseAllSessionsUseCase(repository: UserRepository): CloseAllSessionsUseCase =
        CloseAllSessionsUseCaseImpl(repository)

    @Provides
    fun provideUploadImageUseCase(repository: UserRepository): UploadImageUseCase =
        UploadImageUseCaseImpl(repository)

    @Provides
    fun provideUpdateUserUseCase(repository: UserRepository): UpdateUserUseCase =
        UpdateUserUseCaseImpl(repository)

    @Provides
    fun provideUserLoggedInUseCase(repository: UserRepository): UserLoggedInUseCase =
        UserLoggedInUseCaseImpl(repository)

    @Provides
    fun provideSaveCurrentUserIdUseCase(repository: UserRepository): SaveCurrentUserIdUseCase =
        SaveCurrentUserIdUseCaseImpl(repository)

    @Provides
    fun provideGetCurrentUserIdUseCase(repository: UserRepository): GetCurrentUserIdUseCase =
        GetCurrentUserIdUseCaseImpl(repository)

    @Provides
    fun provideSynchronizeInvoicesUseCase(repository: InvoiceRepository): SynchronizeInvoicesUseCase =
        SynchronizeInvoicesUseCaseImpl(repository)

    @Provides
    fun provideAddReactionUseCase(repository: ReactionRepository): AddReactionUseCase =
        AddReactionUseCaseImpl(repository)

    @Provides
    fun provideRemoveReactionUseCase(repository: ReactionRepository): RemoveReactionUseCase =
        RemoveReactionUseCaseImpl(repository)

    @Provides
    fun provideCheckReactionsWithoutInvoiceUseCase(repository: ReactionRepository): CheckReactionsWithoutInvoiceUseCase =
        CheckReactionsWithoutInvoiceUseCaseImpl(repository)

    @Provides
    fun provideReinsertRemovedReactionUseCase(repository: ReactionRepository): ReinsertRemovedReactionUseCase =
        ReinsertRemovedReactionUseCaseImpl(repository)

    @Provides
    fun provideGetMostUsedReactionsUseCase(repository: ReactionRepository): GetMostUsedReactionsUseCase =
        GetMostUsedReactionsUseCaseImpl(repository)

    @Provides
    fun provideGetInvoicesOfOtherUserUseCase(repository: InvoiceRepository): GetInvoicesOfOtherUserUseCase =
        GetInvoicesOfOtherUserUseCaseImpl(repository)

    @Provides
    fun provideClearOtherUserDataUseCase(
        getInvoicesOfOtherUserUseCase: GetInvoicesOfOtherUserUseCase,
        deleteCategoryUseCase: DeleteCategoryUseCase,
        removeInvoiceUseCase: RemoveInvoiceUseCase,
        removeReactionUseCase: RemoveReactionUseCase
    ): ClearOtherUserDataUseCase = ClearOtherUserDataUseCaseImpl(
        getInvoicesOfOtherUserUseCase,
        deleteCategoryUseCase,
        removeInvoiceUseCase,
        removeReactionUseCase
    )

    @Provides
    fun provideSaveTotpCodeUseCase(sharedPreferences: SharedPreferences): SaveTotpCodeUseCase =
        SaveTotpCodeUseCaseImpl(sharedPreferences)

    @Provides
    fun provideSetTotpEnabledUseCase(sharedPreferences: SharedPreferences): SetTotpEnabledUseCase =
        SetTotpEnabledUseCaseImpl(sharedPreferences)

    @Provides
    fun provideGetBadgesUseCase(userRepository: UserRepository): GetBadgesUseCase =
        GetBadgesUseCaseImpl(userRepository)

    @Provides
    fun provideActivateTotpUseCase(totpRepository: TotpRepository): ActivateTotpUseCase =
        ActivateTotpUseCaseImpl(totpRepository)

    @Provides
    fun provideRemoveTotpUseCase(totpRepository: TotpRepository): RemoveTotpUseCase =
        RemoveTotpUseCaseImpl(totpRepository)

    @Provides
    fun provideRequestRecoverPassUseCase(repository: UserRepository): RequestRecoverPassUseCase =
        RequestRecoverPassUseCaseImpl(repository)

    @Provides
    fun provideChangeUserPassUseCase(repository: UserRepository): ChangeUserPassUseCase =
        ChangeUserPassUseCaseImpl(repository)

    @Provides
    fun provideRecoverUserPassUseCase(repository: UserRepository): RecoverUserPassUseCase =
        RecoverUserPassUseCaseImpl(repository)

    @Provides
    fun provideGetResendTimestampUseCase(sharedPreferences: SharedPreferences): GetResendTimestampUseCase =
        GetResendTimestampUseCaseImpl(sharedPreferences)

    @Provides
    fun provideSetResendTimestampUseCase(sharedPreferences: SharedPreferences): SetResendTimestampUseCase =
        SetResendTimestampUseCaseImpl(sharedPreferences)

    @Provides
    fun provideRequestVerificationEmailUseCase(repository: UserRepository): RequestVerificationEmailUseCase =
        RequestVerificationEmailUseCaseImpl(repository)
}
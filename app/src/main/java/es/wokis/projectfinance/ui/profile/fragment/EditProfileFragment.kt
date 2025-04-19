package es.wokis.projectfinance.ui.profile.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.user.UserBO
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import es.wokis.projectfinance.databinding.FragmentEditProfileBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.profile.filter.whiteSpaceFilter
import es.wokis.projectfinance.ui.profile.viewmodel.ProfileViewModel
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.showSnackBar

class EditProfileFragment : BaseFragment() {

    private var binding: FragmentEditProfileBinding? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            uri?.let {
                navigateTo(EditProfileFragmentDirections.actionEditProfileToCropImage(it))
            }
        }

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        setUpView()
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()
        setUpObservers()
        setUpFragmentResultListener()
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpView() {
        binding?.apply {
            editProfileInputUsername.filters = arrayOf(whiteSpaceFilter)
        }
    }

    private fun setUpFragmentResultListener() {
        setUpCropImageListener()
        setUpEditProfileTotpListener()
        setUpUpdateImageTotpListener()
    }

    private fun setUpUpdateImageTotpListener() {
        setFragmentResultListener(
            UPDATE_IMAGE_TOTP_KEY
        ) { _, _ ->
            viewModel.retryUploadImage()
        }
    }

    private fun setUpEditProfileTotpListener() {
        setFragmentResultListener(
            UPDATE_PROFILE_TOTP_KEY
        ) { _, _ ->
            updateUserInfo()
        }
    }

    private fun setUpCropImageListener() {
        setFragmentResultListener(RequestKeys.EDIT_PROFILE_TO_CROP) { _, bundle ->
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(CROPPED_IMAGE_URI, Uri::class.java)

            } else {
                bundle.getParcelable(CROPPED_IMAGE_URI)
            }
            uri?.let { imageUri ->
                viewModel.uploadImage(imageUri)
            }
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            editProfileBtnUserPicture.setOnClickListener {
                openImage()
            }

            editProfileBtnSave.setOnClickListener {
                updateUserInfo()
            }

            editProfileBtnGoBack.setOnClickListener {
                navigateBack()
            }

            editProfileContainerChangePass.setOnClickListener {
                navigateTo(EditProfileFragmentDirections.actionEditProfileToRecoverPass(false))
            }
        }
    }

    private fun updateUserInfo() {
        if (isAllValid()) {
            val username = getUsername()
            if (username == viewModel.username) {
                navigateBack()
                return
            }

            viewModel.updateUserInfo(
                username
            )
        }
    }

    private fun isAllValid(): Boolean {
        if (getUsername().trim().length < 3) {
            binding?.editProfileInputUsername?.error =
                getString(R.string.login__error_invalid_username)
            return false
        }
        return true
    }

    private fun openImage() {
        // Launch the photo picker and allow the user to choose only images.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun setUpObservers() {
        setUpUserInfoObserver()
        setUpUploadImageObserver()
        setUpUpdateUserObserver()
    }

    private fun setUpUpdateUserObserver() {
        viewModel.getUpdateUserLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    when (it.error) {
                        is ErrorType.TOTPRequiredError -> {
                            navigateTo(
                                EditProfileFragmentDirections.actionEditProfileToTotp(
                                    UPDATE_PROFILE_TOTP_KEY
                                )
                            )
                        }

                        else -> {
                            binding?.apply {
                                showSnackBar(
                                    R.string.edit_profile__update_user_error,
                                    view = editProfileBtnGoBack
                                )
                            }
                        }
                    }
                }

                is AsyncResult.Loading -> Unit

                is AsyncResult.Success -> {
                    navigateBack()
                }
            }
        }
    }

    private fun getUsername() = binding?.editProfileInputUsername?.text.toString()

    private fun setUpUploadImageObserver() {
        viewModel.getUploadImageLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    when (it.error) {
                        is ErrorType.TOTPRequiredError -> {
                            navigateTo(
                                EditProfileFragmentDirections.actionEditProfileToTotp(
                                    UPDATE_IMAGE_TOTP_KEY
                                )
                            )
                        }

                        else -> {
                            binding?.editProfileImgUserPicture?.let { view ->
                                showSnackBar(R.string.edit_profile__upload_image_error, view = view)
                            }
                        }
                    }

                }

                is AsyncResult.Loading -> Unit

                is AsyncResult.Success -> {
                    viewModel.getUserInfo()
                }
            }
        }
    }

    private fun setUpUserInfoObserver() {
        viewModel.getUserInfoLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    if (it.error is ErrorType.ServerError &&
                        it.error.httpCode == 401
                    ) {
                        viewModel.signOut()
                        navigateTo(ProfileFragmentDirections.actionProfileToLogin())
                    }

                }

                is AsyncResult.Loading -> Unit

                is AsyncResult.Success -> {
                    setUpUserInfo(it.data)
                }
            }
        }
    }

    private fun setUpUserInfo(data: UserBO?) {
        data?.let {
            binding?.apply {
                editProfileInputUsername.setText(it.username)
                editProfileLabelEmailDescription.text = it.email
                editProfileContainerChangePass.setVisible(it.loginWithGoogle.not())
                Glide
                    .with(root)
                    .load(it.image)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_user)
                    .into(editProfileImgUserPicture)
            }
        }
    }

    companion object {
        const val CROPPED_IMAGE_URI = "CROPPED_IMAGE_URI"
        private const val UPDATE_PROFILE_TOTP_KEY = "UPDATE_PROFILE_TOTP_KEY"
        private const val UPDATE_IMAGE_TOTP_KEY = "UPDATE_IMAGE_TOTP_KEY"
    }
}
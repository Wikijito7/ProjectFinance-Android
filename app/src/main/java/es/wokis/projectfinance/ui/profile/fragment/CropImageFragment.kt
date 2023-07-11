package es.wokis.projectfinance.ui.profile.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.FragmentCropImageBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.utils.hide

class CropImageFragment : BaseFragment() {

    private var binding: FragmentCropImageBinding? = null
    private val args: CropImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCropImageBinding.inflate(inflater, container, false)
        setUpView()
        setUpListeners()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
        hideAddInvoiceButton()
        hideBottomNavigation()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpListeners() {
        binding?.apply {
            cropImageBtnApply.setOnClickListener {
                cropImageViewImageCropper.croppedImageAsync(reqWidth = 512, reqHeight = 512)
            }

            cropImageBtnRotate.setOnClickListener {
                cropImageViewImageCropper.rotateImage(90)
            }

            cropImageViewImageCropper.setOnCropImageCompleteListener { _, result ->
                context?.let {
                    Log.d(
                        "CROP",
                        "Cropped image: isSuccessful: ${result.isSuccessful}, uri: ${result.uriContent}"
                    )
                    val bundle = Bundle().apply {
                        val path = result.getUriFilePath(it)
                        putParcelable(EditProfileFragment.CROPPED_IMAGE_URI, Uri.parse(path))
                    }
                    setFragmentResult(
                        RequestKeys.EDIT_PROFILE_TO_CROP,
                        bundle
                    )
                    navigateBack()
                }
            }

            cropImageViewImageCropper.setOnSetImageUriCompleteListener { _, _, error ->
                cropImageIncludeLoading.root.hide()
                Log.d("TAG", "error loading image: ${error?.stackTraceToString()}")
            }
        }
    }

    private fun setUpView() {
        binding?.apply {
            cropImageViewImageCropper.apply {
                setImageUriAsync(args.imageUri)
                setAspectRatio(1, 1)
            }
        }
    }
}
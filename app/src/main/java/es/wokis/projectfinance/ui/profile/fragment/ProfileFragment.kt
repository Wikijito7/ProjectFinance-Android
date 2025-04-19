package es.wokis.projectfinance.ui.profile.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.MobileNavigationDirections
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.user.BadgeBO
import es.wokis.projectfinance.data.bo.user.BadgeType
import es.wokis.projectfinance.data.bo.user.BadgeType.BETA_TESTER
import es.wokis.projectfinance.data.bo.user.BadgeType.EMAIL_VERIFIED
import es.wokis.projectfinance.data.bo.user.BadgeType.LEGACY
import es.wokis.projectfinance.data.bo.user.UserBO
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import es.wokis.projectfinance.databinding.FragmentProfileBinding
import es.wokis.projectfinance.databinding.RowBadgeBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.profile.viewmodel.ProfileViewModel
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.showSnackBar
import es.wokis.projectfinance.utils.toStringFormatted

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private val viewModel: ProfileViewModel by viewModels()

    private var binding: FragmentProfileBinding? = null

    override fun showInvoiceRemovedSnackBar() {
        // no-op
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isUserLoggedIn()) {
            getUserInfo()
            setUpObservers()

        } else {
            navigateTo(ProfileFragmentDirections.actionProfileToLogin())
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpListeners() {
        binding?.apply {
            profileLabelConfig.setOnClickListener {
                navigateTo(ProfileFragmentDirections.actionProfileToConfig())
            }

            profileLabelEditProfile.setOnClickListener {
                navigateTo(ProfileFragmentDirections.actionProfileToEditProfile())
            }

            profileLabelSignOut.setOnClickListener {
                onSignOut()
            }

            profileLabelContacts.setOnClickListener {
                Toast.makeText(context, "Esto irá a contactos", Toast.LENGTH_SHORT).show()
            }

            profileContainerBadges.setOnClickListener {
                navigateTo(ProfileFragmentDirections.actionProfileToBadges())
            }

            profileContainerError.setOnClickListener {
                navigateTo(ProfileFragmentDirections.actionProfileToVerification())
            }
        }
    }

    private fun onSignOut() {
        viewModel.signOut()
        binding?.root?.let {
            showSnackBar(
                R.string.login__sign_out_successfully,
                view = it,
                snackBarDuration = SNACK_BAR_DURATION
            )
        }
        navigateTo(MobileNavigationDirections.actionGlobalToHome())
    }

    private fun getUserInfo() {
        viewModel.getUserInfo()
    }

    private fun setUpObservers() {
        setUpNumberOfInvoicesObserver()
        setUpUserInfoObserver()
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
        binding?.apply {
            data?.let {
                profileLabelUsername.text = it.username
                profileLabelJoinedOn.text =
                    getString(R.string.profile__joined_on, it.createdOn.toStringFormatted())
                profileContainerError.setVisible(it.emailVerified.not())
                Glide
                    .with(root)
                    .load(it.image)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_user)
                    .into(profileImgUserPicture)
                setUpBadges(it.badges)
            }
        }
    }

    private fun setUpBadges(badges: List<BadgeBO>) {
        binding?.apply {
            profileContainerBadges.removeAllViews()
            badges.forEach {
                val badgeView = RowBadgeBinding.inflate(LayoutInflater.from(context)).apply {
                    rowBadgeImgIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            when (BadgeType.fromKey(it.id)) {
                                EMAIL_VERIFIED -> R.drawable.ic_email_verified
                                BETA_TESTER -> R.drawable.ic_beta
                                LEGACY -> R.drawable.ic_founder
                                null -> R.drawable.ic_bagde_error
                            },
                            context?.theme
                        )
                    )
                    rowBadgeImgIcon.background.setTint(Color.parseColor(it.color))
                }
                profileContainerBadges.addView(badgeView.root)
            }
        }
    }

    private fun setUpNumberOfInvoicesObserver() {
        viewModel.getNumberOfInvoicesLiveData().observe(viewLifecycleOwner) {
            binding?.profileLabelInvoicesCreated?.text =
                getString(R.string.profile__number_of_invoices, it)
        }
    }

    companion object {
        private const val SNACK_BAR_DURATION = 1500
    }
}
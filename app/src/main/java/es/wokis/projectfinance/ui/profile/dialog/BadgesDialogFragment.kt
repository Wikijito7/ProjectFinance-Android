package es.wokis.projectfinance.ui.profile.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.user.BadgeBO
import es.wokis.projectfinance.data.bo.user.BadgeType
import es.wokis.projectfinance.databinding.DialogBadgesDetailBinding
import es.wokis.projectfinance.databinding.RowBadgeDetailBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.profile.viewmodel.ProfileViewModel

class BadgesDialogFragment : BaseBottomSheetDialog() {

    private var binding: DialogBadgesDetailBinding? = null
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogBadgesDetailBinding.inflate(inflater, container, false)
        setUpDialog()
        forceExtendView()
        setUpListener()
        setUpView()
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpView() {
        binding?.apply {
            viewModel.getBadges().forEach {
                val badgeRow = RowBadgeDetailBinding.inflate(layoutInflater).apply {
                    rowBadgeDetailImgIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            getBadgeIcon(it),
                            context?.theme
                        )
                    )
                    rowBadgeDetailImgIcon.background.setTint(Color.parseColor(it.color))
                    rowBadgeDetailLabelBadgeTitle.text = getBadgeTitle(it)
                    rowBadgeDetailLabelBadgeDesc.text = getBadgeDescription(it)
                }
                badgeDetailContainerBadges.addView(badgeRow.root)
            }
        }
    }

    private fun getBadgeDescription(badge: BadgeBO): String = getString(
        when (BadgeType.fromKey(badge.id)) {
            BadgeType.EMAIL_VERIFIED -> R.string.badges__verified_desc
            BadgeType.BETA_TESTER -> R.string.badges__beta_desc
            BadgeType.LEGACY -> R.string.badges__legacy_desc
            null -> R.string.badges__error_desc
        }
    )

    private fun getBadgeTitle(badge: BadgeBO): String = getString(
        when (BadgeType.fromKey(badge.id)) {
            BadgeType.EMAIL_VERIFIED -> R.string.badges__verified
            BadgeType.BETA_TESTER -> R.string.badges__beta
            BadgeType.LEGACY -> R.string.badges__legacy
            null -> R.string.badges__error
        }
    )

    private fun getBadgeIcon(badge: BadgeBO) = when (BadgeType.fromKey(badge.id)) {
        BadgeType.EMAIL_VERIFIED -> R.drawable.ic_email_verified
        BadgeType.BETA_TESTER -> R.drawable.ic_beta
        BadgeType.LEGACY -> R.drawable.ic_founder
        null -> R.drawable.ic_bagde_error
    }

    private fun setUpListener() {
        binding?.apply {
            badgeDetailBtnClose.setOnClickListener {
                navigateBack()
            }
        }
    }
}
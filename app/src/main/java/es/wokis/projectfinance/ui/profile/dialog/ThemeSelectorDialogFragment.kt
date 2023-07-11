package es.wokis.projectfinance.ui.profile.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.data.enums.SelectedThemeEnum
import es.wokis.projectfinance.databinding.DialogThemeSelectorBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.profile.fragment.ConfigFragment
import es.wokis.projectfinance.ui.profile.viewmodel.ConfigViewModel

class ThemeSelectorDialogFragment : BaseBottomSheetDialog() {

    private var selectedTheme: SelectedThemeEnum = SelectedThemeEnum.SYSTEM
    private var binding: DialogThemeSelectorBinding? = null
    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogThemeSelectorBinding.inflate(inflater, container, false)
        setUpDialog()
        setUpListener()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSelectedTheme(viewModel.getSelectedTheme())
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpListener() {
        binding?.apply {
            themeSelectorBtnAccept.setOnClickListener {
                onAccept()
            }

            themeSelectorContainerDark.setOnClickListener {
                updateSelectedTheme(SelectedThemeEnum.DARK)
            }

            themeSelectorContainerLight.setOnClickListener {
                updateSelectedTheme(SelectedThemeEnum.LIGHT)
            }

            themeSelectorContainerSystemDefault.setOnClickListener {
                updateSelectedTheme(SelectedThemeEnum.SYSTEM)
            }

            themeSelectorBtnClose.setOnClickListener {
                navigateBack()
            }
        }
    }

    private fun updateSelectedTheme(theme: SelectedThemeEnum) {
        binding?.apply {
            themeSelectorRadioLight.isChecked = false
            themeSelectorRadioDark.isChecked = false
            themeSelectorRadioSystemDefault.isChecked = false
        }
        setUpSelectedTheme(theme)
    }

    private fun onAccept() {
        setFragmentResult(
            RequestKeys.CONFIG_TO_THEME_SELECTOR,
            bundleOf(ConfigFragment.SELECTED_THEME to selectedTheme.key)
        )
        navigateBack()
    }

    private fun setUpSelectedTheme(theme: SelectedThemeEnum) {
        binding?.apply {
            when (theme) {
                SelectedThemeEnum.LIGHT -> themeSelectorRadioLight.isChecked = true
                SelectedThemeEnum.DARK -> themeSelectorRadioDark.isChecked = true
                SelectedThemeEnum.SYSTEM -> themeSelectorRadioSystemDefault.isChecked = true
            }
            selectedTheme = theme
        }
    }
}
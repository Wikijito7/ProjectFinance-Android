package es.wokis.projectfinance.ui.category.dialog

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogColorPickerBinding
import es.wokis.projectfinance.ui.base.BaseDialogFragment
import es.wokis.projectfinance.ui.category.fragment.CreateCategoryDialogFragment.Companion.COLOR_KEY
import es.wokis.projectfinance.ui.category.viewmodel.ColorPickerViewModel


@AndroidEntryPoint
class ColorPickerFragment : BaseDialogFragment() {

    private val viewModel: ColorPickerViewModel by viewModels()
    private val args: ColorPickerFragmentArgs by navArgs()

    private var binding: DialogColorPickerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogColorPickerBinding.inflate(inflater, container, false)
        setUpListeners()
        setUpView()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        updateSliders()
    }

    private fun updateSliders() {
        binding?.apply {
            updateSlideBarValue { value ->
                value?.let {
                    colorPickerViewAlphaSlideBar.updateSelectorX(it)
                }
            }

            updateSlideBarValue { value ->
                value?.let {
                    colorPickerViewBrightnessSlideBar.updateSelectorX(it)
                }
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun setUpView() {
        binding?.apply {
            colorPickerViewPicker.setInitialColor(Color.parseColor(args.defaulColor))
            colorPickerViewPicker.attachAlphaSlider(colorPickerViewAlphaSlideBar)
            colorPickerViewPicker.attachBrightnessSlider(colorPickerViewBrightnessSlideBar)
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            colorPickerBtnClose.setOnClickListener {
                navigateBack()
            }

            colorPickerBtnAccept.setOnClickListener {
                returnInfo()
            }

            colorPickerViewPicker.setColorListener(object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    envelope?.let {
                        val color = "#${envelope.hexCode}"
                        binding?.colorPickerViewColorPreview?.setBackgroundColor(
                            Color.parseColor(
                                color
                            )
                        )
                        viewModel.lastColor = color
                        binding?.colorPickerBtnAccept?.isEnabled = true
                    }
                }
            })
        }
    }

    private fun returnInfo() {
        val bundle = Bundle().apply {
            putString(COLOR_KEY, viewModel.lastColor)
        }
        setFragmentResult(RequestKeys.CREATE_CATEGORY_TO_COLOR, bundle)
        navigateBack()
    }

    private fun updateSlideBarValue(block: (currentFloat: Int?) -> Unit) {
        val animator = ValueAnimator.ofInt(0, Integer.MAX_VALUE)
        animator.duration = 100
        animator.addUpdateListener {
            block(it.animatedValue as? Int)
        }
        animator.start()
    }
}
package com.example.qrcodescanner.View

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.LanguageList
import com.example.qrcodescanner.UIExtensions.getAll
import com.example.qrcodescanner.UIExtensions.getText
import com.example.qrcodescanner.databinding.DialogLanguagePickerBinding
import com.example.qrcodescanner.databinding.DialogQrDetailsBottomSheetBinding

class LanguageBottomSheetDialog(
    private val currentLanguage: LanguageList,
    private val availableLanguages: List<LanguageList>, // Передаем полный список
    private val onLanguageSelected: (LanguageList) -> Unit
) : BottomSheetDialogFragment(R.layout.dialog_language_picker) {

    private var _binding: DialogLanguagePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLanguagePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rgLanguages = binding.rgLanguages
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.primary)
        val textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)

        // Генерируем RadioButton для каждого языка из интерфейса
        availableLanguages.forEach { language ->
            val radioButton = RadioButton(requireContext()).apply {
                id = View.generateViewId()
                text = getString(language.getText())
                setTextColor(textColor)
                textSize = 15f
                buttonTintList = ColorStateList.valueOf(primaryColor)
                layoutParams = RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(48) // Высота кликабельной зоны
                )

                // Сохраняем ссылку на объект sealed interface прямо в тег кнопки
                tag = language

                // Отмечаем текущий выбранный язык
                isChecked = (language == currentLanguage)
            }

            rgLanguages.addView(radioButton)
        }

        // Слушатель клика
        rgLanguages.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
            val selectedLanguage = checkedRadioButton?.tag as? LanguageList

            selectedLanguage?.let {
                onLanguageSelected(it)
                dismiss()
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
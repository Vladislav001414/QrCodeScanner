package com.example.qrcodescanner.Fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.LanguageList
import com.example.qrcodescanner.SealedInterface.ThemeList
import com.example.qrcodescanner.UIExtensions.fromBtnId
import com.example.qrcodescanner.UIExtensions.fromIsoCode
import com.example.qrcodescanner.UIExtensions.fromMode
import com.example.qrcodescanner.UIExtensions.getAll
import com.example.qrcodescanner.UIExtensions.getBtnId
import com.example.qrcodescanner.UIExtensions.getIsoCode
import com.example.qrcodescanner.UIExtensions.getMode
import com.example.qrcodescanner.UIExtensions.getText
import com.example.qrcodescanner.View.LanguageBottomSheetDialog
import com.example.qrcodescanner.ViewModel.ProfileViewModel
import com.example.qrcodescanner.VmFactory.ProfileVMFactory
import com.example.qrcodescanner.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    lateinit var binding: FragmentProfileBinding

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toggleTheme.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = group.findViewById<View>(checkedId)
            if (isChecked && checkedButton.isPressed) {
                val mode = ThemeList.fromBtnId(checkedId).getMode()
                viewModel.updateTheme(mode)
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

        binding.btnSelectLanguage.setOnClickListener {
            val currentLangCode = viewModel.profileSettings.value?.language ?: "en"
            showLanguageDialog(currentLangCode)
        }


        setupVM()
    }

    fun setupVM(){
        val vmFactory = ProfileVMFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[ProfileViewModel::class.java]
        startObserving()
    }

    fun startObserving(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileSettings.collect { profile ->
                    val currentTheme = ThemeList.fromMode(profile?.themeMode)

                    binding.toggleTheme.check(currentTheme.getBtnId())

                    // Включаем слушатель обратно
                    val currentLanguage = LanguageList.fromIsoCode(profile?.language)
                    binding.tvCurrentLanguage.setText(currentLanguage.getText())
                }
            }
        }
    }

    private fun showLanguageDialog(currentLanguage: String) {


        val dialog = LanguageBottomSheetDialog(
            currentLanguage = LanguageList.fromIsoCode(currentLanguage),
            availableLanguages = LanguageList.getAll()
        ) { selectedLanguage ->

            viewModel.updateLanguage(selectedLanguage.getIsoCode())
            val appLocale = LocaleListCompat.forLanguageTags(selectedLanguage.getIsoCode())
            AppCompatDelegate.setApplicationLocales(appLocale)
        }


        dialog.show(childFragmentManager, "LanguageBottomSheet")
    }
}
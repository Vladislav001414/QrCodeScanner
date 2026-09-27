package com.example.qrcodescanner.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import com.example.qrcodescanner.Handler.QrFormHandler
import com.example.qrcodescanner.HandlerFactory.QrFormHandlerFactory
import com.example.qrcodescanner.QrLogicalClass.BarcodeGenerator
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.getFormId
import com.example.qrcodescanner.UIExtensions.showViewById
import com.example.qrcodescanner.ViewModel.QrCreatorAndInsertSharedVM
import com.example.qrcodescanner.ViewModel.QrCreatorViewModel
import com.example.qrcodescanner.ViewModel.QrInsertDataViewModel
import com.example.qrcodescanner.ViewModel.QrViewSharedVM
import com.example.qrcodescanner.VmFactory.QrCreationAndInsertSharedVMFactory
import com.example.qrcodescanner.VmFactory.QrCreatorVMFactory
import com.example.qrcodescanner.VmFactory.QrInsertDataVMFactory
import com.example.qrcodescanner.VmFactory.QrViewSharedVMFactory
import com.example.qrcodescanner.databinding.FragmentQrInsertDataBinding
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.launch

class QrInsertDataFragment : Fragment() {

    companion object {
        fun newInstance() = QrInsertDataFragment
    }

    private lateinit var viewModel: QrInsertDataViewModel
    private lateinit var sharedVM: QrCreatorAndInsertSharedVM
    private lateinit var sharedViewVM: QrViewSharedVM
    private lateinit var binding: FragmentQrInsertDataBinding

    private var currentHandler: QrFormHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrInsertDataBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupVM()

        // 2. Переключаем ViewFlipper на нужный дочерний View


        // 4. Обработка нажатия на кнопку "Создать QR"
        binding.btnGenerate.setOnClickListener {
            val qrInfo = currentHandler?.validateAndBuildPayload()?: return@setOnClickListener

            val qrItem = QrCodeItemTable(
                id = null,
                qrType = qrInfo.type ?: "TEXT",
                rawValue = qrInfo.rawValue ?: "",
                status = "Create",
                favorite = false,
                dateAdded = System.currentTimeMillis()
                )
            sharedViewVM.saveNewQrItem(qrItem)
            findNavController().navigate(R.id.qrViewFragment)
        }
    }

    private fun setupVM(){
        val qrInsertVMFactory = QrInsertDataVMFactory()
        val qrSharedVMFactory = QrCreationAndInsertSharedVMFactory()
        val qrViewSharedVMFactory = QrViewSharedVMFactory(requireContext())

        viewModel = ViewModelProvider(requireActivity(), qrInsertVMFactory)[QrInsertDataViewModel::class.java]
        sharedVM = ViewModelProvider(requireActivity(), qrSharedVMFactory)[QrCreatorAndInsertSharedVM::class.java]
        sharedViewVM = ViewModelProvider(requireActivity(), qrViewSharedVMFactory)[QrViewSharedVM::class.java]


        startObserve()
    }

    fun startObserve(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedVM.selectedType.collect { qrItemCreation ->
                    val targetView = binding.viewFlipper.showViewById(qrItemCreation.getFormId())

                    if (targetView != null) {
                        currentHandler = QrFormHandlerFactory.createHandler(qrItemCreation, targetView, parentFragmentManager)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentHandler = null
    }
}
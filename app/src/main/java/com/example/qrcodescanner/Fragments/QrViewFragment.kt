package com.example.qrcodescanner.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.qrcodescanner.Handler.TextFormatHandler
import com.example.qrcodescanner.QrLogicalClass.BarcodeGenerator
import com.example.qrcodescanner.UIExtensions.toFormattedDateString
import com.example.qrcodescanner.ViewModel.QrViewSharedVM
import com.example.qrcodescanner.VmFactory.QrViewSharedVMFactory
import com.example.qrcodescanner.databinding.FragmentQrViewBinding
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.launch

class QrViewFragment : Fragment() {

    companion object {
        fun newInstance() = QrViewFragment()
    }

    private lateinit var sharedVm: QrViewSharedVM

    private lateinit var binding: FragmentQrViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupVM()
    }

    private fun setupVM(){
        val viewVMFactory = QrViewSharedVMFactory(requireContext())
        sharedVm = ViewModelProvider(requireActivity(), viewVMFactory)[QrViewSharedVM::class.java]
        setupObserve()
    }

    private fun setupObserve(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedVm.qrSelectedItem.collect { qrViewItem ->

                    qrViewItem?.let {
                        val payload = qrViewItem.rawValue
                        val qrBitmap = BarcodeGenerator.generateBarcode(
                            payload,
                            BarcodeFormat.QR_CODE,
                            400,
                            400
                        )
                        val clearQrInfo =
                            TextFormatHandler.formatRawPayload(requireContext(), payload)
                        binding.ivQrCode.setImageBitmap(qrBitmap)
                        binding.tvQrType.text = qrViewItem.qrType
                        binding.tvQrContent.text = clearQrInfo
                        binding.tvCreatedDate.text = qrViewItem.dateAdded.toFormattedDateString()
                    }
                }
            }
        }
    }
}
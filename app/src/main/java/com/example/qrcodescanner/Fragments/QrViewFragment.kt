package com.example.qrcodescanner.Fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.qrcodescanner.Handler.TextFormatHandler
import com.example.qrcodescanner.QrLogicalClass.BarcodeGenerator
import com.example.qrcodescanner.QrLogicalClass.QrHelper
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.fromString
import com.example.qrcodescanner.UIExtensions.getText
import com.example.qrcodescanner.UIExtensions.toFormattedDateString
import com.example.qrcodescanner.View.QrDetailsBottomSheet
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

    private var currentQrBitmap: Bitmap? = null

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

        binding.btnFavorite.setOnClickListener {

            sharedVm.updateFavoriteStatus()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnViewDetails.setOnClickListener {
            val type = binding.tvQrType.text.toString()
            val content = binding.tvQrContent.text.toString()
            val date = binding.tvCreatedDate.text.toString()
            

            val bottomSheet = QrDetailsBottomSheet(type, content, date)
            bottomSheet.show(childFragmentManager, "QrDetailsBottomSheet")
        }
        binding.btnCopy.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("QR Content", binding.tvQrContent.text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(),R.string.copied_to_clipboard , Toast.LENGTH_SHORT).show()
        }



        binding.btnDownload.setOnClickListener {
            currentQrBitmap?.let { QrHelper.saveQrToGallery(requireContext(), it) }
        }


        binding.btnShare.setOnClickListener {
            currentQrBitmap?.let { QrHelper.shareQrCode(requireContext(), it) }
        }
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
                        currentQrBitmap = BarcodeGenerator.generateBarcode(
                            payload,
                            BarcodeFormat.QR_CODE,
                            400,
                            400
                        )
                        val clearQrInfo =
                            TextFormatHandler.formatRawPayload(requireContext(), payload)
                        binding.ivQrCode.setImageBitmap(currentQrBitmap)
                        val qrObject = QrItemCreation.fromString(qrViewItem.qrType)
                        binding.tvQrType.text = getString(qrObject.getText())
                        binding.tvQrContent.text = clearQrInfo
                        binding.tvCreatedDate.text = qrViewItem.dateAdded.toFormattedDateString()
                        binding.tvQrName.text = getString(qrObject.getText())

                        binding.btnFavorite.setImageResource(
                            if (qrViewItem.favorite){
                                R.drawable.baseline_star_24
                            }
                            else {
                                R.drawable.outline_star_border_24
                            }
                        )
                    }
                }
            }
        }
    }
}
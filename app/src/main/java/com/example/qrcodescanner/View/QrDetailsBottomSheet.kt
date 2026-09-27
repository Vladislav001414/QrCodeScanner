package com.example.qrcodescanner.View

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.qrcodescanner.R
import com.example.qrcodescanner.databinding.DialogQrDetailsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QrDetailsBottomSheet(
    private val qrType: String,
    private val qrContent: String,
    private val qrDate: String
) : BottomSheetDialogFragment() {



    private var _binding: DialogQrDetailsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogQrDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvSheetType.text = qrType
        binding.tvSheetContent.text = qrContent
        binding.tvSheetDate.text = qrDate


        binding.btnSheetClose.setOnClickListener {
            dismiss()
        }


        binding.btnSheetCopy.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("QR Content", qrContent)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(),R.string.copied_to_clipboard , Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
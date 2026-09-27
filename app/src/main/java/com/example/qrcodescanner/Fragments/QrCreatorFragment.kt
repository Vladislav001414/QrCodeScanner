package com.example.qrcodescanner.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.qrcodescanner.R
import com.example.qrcodescanner.RVAdapter.QrCreationAdapter
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.getAllItems
import com.example.qrcodescanner.ViewModel.QrCreatorAndInsertSharedVM
import com.example.qrcodescanner.VmFactory.QrCreatorVMFactory
import com.example.qrcodescanner.ViewModel.QrCreatorViewModel
import com.example.qrcodescanner.VmFactory.QrCreationAndInsertSharedVMFactory
import com.example.qrcodescanner.databinding.FragmentQrCreatorBinding

class QrCreatorFragment : Fragment() {

    companion object {
        fun newInstance() = QrCreatorFragment()
    }

    private lateinit var binding: FragmentQrCreatorBinding

    private lateinit var fragmentVM: QrCreatorViewModel
    private lateinit var sharedVM: QrCreatorAndInsertSharedVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrCreatorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVM()
        setupRV()
    }

    private fun setupVM(){
        val fragmentFactory = QrCreatorVMFactory()
        val sharedVMFactory = QrCreationAndInsertSharedVMFactory()

        fragmentVM = ViewModelProvider(requireActivity(), fragmentFactory)[QrCreatorViewModel::class.java]
        sharedVM = ViewModelProvider(requireActivity(), sharedVMFactory)[QrCreatorAndInsertSharedVM::class.java]

        startObserving()
    }

    private fun setupRV(){
        val adapter = QrCreationAdapter { selectedType ->
            sharedVM.selectQrType(selectedType)
            findNavController().navigate(R.id.qrCreationFragment)

        }

        binding.rvQrTypes.adapter = adapter

        adapter.submitList(QrItemCreation.getAllItems())
    }

    private fun startObserving(){

    }

}
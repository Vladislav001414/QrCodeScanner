package com.example.qrcodescanner.Fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.qrcodescanner.HandlerFactory.QrFormHandlerFactory
import com.example.qrcodescanner.R
import com.example.qrcodescanner.RVAdapter.QrCreationAdapter
import com.example.qrcodescanner.RVAdapter.QrHistoryAdapter
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.getAllItems
import com.example.qrcodescanner.UIExtensions.getFormId
import com.example.qrcodescanner.UIExtensions.showViewById
import com.example.qrcodescanner.ViewModel.QrCodeHistoryViewModel
import com.example.qrcodescanner.ViewModel.QrViewSharedVM
import com.example.qrcodescanner.VmFactory.QrCodeHistoryVMFactory
import com.example.qrcodescanner.VmFactory.QrViewSharedVMFactory
import com.example.qrcodescanner.databinding.FragmentQrCodeHistoryBinding
import com.example.qrcodescanner.enumClass.QrFilter
import kotlinx.coroutines.launch

class QrCodeHistoryFragment : Fragment() {

    companion object {
        fun newInstance() = QrCodeHistoryFragment()
    }



    private lateinit var binding: FragmentQrCodeHistoryBinding
    private lateinit var viewModel: QrCodeHistoryViewModel
    private lateinit var adapter: QrHistoryAdapter
    private lateinit var sharedVM: QrViewSharedVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrCodeHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipFavorite -> QrFilter.FAVORITE
                R.id.chipCreated -> QrFilter.CREATED
                R.id.chipScanned -> QrFilter.SCANNED
                else -> QrFilter.ALL
            }
            viewModel.setFilter(filter)
        }
        setupVM()
    }


    fun setupVM(){
        val vmFactory = QrCodeHistoryVMFactory(requireContext())
        val sharedVMFactory = QrViewSharedVMFactory(requireContext())

        viewModel = ViewModelProvider(requireActivity(), vmFactory)[QrCodeHistoryViewModel::class.java]
        sharedVM = ViewModelProvider(requireActivity(), sharedVMFactory)[QrViewSharedVM::class.java]
        setupRV()
        startObserving()
    }

    fun startObserving(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.qrHistoryList.collect { list->
                    adapter.submitList(list)
                    binding.tvEmptyState.isVisible = list.isEmpty()
                }
            }
        }
    }

    private fun setupRV(){
        adapter = QrHistoryAdapter(

            onItemClick =  { selectedItem ->
                if(selectedItem.id != null) {
                    sharedVM.getSelectedItem(selectedItem.id)
                    findNavController().navigate(R.id.qrViewFragment)
                }
            },
            onFavoriteClick = { item ->
                viewModel.toggleFavorite(item)
            }
        )

        binding.recyclerViewHistory.adapter = adapter
    }
}
package com.example.qrcodescanner.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.qrcodescanner.QrLogicalClass.IntentLauncher
import com.example.qrcodescanner.QrLogicalClass.QrCodeAnalyzer
import com.example.qrcodescanner.enumClass.QrType
import com.example.qrcodescanner.R
import com.example.qrcodescanner.VmFactory.ScannerFragmentVMFactory
import com.example.qrcodescanner.ViewModel.ScannerFragmentViewModel
import com.example.qrcodescanner.SealedInterface.BarcodeScanState
import com.example.qrcodescanner.SealedInterface.FlashState
import com.example.qrcodescanner.SealedInterface.QrAction
import com.example.qrcodescanner.databinding.FragmentScannerBinding
import com.example.qrcodescanner.UIExtensions.getCopyText
import com.example.qrcodescanner.UIExtensions.getIcon
import com.example.qrcodescanner.UIExtensions.getText
import eightbitlab.com.blurview.BlurTarget
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerFragment : Fragment() {

    companion object {
        fun newInstance() = ScannerFragment()
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private lateinit var binding: FragmentScannerBinding

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var fragmentVM: ScannerFragmentViewModel

    private lateinit var cameraController: LifecycleCameraController

    lateinit var qrCodeAnalyzer: QrCodeAnalyzer

    private var originalBrightness: Float = -1f

    private val intentLauncher : IntentLauncher by lazy { IntentLauncher(requireContext()) }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->

        if (uri != null) {

            qrCodeAnalyzer.analyzeStaticImage(uri)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraController = LifecycleCameraController(requireContext())
        cameraController.bindToLifecycle(this)
        qrCodeAnalyzer = QrCodeAnalyzer(requireActivity()) { qrResult ->
            fragmentVM.onQrCodeDetected(qrResult)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScannerBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val decorView = requireActivity().window.decorView
        val rootView = decorView.findViewById<BlurTarget>(R.id.blurTarget)
        val windowBackground = decorView.background




        binding.blurview.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(25f)
        binding.blurview.outlineProvider = ViewOutlineProvider.BACKGROUND;
        binding.blurview.clipToOutline = true;

        previewView = binding.cameraPreviewView

        cameraExecutor = Executors.newSingleThreadExecutor()

        setupVM()

        setupInfoCardView()

        setupUI()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun setupVM(){
        val factory = ScannerFragmentVMFactory(requireContext())

        fragmentVM = ViewModelProvider(requireActivity(), factory)[ScannerFragmentViewModel::class.java]

        startObserving()
    }

    private fun setupUI(){
        binding.btnFlashlight.setOnClickListener {
            fragmentVM.onFlashButtonClick()
        }
        binding.btnSwitchCamera.setOnClickListener {
            fragmentVM.switchCamera()
        }
        binding.btnGallery.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupInfoCardView(){
        binding.moreOptionsBtn.setOnClickListener {

            val materialTransition = TransitionSet().apply {

                ordering = TransitionSet.ORDERING_TOGETHER
                duration = 375


                interpolator = PathInterpolatorCompat.create(0.4f, 0.0f, 0.2f, 1.0f)


                addTransition(ChangeBounds())

                addTransition(Fade())
            }


            TransitionManager.beginDelayedTransition(binding.root, materialTransition)


            binding.shortQrInfoLayout.visibility = View.GONE
            binding.blurview.visibility = View.VISIBLE

            // МЕНЯЕМ РЕЖИМ ШИРИНЫ НА СТЕНУ (MATCH_PARENT)
            val params = binding.qrInfoCardView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.qrInfoCardView.layoutParams = params
        }


        binding.closeOptionsBtn.setOnClickListener {
            val materialTransition = TransitionSet().apply {

                ordering = TransitionSet.ORDERING_TOGETHER
                duration = 375


                interpolator = PathInterpolatorCompat.create(0.4f, 0.0f, 0.2f, 1.0f)


                addTransition(ChangeBounds())

                addTransition(Fade())
            }


            TransitionManager.beginDelayedTransition(binding.root, materialTransition)


            binding.blurview.visibility = View.GONE
            binding.shortQrInfoLayout.visibility = View.VISIBLE


            val params = binding.qrInfoCardView.layoutParams
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.qrInfoCardView.layoutParams = params
        }
    }

    private fun startObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentVM.qrCodeEvent.collect { event ->

                    when (event) {
                        is BarcodeScanState.Error -> {
                            Toast.makeText(
                                requireActivity(),
                                getString(event.stringResId),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is BarcodeScanState.Success -> {

                            val result = event.qrResult
                            var text = result.displayValue
                            fragmentVM.saveNewScan(result)
                            if(result.type == QrType.URL) {
                                text = URL(result.displayValue).host.removePrefix("www.")
                            }


                            binding.shortQrTypeTV.text = getString(result.type.stringResId) + ": "
                            binding.qrTypeTV.text = getString(result.type.stringResId)
                            binding.shortQrCodeTV.text = text
                            binding.qrCodeTV.text = result.displayValue
                            binding.shortQrInfoLayout.visibility = View.VISIBLE

                            binding.btnQrDataCopy.setOnClickListener {
                                IntentLauncher(requireActivity()).launchAction(QrAction.CopyData, result)
                            }
                            binding.btnQrShareData.setOnClickListener {
                                IntentLauncher(requireActivity()).launchAction(QrAction.ShareData, result)
                            }


                            if(result.type.primaryAction != null){
                                val primaryActionIcon = result.type.primaryAction.getIcon()
                                val primaryActionText = result.type.primaryAction.getText()
                                binding.btnQrAction1.visibility = View.VISIBLE
                                binding.btnAction1TV.visibility = View.VISIBLE
                                binding.btnQrAction1.setImageResource(primaryActionIcon)
                                binding.btnAction1TV.setText(primaryActionText)
                                binding.btnQrAction1.setOnClickListener {
                                    IntentLauncher(requireActivity()).launchAction(result.type.primaryAction, result)
                                }
                                binding.btnCopyTV.setText(result.type.primaryAction.getCopyText())
                            }

                            if(result.type.secondaryAction != null){
                                val secondaryActionIcon = result.type.secondaryAction.getIcon()
                                val secondaryActionText = result.type.secondaryAction.getText()
                                binding.btnQrAction2.visibility = View.VISIBLE
                                binding.btnAction2TV.visibility = View.VISIBLE
                                binding.btnQrAction2.setImageResource(secondaryActionIcon)
                                binding.btnAction2TV.setText(secondaryActionText)
                                binding.btnQrAction2.setOnClickListener {
                                    IntentLauncher(requireActivity()).launchAction(result.type.secondaryAction, result)
                                }
                            }
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentVM.flashIsWorking.collect { flashState->


                    when (flashState){
                        FlashState.Disabled -> {
                            setMaxScreenBrightness(false)
                            binding.btnFlashlight.setImageResource(R.drawable.outline_flash_off_24)
                            cameraController.enableTorch(false)
                            binding.scannerOverlay.updateScrimColor("#00000032")
                        }
                        FlashState.OnForward -> {
                            binding.btnFlashlight.setImageResource(R.drawable.outline_flash_on_24)
                            setMaxScreenBrightness(true)
                            binding.scannerOverlay.updateScrimColor("#FFFFFF")
                            cameraController.enableTorch(false)
                        }
                        FlashState.OnBackward -> {
                            setMaxScreenBrightness(false)
                            binding.btnFlashlight.setImageResource(R.drawable.outline_flash_on_24)
                            cameraController.enableTorch(true)
                            binding.scannerOverlay.updateScrimColor("#00000032")
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentVM.cameraSelector.collect { cameraSelector ->
                    cameraController.cameraSelector = cameraSelector

                }
            }
        }
    }



    private fun startCamera() {

        var isUserTouching = false
        var lastZoomTime = 0L


        binding.cameraPreviewView.controller = cameraController

        cameraController.zoomState.observe(viewLifecycleOwner) { zoomState ->
            val currentZoom: Float = zoomState.zoomRatio
            val maxZoom: Float = zoomState.maxZoomRatio
            val minZoom: Float = zoomState.minZoomRatio
            binding.seekBarZoom.max = (maxZoom * 100).toInt()
            binding.seekBarZoom.min = (minZoom * 100).toInt()
            if (!isUserTouching) {
                binding.seekBarZoom.progress = (currentZoom * 100).toInt()

            }
        }

        binding.seekBarZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean
            ) {

                if (p2) {
                    val currentTime = System.currentTimeMillis()


                    if (currentTime - lastZoomTime > 30) {
                        lastZoomTime = currentTime
                    }

                    val linearZoomValue = p1 / 100f

                    cameraController.setZoomRatio(linearZoomValue)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                isUserTouching = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                isUserTouching = false
            }

        })

        qrCodeAnalyzer.attachToCamera(cameraController, binding.scannerOverlay)
    }





    private fun setMaxScreenBrightness(enable: Boolean) {
        val window = requireActivity().window
        val layoutParams = window.attributes

        if (enable) {

            if (originalBrightness == -1f) {
                originalBrightness = layoutParams.screenBrightness
            }

            layoutParams.screenBrightness = 1.0f
        } else {

            if (originalBrightness != -1f) {
                layoutParams.screenBrightness = originalBrightness
                originalBrightness = -1f
            }
        }
        window.attributes = layoutParams
    }




    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(rc: Int, perms: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(rc, perms, results)
        if (rc == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireActivity(), getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }
}
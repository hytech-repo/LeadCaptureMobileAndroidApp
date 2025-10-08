package com.eva.lead.capture.ui.fragments.scanqr

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.FragmentEvaScanQrBinding
import com.eva.lead.capture.ui.base.BaseFragment
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EvaScanQRFragment :
    BaseFragment<FragmentEvaScanQrBinding, EvaScanQRViewModel>(EvaScanQRViewModel::class.java) {

    private lateinit var mContext: Context
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private var textValues: MutableList<String> = mutableListOf<String>()
    private var isFrontCamera = true
    private var lastScanTime = 0L
    private val scanCooldown = 2000L // 2 seconds
    private val barcodePattern = Regex(AppConstants.BARCODE_REGEX)

    private val cameraPermissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaScanQRFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaScanQrBinding {
        return FragmentEvaScanQrBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.init()
        this.initListener()
        this.checkCameraPermission()
    }

    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initListener() {
        binding.ivCameraIcon.setOnClickListener {
            isFrontCamera = !isFrontCamera
            val cameraSelector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera(cameraSelector)
        }

        binding.ivCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun checkCameraPermission() {
        if (!hasPermission(cameraPermissions)) {
            requestPermission(cameraPermissions, 10011)
        } else {
            this.startCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        }
    }

    override fun onPermissionResult(permission: Map<String, Boolean>, requestCode: Int) {
        if (requestCode == 10011) {
            if (permission[Manifest.permission.CAMERA] == true) {
                this.startCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            }
        }
    }

    private fun startCamera(cameraSelector: CameraSelector) {
        log.d(TAG, "starting camera isFrontCamera: $isFrontCamera")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mContext)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, { imageProxy ->
                        processBarcodeImage(imageProxy)
                    })
                }

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                log.e(TAG, "Use case binding failed: ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(mContext))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processBarcodeImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE) // only QR
                .build()
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (!barcodes.isNullOrEmpty()) {
//                        if (!isBarcodeDetected) {
                        processBarcode(barcodes)
//                        }
                    }
                }
                .addOnFailureListener { e ->
                    log.e(TAG, "Barcode scanning failed: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }

        } else {
            imageProxy.close()
        }
    }

    private fun processBarcode(barcodes: MutableList<Barcode>) {
        val now = System.currentTimeMillis()
        if (now - lastScanTime < scanCooldown) {
            return // Ignore this detection
        }
        lastScanTime = now

        for (barcode in barcodes) {
            val value = barcode.rawValue
            log.d(TAG, "detected QrCode: $value")
            if (value != null && barcodePattern.matches(value) && !textValues.contains(value)) {
                textValues.add(0, value)
                navigateToNextScreen(value)
            }
        }
    }

    private fun navigateToNextScreen(data: String) {
        val bundle = Bundle()
        bundle.putString("qr_info", data)
        val fragmentResult = requireArguments().getBoolean("send_fragment_result", false)
        if (fragmentResult) {
            parentFragmentManager.setFragmentResult("scan_result", bundle)
            findNavController().popBackStack()
        }
    }

    companion object {
        fun newInstance() = EvaScanQRFragment()
    }
}
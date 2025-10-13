package com.eva.lead.capture.ui.fragments.camera

/**
 *
 * Created by Laxmi Kant Joshi on 19/08/2025
 *
 * */

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.FragmentEvaCameraBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.getExternalFolderPath
import com.eva.lead.capture.utils.showToast
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EvaCameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EvaCameraFragment :
    BaseFragment<FragmentEvaCameraBinding, EvaCameraViewModel>(EvaCameraViewModel::class.java) {
    private lateinit var mContext: Context
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private var textValues: MutableList<String> = mutableListOf<String>()
    private var isFrontCamera = true
    private var lastScanTime = 0L
    private val scanCooldown = 2000L // 2 seconds
    private val barcodePattern = Regex(AppConstants.BARCODE_REGEX)
    private var mode: String = "manual"

    private val eventId by lazy {
        prefManager.get(AppConstants.EVENT_ID, "")
    }

    private val cameraPermissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaCameraFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaCameraBinding {
        return FragmentEvaCameraBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.init()
        this.initListener()
        this.checkCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
    }

    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        this.initBundle()
        this.initRepo()
        this.initObserver()
    }

    private fun initBundle() {
        if (arguments != null) {
            mode = arguments!!.getString("mode", "manual")
        }
    }

    private fun initRepo() {
    }

    private fun initObserver() {

    }

    private fun showLoader() {
        showProgressDialog(false)
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
        binding.captureButton.setOnClickListener {
            captureBusinessCardImage()
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

            imageCapture =
                ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

//            val imageAnalyzer = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, { imageProxy ->
//                        processBarcodeImage(imageProxy)
//                    })
//                }

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture/*, imageAnalyzer*/
                )
            } catch (exc: Exception) {
                log.e(TAG, "Use case binding failed: ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(mContext))
    }

    private fun captureBusinessCardImage() {
//        // Show loading indicator
//        showLoader()

        // Create file in internal storage
        val imageFile = mContext.getExternalFolderPath("clicked_image")
        if (!imageFile.exists()) {
            imageFile.mkdirs()
        }

        val fileName = "business_card_${System.currentTimeMillis()}.jpg"
        val outputFile = File(imageFile, fileName)

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    log.d(TAG, "Image saved successfully: ${outputFile.absolutePath}")

                    // Process the captured image
                    processImageFile(outputFile)
//                    processImage(outputFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    hideProgressDialog()
                    log.e(TAG, "Image capture failed: ${exception.message}")
//                    showError("Failed to capture image: ${exception.message}")
                }
            }
        )
    }

    private fun processImage(outputFile: File) {
        val greyBitmap = toGrayscale(outputFile)
        val conBitmap = enhanceContrast(greyBitmap)
//        val bitmap = rotateBitmap(conBitmap,)
    }

    fun toGrayscale(inputFile: File): Bitmap {
        val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
        val grayscale = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // 0 = grayscale
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayscale
    }

    fun enhanceContrast(bitmap: Bitmap): Bitmap {
        val contrast = 1.5f // Increase contrast by 50%
        val brightness = 0f
        val cm = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val ret = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return ret
    }

    fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun processImageFile(imageFile: File) {
        try {
            // Preprocess image
            val greyBitmap = toGrayscale(imageFile)
            val conBitmap = enhanceContrast(greyBitmap)
            val image = InputImage.fromBitmap(conBitmap, 0)

            // Process based on mode
            when (mode) {
                "qr" -> {
                    processQRCodeFromImage(image) { qrResults ->
                        val results = mutableMapOf<String, Any>()
                        if (qrResults.isNotEmpty()) {
                            results["qrCodes"] = qrResults
                        }
                        handleAllResults(results, imageFile)
                    }
                }

                "card" -> {
                    processTextFromImage(image) { textResults ->
                        val results = mutableMapOf<String, Any>()
                        if (textResults.isNotEmpty()) {
                            results["businessCardInfo"] = textResults
                        }
                        handleAllResults(results, imageFile)
                    }
                }

                "manual" -> {
                    val combinedResult = CapturedBusinessCardData(
                        imagePath = imageFile.absolutePath,
                        qrCodes = emptyList(),
                        businessCardInfo = emptyMap(),
                        timestamp = System.currentTimeMillis()
                    )
                    handleCombinedResults(combinedResult)
                }

                else -> {
                    log.e(TAG, "Invalid mode: $mode")
                    hideProgressDialog()
                }
            }

        } catch (e: Exception) {
            hideProgressDialog()
            log.e(TAG, "Error processing image: ${e.message}")
            mContext.showToast("Error processing image", ToastType.ERROR)
        }
    }

    private fun processQRCodeFromImage(image: InputImage, callback: (List<String>) -> Unit) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val qrResults = mutableListOf<String>()
                if (barcodes.isNotEmpty()) {
                    for (barcode in barcodes) {
                        val value = barcode.rawValue
                        if (value != null && barcodePattern.matches(value)) {
                            qrResults.add(value)
                            log.d(TAG, "Detected QR Code: $value")
                        }
                    }
                    callback(qrResults)
                } else {
                    mContext.showToast("Qr code not found", ToastType.ERROR)
                }
            }
            .addOnFailureListener { e ->
                log.e(TAG, "QR code scanning failed: ${e.message}")
                callback(emptyList())
            }
    }

    private fun processTextFromImage(image: InputImage, callback: (Map<String, String>) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                if (visionText.text.isNotEmpty()) {
                    val businessCardInfo = extractBusinessCardInfo(visionText.text, visionText)
                    log.d(TAG, "Detected text: ${visionText.text}")
                    callback(businessCardInfo)
                } else {
                    callback(emptyMap())
                }
            }
            .addOnFailureListener { e ->
                log.e(TAG, "Text recognition failed: ${e.message}")
                callback(emptyMap())
            }
    }

    private fun extractBusinessCardInfo(fullText: String, visionText: Text): Map<String, String> {
        val info = mutableMapOf<String, String>()
        val lines = fullText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        // Get all detected text blocks with their confidence and bounding box info
        val validTextBlocks = mutableListOf<TextBlockInfo>()

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text.trim()
                val boundingBox = line.boundingBox

                // Filter out likely icon/image artifacts
//                if (isValidTextLine(lineText, boundingBox)) {
//                    validTextBlocks.add(
//                        TextBlockInfo(
//                            text = lineText,
//                            boundingBox = boundingBox,
//                            confidence = line.confidence ?: 0f
//                        )
//                    )
//                }
                validTextBlocks.add(
                    TextBlockInfo(
                        text = lineText,
                        boundingBox = boundingBox,
                        confidence = line.confidence ?: 0f
                    )
                )
            }
        }

        // Sort by position (top to bottom, left to right)
        validTextBlocks.sortWith(compareBy({ it.boundingBox?.top }, { it.boundingBox?.left }))

        // Process the filtered text blocks
        processFilteredTextBlocks(validTextBlocks, info)

        return info
    }

    private fun processFilteredTextBlocks(
        textBlocks: List<TextBlockInfo>,
        info: MutableMap<String, String>
    ) {
        val processedTexts = textBlocks.map { it.text }

        // Extract specific information types
        extractAddress(textBlocks, info)
        extractContactInfo(textBlocks, info)
        extractBloodGroup(textBlocks, info)
        extractNameAndCompany(textBlocks, info)
        extractEmployeeId(textBlocks, info)
    }

    private fun extractContactInfo(
        textBlocks: List<TextBlockInfo>,
        info: MutableMap<String, String>
    ) {
        for (block in textBlocks) {
            val text = block.text

            // Email detection with better validation
            val emailPattern = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
            emailPattern.find(text)?.let { matchResult ->
                val email = matchResult.value.lowercase()
                if (isValidEmail(email)) {
                    info["email"] = email
                }
            }

            // Phone number detection with better validation
            if (isLikelyPhoneNumber(text)) {
                val phonePattern = Regex("""[\+]?[0-9\s\-\(\)\.]{10,}""")
                phonePattern.find(text)?.let { matchResult ->
                    val phone = matchResult.value.trim()
                    if (isValidPhoneFormat(phone)) {
                        info["phone"] = phone
                    }
                }
            }

            // Website detection
            val urlPattern =
                Regex("""(?i)(?:https?://)?(?:www\.)?[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\.[a-zA-Z]{2,}""")
            urlPattern.find(text)?.let { matchResult ->
                var website = matchResult.value.lowercase()
                if (!website.startsWith("http")) {
                    website = "https://$website"
                }
                info["website"] = website
            }
        }
    }

    private fun extractBloodGroup(
        textBlocks: List<TextBlockInfo>,
        info: MutableMap<String, String>
    ) {
        for (block in textBlocks) {
            val text = block.text

            // Blood group patterns
            val bloodGroupPattern = Regex("""(?i)blood\s*group\s*:?\s*([ABO][+-](?:ve)?)""")
            bloodGroupPattern.find(text)?.let { matchResult ->
                info["bloodGroup"] = matchResult.groupValues.getOrNull(1) ?: matchResult.value
                return  // Found it, exit
            }

            // Also check for standalone blood group (A+, B-, O+, etc.)
            val standalonePattern = Regex("""(?i)([ABO][+-](?:ve)?)""")
            if (text.lowercase().contains("blood")) {
                standalonePattern.find(text)?.let { matchResult ->
                    info["bloodGroup"] = matchResult.value
                    return
                }
            }
        }
    }

    private fun extractAddress(textBlocks: List<TextBlockInfo>, info: MutableMap<String, String>) {
        val addressKeywords = listOf(
            "sector", "block", "plot", "street", "road", "lane", "avenue",
            "noida", "delhi", "bangalore", "mumbai", "gurgaon", "gurugram",
            "india", "colony", "nagar", "phase"
        )

        // Find blocks that likely contain address information
        val addressBlocks = textBlocks.filter { block ->
            val text = block.text.lowercase()
            val hasKeyword = addressKeywords.any { text.contains(it) }
            val hasNumbers = block.text.any { it.isDigit() }
            val hasPincode = Regex("""\d{6}""").containsMatchIn(block.text)

            (hasKeyword && hasNumbers) || hasPincode
        }

        if (addressBlocks.isNotEmpty()) {
            // If multiple address blocks found, combine them or take the most complete one
            val fullAddress = if (addressBlocks.size == 1) {
                addressBlocks[0].text
            } else {
                // Take the longest one (most complete)
                addressBlocks.maxByOrNull { it.text.length }?.text ?: ""
            }

            if (fullAddress.isNotEmpty()) {
                info["address"] = fullAddress
            }
        }
    }

    private fun extractNameAndCompany(
        textBlocks: List<TextBlockInfo>,
        info: MutableMap<String, String>
    ) {
        // Filter out blocks that contain contact information
        val nonContactBlocks = textBlocks.filter { block ->
            val text = block.text
            !text.contains("@") &&
                    !isLikelyPhoneNumber(text) &&
                    !text.matches(Regex("""(?i)(?:www\.|https?://).*""")) &&
                    isLikelyTextualInfo(text)
        }

        if (nonContactBlocks.isEmpty()) return

        // Enhanced name detection
        if (!info.containsKey("name")) {
            val nameCandidate = findBestNameCandidate(nonContactBlocks)
            nameCandidate?.let { info["name"] = it }
        }

        // Enhanced company detection
        if (!info.containsKey("company")) {
            val companyCandidate = findBestCompanyCandidate(nonContactBlocks, info["name"])
            companyCandidate?.let { info["company"] = it }
        }
    }

    private fun isLikelyTextualInfo(text: String): Boolean {
        // Check if text looks like readable name/company info
        val letterCount = text.count { it.isLetter() }
        val totalLength = text.length

        // Should be mostly letters
        if (letterCount < totalLength * 0.6) return false

        // Should have reasonable length
        if (totalLength < 2 || totalLength > 50) return false

        // Should not be mostly uppercase (might be header/logo text)
        val upperCount = text.count { it.isUpperCase() }
        val lowerCount = text.count { it.isLowerCase() }
        if (upperCount > lowerCount * 2 && totalLength > 5) return false

        return true
    }

    private fun findBestNameCandidate(blocks: List<TextBlockInfo>): String? {
        // Look for text that looks like a person's name
        for (block in blocks) {
            val text = block.text

            // Name patterns
            if (isLikelyPersonName(text)) {
                return text
            }
        }

        // Fallback: return first valid textual block
        return blocks.firstOrNull()?.text
    }

    private fun findBestCompanyCandidate(
        blocks: List<TextBlockInfo>,
        personName: String?
    ): String? {
        // Look for company indicators
        val companyKeywords = listOf(
            "ltd", "llc", "inc", "corp", "company", "pvt", "private", "limited",
            "solutions", "services", "technologies", "tech", "systems",
            "enterprises", "consulting", "software", "development", "edutech"
        )

        // Patterns to EXCLUDE from company detection
        val excludePatterns = listOf(
            Regex("""(?i)blood\s*group"""),
            Regex("""(?i)id\s*:"""),
            Regex("""(?i)phone"""),
            Regex("""(?i)mobile"""),
            Regex("""(?i)emergency""")
        )

        for (block in blocks) {
            val text = block.text
            val lowerText = text.lowercase()

            // Skip if it's the person's name
            if (text.equals(personName, ignoreCase = true)) continue

            // Skip if it matches exclude patterns
            if (excludePatterns.any { pattern -> pattern.containsMatchIn(text) }) {
                continue
            }

            // Check for company keywords
            if (companyKeywords.any { keyword -> lowerText.contains(keyword) }) {
                return text
            }
        }

        // Fallback: find text that's different from the name,
        // longer than 5 chars, and doesn't match exclude patterns
        return blocks.find {
            val text = it.text
            !text.equals(personName, ignoreCase = true) &&
                    text.length > 5 &&
                    excludePatterns.none { pattern -> pattern.containsMatchIn(text) }
        }?.text
    }

    private fun isLikelyPersonName(text: String): Boolean {
        // Check if text looks like a person's name
        val words = text.split(" ").filter { it.isNotBlank() }

        // Should have 1-4 words
        if (words.size !in 1..4) return false

        // Each word should start with uppercase
        if (!words.all { word ->
                word.first().isUpperCase() && word.drop(1).all { it.isLowerCase() }
            }) {
            return false
        }

        // Should be mostly letters
        val letterCount = text.count { it.isLetter() }
        val totalCount = text.count { !it.isWhitespace() }

        return letterCount >= totalCount * 0.8
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                email.length > 5 &&
                email.count { it == '@' } == 1
    }

    private fun isValidPhoneFormat(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[^+0-9]"), "")
        return cleanPhone.length in 10..15
    }

    private fun extractEmployeeId(
        textBlocks: List<TextBlockInfo>,
        info: MutableMap<String, String>
    ) {
        for (block in textBlocks) {
            val text = block.text

            // More specific employee ID patterns
            val empIdPatterns = listOf(
                // Common formats: EMP123, ID:123, Employee ID: 123
                Regex("""(?i)(?:emp|employee)\s*(?:id|no|number)?\s*:?\s*([A-Za-z0-9]{2,10})"""),
                Regex("""(?i)id\s*:?\s*([A-Za-z0-9]{3,10})"""),
                // Alphanumeric codes that might be employee IDs
                Regex("""^[A-Za-z]{2,4}[0-9]{2,6}$"""),
                Regex("""^[0-9]{4,8}$""") // Pure numeric IDs
            )

            empIdPatterns.forEach { pattern ->
                pattern.find(text)?.let { matchResult ->
                    val empId = matchResult.groupValues.getOrNull(1) ?: matchResult.value
                    if (empId.length >= 2 && empId.length <= 10) {
                        info["empId"] = empId
                        return@forEach
                    }
                }
            }
        }
    }

    private fun isValidTextLine(text: String, boundingBox: android.graphics.Rect?): Boolean {
        // Filter out common icon artifacts and noise

        // 1. Check text length and content
        if (text.length < 2) return false

        // 2. Filter out single characters or symbols (likely from icons)
        if (text.length == 1 && !text.matches(Regex("[a-zA-Z0-9]"))) return false

        // 3. Filter out lines with mostly special characters (icon artifacts)
        val specialCharCount = text.count { !it.isLetterOrDigit() && !it.isWhitespace() }
        val totalChars = text.length
        if (specialCharCount > totalChars * 0.5 && totalChars > 2) return false

        // 4. Filter out common OCR artifacts from icons
        val iconArtifacts = listOf(
            "®", "©", "™", "℠", "§", "¶", "†", "‡", "•", "▪", "▫", "◦", "‣",
            "□", "■", "○", "●", "△", "▲", "▽", "▼", "◇", "◆", "☆", "★",
            "♠", "♣", "♥", "♦", "←", "→", "↑", "↓", "⇐", "⇒", "⇑", "⇓"
        )
        if (iconArtifacts.any { text.contains(it) } && text.length < 10) return false

        // 5. Filter out lines that are mostly numbers with special formatting (might be design elements)
        if (text.matches(Regex("^[0-9\\s\\-\\.\\(\\)\\+]+$")) && !isLikelyPhoneNumber(text)) {
            return false
        }

        // 6. Check aspect ratio of bounding box (very wide or very tall might be design elements)
        boundingBox?.let { box ->
            val width = box.width()
            val height = box.height()
            if (width > 0 && height > 0) {
                val aspectRatio = width.toFloat() / height.toFloat()
                // Filter out very wide or very tall elements (likely decorative)
                if (aspectRatio > 10 || aspectRatio < 0.1) return false
            }
        }

        return true
    }

    private fun isLikelyPhoneNumber(text: String): Boolean {
        val cleanText = text.replace(Regex("[^+0-9]"), "")
        return cleanText.length >= 10 && cleanText.length <= 15
    }

//    private fun extractBusinessCardInfo(fullText: String, visionText: Text): Map<String, String> {
//        val info = mutableMapOf<String, String>()
//        val lines = fullText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
//
//        // Use both line-by-line analysis and the structured Text object
//        for (block in visionText.textBlocks) {
//            for (line in block.lines) {
//                val lineText = line.text.trim()
//
//                // Email detection
//                if (lineText.contains("@") &&
//                    android.util.Patterns.EMAIL_ADDRESS.matcher(lineText).find()) {
//                    val emailMatch = android.util.Patterns.EMAIL_ADDRESS.matcher(lineText)
//                    if (emailMatch.find()) {
//                        info["email"] = emailMatch.group()
//                    }
//                }
//
//                // Phone number detection
//                val phonePattern = Regex("""[\+]?[0-9\s\-\(\)]{10,}""")
//                phonePattern.find(lineText)?.let { matchResult ->
//                    val phone = matchResult.value.replace(Regex("[^+0-9]"), "")
//                    if (phone.length >= 10) {
//                        info["phone"] = matchResult.value
//                    }
//                }
//
//                // Employee ID detection (customize as per your format)
//                val empIdPatterns = listOf(
//                    Regex("""(?i)emp\s*(?:id|no)?\s*:?\s*([A-Za-z0-9]+)"""),
//                    Regex("""(?i)employee\s*(?:id|no)?\s*:?\s*([A-Za-z0-9]+)"""),
//                    Regex("""[A-Za-z]{2,4}\d{3,6}""") // Common format like ABC123, EMP1234
//                )
//
//                empIdPatterns.forEach { pattern ->
//                    pattern.find(lineText)?.let { matchResult ->
//                        info["empId"] = matchResult.groupValues.getOrNull(1) ?: matchResult.value
//                    }
//                }
//
//                // Website detection
//                val urlPattern = android.util.Patterns.WEB_URL
//                if (urlPattern.matcher(lineText).find()) {
//                    val urlMatch = urlPattern.matcher(lineText)
//                    if (urlMatch.find()) {
//                        info["website"] = urlMatch.group()
//                    }
//                }
//            }
//        }
//
//        // Extract name and company (usually first few lines)
//        val filteredLines = lines.filter { line ->
//            !line.contains("@") &&
//                    !android.util.Patterns.PHONE.matcher(line).find() &&
//                    !android.util.Patterns.WEB_URL.matcher(line).find() &&
//                    line.length > 2
//        }
//
//        if (filteredLines.isNotEmpty() && !info.containsKey("name")) {
//            info["name"] = filteredLines[0]
//        }
//
//        if (filteredLines.size > 1 && !info.containsKey("company")) {
//            // Look for company indicators
//            val companyLine = filteredLines.find {
//                it.contains(Regex("(?i)(ltd|llc|inc|corp|company|pvt|private|limited)"))
//            } ?: filteredLines[1]
//            info["company"] = companyLine
//        }
//
//        return info
//    }

    private fun handleAllResults(results: Map<String, Any>, imageFile: File) {
        hideProgressDialog()

        val qrCodes = results["qrCodes"] as? List<String> ?: emptyList()
        val businessCardInfo = results["businessCardInfo"] as? Map<String, String> ?: emptyMap()

        if (qrCodes.isNotEmpty() || businessCardInfo.isNotEmpty()) {
            log.d(TAG, "Processing completed successfully")

            // Create combined result
            val combinedResult = CapturedBusinessCardData(
                imagePath = imageFile.absolutePath,
                qrCodes = qrCodes,
                businessCardInfo = businessCardInfo,
                timestamp = System.currentTimeMillis()
            )

            // Handle the results
            handleCombinedResults(combinedResult)

        } else {
            log.d(TAG, "No QR codes or business card information detected")
//            showError("No readable information found in the image. Please try again with better lighting.")

            // Optionally delete the image if no information was found
            // imageFile.delete()
        }
    }

    private fun handleCombinedResults(data: CapturedBusinessCardData) {
        log.d(TAG, "Combined Results:")
        log.d(TAG, "Image saved at: ${data.imagePath}")

        if (data.qrCodes.isNotEmpty()) {
            log.d(TAG, "QR Codes found: ${data.qrCodes}")
        }

        if (data.businessCardInfo.isNotEmpty()) {
            log.d(TAG, "Business Card Info:")
            data.businessCardInfo.forEach { (key, value) ->
                log.d(TAG, "$key: $value")
            }
        }

        // You can now:
        // 1. Save to database with image path
        // 2. Navigate to result screen
        // 3. Show results dialog
        // 4. Pass results back to parent

        navigateToNextScreen(data)

//        print(data)
    }

    private fun navigateToNextScreen(data: CapturedBusinessCardData) {
        val bundle = Bundle()
        bundle.putParcelable("user_info", data)
        val fragmentResult = requireArguments().getBoolean("send_fragment_result", false)
        if (fragmentResult) {
            parentFragmentManager.setFragmentResult("scan_result", bundle)
            findNavController().popBackStack()
        } else {
            findNavController().navigate(R.id.action_evaCameraFragment_to_evaAddManualLead, bundle)
        }
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

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EvaCameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EvaCameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
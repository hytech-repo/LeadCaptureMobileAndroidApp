package com.eva.lead.capture.ui.fragments.camera

data class CapturedBusinessCardData(
    val imagePath: String,
    val qrCodes: List<String>,
    val businessCardInfo: Map<String, String>,
    val timestamp: Long
)


data class TextBlockInfo(
    val text: String,
    val boundingBox: android.graphics.Rect?,
    val confidence: Float
)


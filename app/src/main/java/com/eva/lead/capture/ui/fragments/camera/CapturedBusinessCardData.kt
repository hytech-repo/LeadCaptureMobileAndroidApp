package com.eva.lead.capture.ui.fragments.camera

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CapturedBusinessCardData(
    val imagePath: String,
    val qrCodes: List<String>,
    val businessCardInfo: Map<String, String>,
    val timestamp: Long
): Parcelable


data class TextBlockInfo(
    val text: String,
    val boundingBox: android.graphics.Rect?,
    val confidence: Float
)


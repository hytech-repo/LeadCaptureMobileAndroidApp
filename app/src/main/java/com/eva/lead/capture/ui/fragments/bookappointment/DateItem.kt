package com.eva.lead.capture.ui.fragments.bookappointment

data class DateItem(
    val fullDate: String,
    val dayNumber: String,
    val dayName: String,
    var isSelected: Boolean = false
)
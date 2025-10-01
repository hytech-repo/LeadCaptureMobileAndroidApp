package com.eva.lead.capture.domain.model


data class FAQItemList(
    val faqs: List<FAQHelpItem>
)

data class FAQHelpItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)

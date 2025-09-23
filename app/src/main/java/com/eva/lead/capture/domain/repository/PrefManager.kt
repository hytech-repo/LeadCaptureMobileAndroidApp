package com.eva.lead.capture.domain.repository

interface PrefManager {
    fun <T> put(key: String, value: T)

    fun <T> get(key: String, defaultValue: T): T

    fun remove(key: String)

    fun clear()
}
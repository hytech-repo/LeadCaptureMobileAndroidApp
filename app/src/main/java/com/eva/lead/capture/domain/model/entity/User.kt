package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.collections.joinToString
import kotlin.let

@Entity(tableName = "user", indices = [Index(value = ["userId"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo("userId")
    val userId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var jobTitle: String? = null,
    var organization: String? = null,
    var street: String? = null,
    var postalCode: String? = null,
    var city: String? = null,
    var state: String? = null,
    var country: String? = null,
    val badgeType: String?,
    val checkInStatus: Int?,
    var checkInDate: String? = null,
    val status: String?,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var lastSync: Long? = null
)


fun User.generateUpdateQuery(): String {
    val updates = mutableListOf<String>()

    this.firstName?.let { updates.add("firstName = '$it'") }
    this.lastName?.let { updates.add("lastName = '$it'") }
    this.email?.let { updates.add("email = '$it'") }
    this.phone?.let { updates.add("phone = '$it'") }
    this.jobTitle?.let { updates.add("jobTitle = '$it'") }
    this.organization?.let { updates.add("organization = '$it'") }
    this.street?.let { updates.add("street = '$it'") }
    this.postalCode?.let { updates.add("postalCode = '$it'") }
    this.city?.let { updates.add("city = '$it'") }
    this.state?.let { updates.add("state = '$it'") }
    this.country?.let { updates.add("country = '$it'") }
    this.badgeType?.let { updates.add("badgeType = '$it'") }
    this.checkInStatus?.let { updates.add("checkInStatus = $it") }
    this.checkInDate?.let { updates.add("checkInDate = '$it'") }
    this.status?.let { updates.add("status = '$it'") }
    this.updatedAt?.let { updates.add("updatedAt = '$it'") }
    this.lastSync?.let { updates.add("lastSync = $it") }

    if (updates.isEmpty()) return ""

    // Build the SET clause for the query
    val setClause = updates.joinToString(", ")
    return "update eva_attendees SET $setClause WHERE userId = '${this.userId}'"
}
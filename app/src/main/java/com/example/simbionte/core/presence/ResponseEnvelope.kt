package com.example.simbionte.core.presence

data class ResponseEnvelope(
    val opening: String?,
    val body: String
) {
    fun toDisplayString(): String = if (opening != null) "$opening $body" else body
}

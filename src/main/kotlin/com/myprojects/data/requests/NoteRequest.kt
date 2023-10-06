package com.myprojects.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val title: String,
    val text: String,
    val modifiedDateTime: String
)
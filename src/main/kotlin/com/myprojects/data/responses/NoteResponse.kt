package com.myprojects.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse(
    val title: String,
    val text: String,
    val id: String
)
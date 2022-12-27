package com.cheezycode.notesample.models

data class NoteResponse(
    val id: String,
    val created_at: String,
    val body: String,
    val title: String,
    val updated_at: String,
)
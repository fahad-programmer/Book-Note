package com.cheezycode.notesample.models

data class UserCreate(
    val password: String,
    val username: String,
    val email:String
)
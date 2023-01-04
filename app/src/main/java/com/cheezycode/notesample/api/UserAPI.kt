package com.cheezycode.notesample.api

import com.cheezycode.notesample.models.UserCreate
import com.cheezycode.notesample.models.UserRequest
import com.cheezycode.notesample.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("api/users/")
    suspend fun signup(@Body userRequest: UserCreate) : Response<UserResponse>

    @POST("api/users/login/")
    suspend fun signin(@Body userRequest: UserRequest) : Response<UserResponse>

}
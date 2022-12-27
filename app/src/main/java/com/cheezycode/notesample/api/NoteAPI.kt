package com.cheezycode.notesample.api

import com.cheezycode.notesample.models.NoteRequest
import com.cheezycode.notesample.models.NoteResponse
import retrofit2.Response
import retrofit2.http.*

interface NoteAPI {

    @POST("api/notes/")
    suspend fun createNote(@Body noteRequest: NoteRequest): Response<NoteResponse>

    @GET("api/notes/")
    suspend fun getNotes(): Response<List<NoteResponse>>

    @DELETE("api/notes/{id}/")
    suspend fun deleteNote(@Path("id") id: String) : Response<NoteResponse>

    @PUT("api/notes/{id}/")
    suspend fun updateNote(
        @Path("id") id: String,
        @Body noteRequest: NoteRequest
    ): Response<NoteResponse>
}
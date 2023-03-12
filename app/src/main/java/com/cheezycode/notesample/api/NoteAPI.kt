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

    @GET("api/search/{term}")
    suspend fun searchNotes(@Path("term") term:String): Response<List<NoteResponse>>

    @POST("api/notes/{id}/trash/")
    suspend fun deleteNote(@Path("id") id: String): Response<Void>

    @GET("api/trash")
    suspend fun trashView(): Response<List<NoteResponse>>

    @PUT("api/notes/{id}/")
    suspend fun updateNote(
        @Path("id") id: String,
        @Body noteRequest: NoteRequest
    ): Response<NoteResponse>
}
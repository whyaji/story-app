package com.whyaji.storyapp.data.remote.network

import com.whyaji.storyapp.data.remote.response.AddNewStoryResponse
import com.whyaji.storyapp.data.remote.response.GetAllStoryResponse
import com.whyaji.storyapp.data.remote.response.LoginResponse
import com.whyaji.storyapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : GetAllStoryResponse

    @GET("stories")
    suspend fun getAllStories(
        @Query("location") location: Int,
    ) : GetAllStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): AddNewStoryResponse
}
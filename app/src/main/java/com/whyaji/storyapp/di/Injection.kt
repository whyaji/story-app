package com.whyaji.storyapp.di

import android.content.Context
import com.whyaji.storyapp.data.local.StoryDatabase
import com.whyaji.storyapp.data.local.StoryRepository
import com.whyaji.storyapp.data.remote.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(context)
        return StoryRepository(database, apiService)
    }
}
package com.whyaji.storyapp.ui.story.create

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.whyaji.storyapp.data.local.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun addNewStory(file: MultipartBody.Part, description: RequestBody, latLng: LatLng?) = storyRepository.addNewStory(file, description, latLng)
}
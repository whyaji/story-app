package com.whyaji.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import com.whyaji.storyapp.data.local.StoryRepository

class MapsViewModel (private val storyRepository: StoryRepository):ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}
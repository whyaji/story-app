package com.whyaji.storyapp.ui.story.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.whyaji.storyapp.data.local.StoryRepository
import com.whyaji.storyapp.data.remote.response.ListStoryItem

class ListStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getAllStories(): LiveData<PagingData<ListStoryItem>> {
        return repository.getAllStories().cachedIn(viewModelScope)
    }
}
package com.whyaji.storyapp.ui.auth.register

import androidx.lifecycle.ViewModel
import com.whyaji.storyapp.data.local.StoryRepository

class RegisterViewModel (private val storyRepository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = storyRepository.register(name, email, password)
}

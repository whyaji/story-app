package com.whyaji.storyapp.ui.auth.login

import androidx.lifecycle.ViewModel
import com.whyaji.storyapp.data.local.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) = storyRepository.login(email, password)
}
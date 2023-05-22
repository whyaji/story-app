package com.whyaji.storyapp.util

import com.whyaji.storyapp.data.remote.response.AddNewStoryResponse
import com.whyaji.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStory(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val news = ListStoryItem(
                id = "$i",
                name = "Penulis $i",
                description = "Cerita $i",
                photoUrl = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/homepage-hero.png"
            )
            storyList.add(news)
        }
        return storyList
    }

    fun generateDummyStoryEmpty(): List<ListStoryItem> {
        return ArrayList()
    }

    fun generateDummyCreateStory(): AddNewStoryResponse {
        return AddNewStoryResponse(
            error = false,
            message = "success"
        )
    }
}
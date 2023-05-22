package com.whyaji.storyapp.util

import android.content.Context
import android.content.SharedPreferences
import com.whyaji.storyapp.data.model.UserModel

class Preference(context: Context) {
    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val USER_ID = "user_id"
        private const val NAME = "name"
        private const val TOKEN = "token"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    fun setUser(value: UserModel) {
        editor.putString(USER_ID, value.userId)
        editor.putString(NAME, value.name)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getUserId(): String {
        return prefs.getString(USER_ID, "") ?: ""
    }

    fun getName(): String {
        return prefs.getString(NAME, "") ?: ""
    }

    fun getToken(): String {
        return prefs.getString(TOKEN, "") ?: ""
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }
}
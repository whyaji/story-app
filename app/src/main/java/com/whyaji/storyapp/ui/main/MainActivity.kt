package com.whyaji.storyapp.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.whyaji.storyapp.R
import com.whyaji.storyapp.databinding.ActivityMainBinding
import com.whyaji.storyapp.ui.auth.AuthActivity
import com.whyaji.storyapp.ui.maps.MapsActivity
import com.whyaji.storyapp.ui.setting.SettingActivity
import com.whyaji.storyapp.ui.setting.SettingViewModel
import com.whyaji.storyapp.util.Preference
import com.whyaji.storyapp.util.SettingPreferences

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val settingViewModel by viewModels<SettingViewModel> {
        SettingViewModel.Factory(SettingPreferences(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            val nightMode = if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val logoutItem = menu.findItem(R.id.action_logout_menu)
        val settingItem = menu.findItem(R.id.action_setting_menu)

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                logoutItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_logout)
                settingItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_settings)
            }
            else -> {
                logoutItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_logout_black)
                settingItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_settings_black)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_logout_menu ->  {
                Preference(this).logout()
                val intent = Intent(this, AuthActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.action_setting_menu -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
            R.id.action_maps_menu -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
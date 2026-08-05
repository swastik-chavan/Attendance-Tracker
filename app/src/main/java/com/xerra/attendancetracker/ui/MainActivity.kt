package com.xerra.attendancetracker.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.xerra.attendancetracker.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme and scale before super.onCreate
        val sharedPrefs = getSharedPreferences("attendance_prefs", Context.MODE_PRIVATE)
        applySavedTheme(sharedPrefs)
        applySavedScale(sharedPrefs)
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)
    }

    private fun applySavedTheme(sharedPrefs: android.content.SharedPreferences) {
        val savedTheme = sharedPrefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        val mode = when (savedTheme) {
            "LIGHT" -> AppCompatDelegate.MODE_NIGHT_NO
            "DARK" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun applySavedScale(sharedPrefs: android.content.SharedPreferences) {
        val scale = sharedPrefs.getString("ui_scale", "DEFAULT") ?: "DEFAULT"
        val themeId = when (scale) {
            "COMPACT" -> R.style.Theme_AttendanceTracker_Compact
            "COMFORTABLE" -> R.style.Theme_AttendanceTracker_Comfortable
            "LARGE" -> R.style.Theme_AttendanceTracker_Large
            else -> R.style.Theme_AttendanceTracker
        }
        setTheme(themeId)
    }
}

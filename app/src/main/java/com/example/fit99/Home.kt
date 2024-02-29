package com.example.fit99

import AnnouncementFragment
import AppPreferences
import HomeFragment
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fit99.classes.ProfileWorkout
import com.example.fit99.classes.WorkoutExerciseView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import java.time.LocalDate


class Home : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_home)
        // Initialize NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup BottomNavigationView with NavController
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.setupWithNavController(navController)

        val appPreferences = AppPreferences(this)



        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // Check if home fragment is in the back stack
                    val homeFragmentFound = navController.popBackStack(R.id.homeFragment, false)

                    // If home fragment was not found in the back stack, navigate to it
                    if (!homeFragmentFound) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.workoutFragment2 -> {
                    // Check if workout fragment is in the back stack
                    val workoutFragmentFound = navController.popBackStack(R.id.workoutFragment2, false)

                    // If workout fragment was not found in the back stack, navigate to it
                    if (!workoutFragmentFound) {
                        navController.navigate(R.id.workoutFragment2)
                    }
                    true
                }
                R.id.announcementFragment -> {
                    // Check if announcement fragment is in the back stack
                    val announcementFragmentFound = navController.popBackStack(R.id.announcementFragment, false)

                    // If announcement fragment was not found in the back stack, navigate to it
                    if (!announcementFragmentFound) {
                        navController.navigate(R.id.announcementFragment)
                    }
                    true
                }
                R.id.profileFragment2 -> {
                    // Check if profile fragment is in the back stack
                    val profileFragmentFound = navController.popBackStack(R.id.profileFragment2, false)

                    // If profile fragment was not found in the back stack, navigate to it
                    if (!profileFragmentFound) {
                        navController.navigate(R.id.profileFragment2)
                    }
                    true
                }
                else -> false
            }
        }

        logIntentExtras(intent)



    }

    fun hideBottomNavigationBar() {
        bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNavigationBar() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun setScreenRotation(locked: Boolean) {
        requestedOrientation = if (locked) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }


    private fun logIntentExtras(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                Log.d("Notifs", "Key: $key, Value: $value")
            }
        } else {
            Log.d("IntentExtras", "No extras in intent")
        }
    }


    override fun onBackPressed() {
        val currentFragment = getCurrentFragment()

        when (currentFragment) {
            is HomeFragment,
            is workoutFragment,
            is AnnouncementFragment,
            is ProfileFragment -> {
                // Do nothing or custom logic for specific fragments
                Log.d("FragmentDebug", "Back press disabled for fragment ID: ${currentFragment.id}")
            }
            else -> {
                super.onBackPressed()
            }
        }
    }


    private fun getCurrentFragment(): Fragment? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
        return navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
    }







}

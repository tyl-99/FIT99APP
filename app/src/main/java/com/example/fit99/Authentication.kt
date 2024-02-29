package com.example.fit99

import AppPreferences
import CoachPreferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp

class Authentication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        FirebaseApp.initializeApp(this)


        val appPreferences = AppPreferences(this)
        if(appPreferences.isLoggedIn()){
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            this.finish()
        }


        navController.navigate(R.id.loginFragment2)


    }
}
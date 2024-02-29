package com.example.fit99

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MyReceiver","jini")
        val message = intent.getStringExtra("notification_message") ?: "Notification Time!"
    }
}


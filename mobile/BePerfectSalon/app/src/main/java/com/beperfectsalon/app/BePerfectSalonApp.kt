package com.beperfectsalon.app

import android.app.Application
import com.google.firebase.FirebaseApp

class BePerfectSalonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

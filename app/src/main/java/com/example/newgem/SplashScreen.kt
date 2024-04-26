package com.example.newgem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.statusBarColor = getColor(R.color.back)
        window.navigationBarColor = getColor(R.color.back)

        val splashScreen= findViewById<LinearLayout>(R.id.mainlogo)
        splashScreen.alpha= 0f
        splashScreen.animate().setDuration(4000).alpha(1f).withEndAction {
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
package com.example.newgem

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.statusBarColor = getColor(R.color.back)
        window.navigationBarColor = getColor(R.color.back)
        val textview = findViewById<TextView>(R.id.name)
        val title = "Questions Evaluated and Responded by Intelligence"
        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(
            ForegroundColorSpan(getColor(R.color.blue)), 0, 9, 0
        )
        spannableTitle.setSpan(
            ForegroundColorSpan(getColor(R.color.green)), 10, title.length, 0
        )
        textview.text = spannableTitle


        val splashScreen = findViewById<RelativeLayout>(R.id.splashscreen)
        splashScreen.alpha = 0f
        splashScreen.animate().setDuration(4000).alpha(1f).withEndAction {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
package com.example.newgem

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etprompt = findViewById<EditText>(R.id.prompt)
        val submitbtn = findViewById<Button>(R.id.submitbtn)
        val aioutput = findViewById<TextView>(R.id.aioutput)


        submitbtn.setOnClickListener {
            val prompt = etprompt.text.toString()
            etprompt
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro-vision",
                apiKey = com.example.newgem.BuildConfig.apiKey
            )

            val image1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.image1)
            val image2: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.image2)

            // Construct input content
            val inputContent = content {
                image(image1)
                image(image2)
                if (prompt.isNotEmpty()) {
                    text(prompt) // Include the prompt as text input if it's not empty
                }
            }

            // Generate content
            runBlocking {
                val response = generativeModel.generateContent(inputContent)
                aioutput.text = response.text
            }
            etprompt.text.clear()
        }
    }

//            print(response.text)
}
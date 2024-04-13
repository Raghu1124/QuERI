package com.example.newgem

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private val imageRequestCode = 100
    private lateinit var viewImage: ImageView
    private lateinit var addbtn: ImageButton
    private var image1: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etprompt = findViewById<EditText>(R.id.prompt)
        val submitbtn = findViewById<Button>(R.id.submitbtn)
        val aioutput = findViewById<TextView>(R.id.aioutput)
        viewImage = findViewById(R.id.viewImage)
        addbtn = findViewById(R.id.addd)

        addbtn.setOnClickListener {
            pickImage()
        }

        submitbtn.setOnClickListener {
            val prompt = etprompt.text.toString()
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro-vision", apiKey = BuildConfig.apiKey
            )

            val inputContent = content {
                image(image1!!)
//                image(image2)
                if (prompt.isNotEmpty()) {
                    text(prompt)
                }
            }

            runBlocking {
                val response = generativeModel.generateContent(inputContent)
                aioutput.text = response.text
            }
            etprompt.text.clear()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                try {
                    // Decode the image URI into a Bitmap
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    viewImage.setImageBitmap(bitmap)
                    image1 = bitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
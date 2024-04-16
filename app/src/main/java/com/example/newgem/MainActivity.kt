package com.example.newgem

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
        val copybtn = findViewById<Button>(R.id.copy)
        val resetbtn = findViewById<Button>(R.id.reset)
        val copyresetbtn = findViewById<LinearLayout>(R.id.copyreset)

        viewImage = findViewById(R.id.viewImage)
        addbtn = findViewById(R.id.addd)

        addbtn.setOnClickListener {
            pickImage()
        }

        submitbtn.setOnClickListener {
            val prompt = etprompt.text.toString()
            val generativeModel = GenerativeModel(
                modelName = if (image1 == null) "gemini-pro" else "gemini-pro-vision",
                apiKey = BuildConfig.apiKey
            )

            if (image1 == null && prompt.isEmpty()) {
                Toast.makeText(this, "Please select an image or enter a prompt", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val inputContent = content {
                    if (image1 != null) {
                        image(image1!!)
                    }
                    if (prompt.isNotEmpty()) {
                        text(prompt)
                    }
                }
                runBlocking {
                    val response = generativeModel.generateContent(inputContent)
                    copyresetbtn.visibility = View.VISIBLE
                    aioutput.text = response.text
                }
            }
        }

        copybtn.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Output", aioutput.text)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(this, "Text copied", Toast.LENGTH_SHORT).show()
        }

        resetbtn.setOnClickListener {
            etprompt.text.clear()
            viewImage.visibility = View.GONE
            aioutput.text = ""
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewImage.visibility = View.VISIBLE
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                try {
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
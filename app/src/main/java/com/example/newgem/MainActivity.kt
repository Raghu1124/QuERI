package com.example.newgem

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        val clearbtn = findViewById<Button>(R.id.clear)
        val resetbtn = findViewById<Button>(R.id.reset)
        val closebtn = findViewById<ImageButton>(R.id.close)
        val copyresetbtn = findViewById<LinearLayout>(R.id.copyreset)

        setStatusBarColor(getColor(R.color.black))
        setWindowNavigationBarColor(getColor(R.color.black))

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
            if (clip == null) {
                Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show()
            } else {
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(this, "Text copied", Toast.LENGTH_SHORT).show()
            }
        }
        clearbtn.setOnClickListener {
            aioutput.text = ""
        }
        closebtn.setOnClickListener {
            etprompt.text.clear()
        }

        resetbtn.setOnClickListener {
            etprompt.text.clear()
            viewImage.visibility = View.GONE
            aioutput.text = ""
            copyresetbtn.visibility = View.GONE
        }

        viewImage.setOnClickListener {
            image1?.let { bitmap ->
                showImagePreview(bitmap)
            }
        }
    }

    private fun showImagePreview(bitmap: Bitmap) {
        val overlay = View(this)
        overlay.setBackgroundColor(Color.parseColor("#90000000"))
        overlay.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val builder = AlertDialog.Builder(this)
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        builder.setView(imageView)
        val dialog = builder.create()
        dialog.show()
    }

    private fun setWindowNavigationBarColor(color: Int) {
        window.navigationBarColor = color
    }

    private fun setStatusBarColor(color: Int) {
        window.statusBarColor = color
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
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    viewImage.setImageBitmap(bitmap)
                    image1 = bitmap
                    viewImage.visibility = View.VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
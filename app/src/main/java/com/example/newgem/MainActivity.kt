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
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.internal.ViewUtils.hideKeyboard
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

//    nothing just checking
    private val imageRequestCode = 100
    private lateinit var viewImage: ImageView
    private lateinit var addbtn: Button
    private var image1: Bitmap? = null
    private lateinit var outputcard: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etprompt = findViewById<EditText>(R.id.prompt)
        val submitbtn = findViewById<Button>(R.id.submitbtn)
        val aioutput = findViewById<TextView>(R.id.aioutput)
        val copybtn = findViewById<ImageButton>(R.id.copy)
        val clearbtn = findViewById<Button>(R.id.clear)
        val resetbtn = findViewById<Button>(R.id.reset)
        val closebtn = findViewById<ImageButton>(R.id.close)
        val clearreset = findViewById<LinearLayout>(R.id.clearreset)

        window.statusBarColor = getColor(R.color.back)
        window.navigationBarColor = getColor(R.color.back)

        outputcard = findViewById(R.id.outputcard)
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
                    aioutput.text = response.text
                }
                outputcard.visibility = View.VISIBLE
                clearreset.visibility = View.VISIBLE

                hideKeyboard()
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
            outputcard.visibility = View.GONE
            aioutput.text = ""
            clearreset.visibility = View.GONE
            viewImage.visibility = View.GONE
        }

        viewImage.setOnClickListener {
            image1?.let { bitmap ->
                showImagePreview(bitmap)
            }
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: View(this)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
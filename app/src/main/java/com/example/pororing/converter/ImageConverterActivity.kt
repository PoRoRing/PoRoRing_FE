package com.example.pororing.converter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pororing.DBHelper
import com.example.pororing.databinding.ActivityImageConverterBinding
import com.example.pororing.myPage.MyPageActivity
import com.example.pororing.save.ResultActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageConverterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageConverterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra("image_uri")
        if (imageUriString == null) {
            Toast.makeText(this, "이미지 불러오기 실패", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.getResult.setOnClickListener {
            val checkedId = binding.kind.checkedRadioButtonId
            val category = findViewById<RadioButton>(checkedId)?.text?.toString() ?: ""

            val infoOptions = arrayListOf<String>()
            listOf(
                binding.ch1,
                binding.ch2,
                binding.ch3,
                binding.ch4,
                binding.ch5,
                binding.ch6
            ).forEach {
                if (it.isChecked) infoOptions.add(it.text.toString())
            }

            val db = DBHelper(this).readableDatabase
            val cursor = db.rawQuery("SELECT * FROM MEMBER LIMIT 1", null)

            val resultJson = JSONObject()
            if (cursor.moveToFirst()) {
                val age = cursor.getInt(cursor.getColumnIndexOrThrow("age"))
                val heightWeight = cursor.getString(cursor.getColumnIndexOrThrow("height_weight"))
                val job = cursor.getString(cursor.getColumnIndexOrThrow("job"))
                val health = cursor.getString(cursor.getColumnIndexOrThrow("health"))
                val allergy = cursor.getString(cursor.getColumnIndexOrThrow("allergy"))
                val etc = cursor.getString(cursor.getColumnIndexOrThrow("etc"))

                val (height, weight) = heightWeight.split("/").map { it.trim() }
                val healthList = health.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val allergyList = allergy.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                val userInfo = JSONObject().apply {
                    put("age", age)
                    put("height", height)
                    put("weight", weight)
                    put("job", job)
                    put("healthConditions", JSONArray(healthList))
                    put("allergies", JSONArray(allergyList))
                    put("etc", etc)
                }
                resultJson.put("category", category)
                resultJson.put("infoOptions", JSONArray(infoOptions))
                resultJson.put("userInfo", userInfo)
            }
            cursor.close()
            db.close()

            val imagePart = imageUriString.let {
                val uri = Uri.parse(it)
                uriToMultipart(this, uri)
            }

            if (imagePart == null) {
                Toast.makeText(this, "이미지 변환 실패", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val responseBody = uploadToServer(imagePart, resultJson)
                if (responseBody != null) {
                    val intent = Intent(this@ImageConverterActivity, ResultActivity::class.java)
                    intent.putExtra("server_response", responseBody)
                    startActivity(intent)
                }
            }
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val fileName = getFileName(context, uri) ?: "image.jpg"

        val requestBody = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", fileName, requestBody)
    }

    suspend fun uploadToServer(imagePart: MultipartBody.Part, resultJson: JSONObject): String? {
        val jsonString = resultJson.toString()
        val jsonRequestBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val jsonPart = MultipartBody.Part.createFormData("preInformation", null, jsonRequestBody)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        return try {
            val response = apiService.uploadImageWithJson(imagePart, jsonPart)
            if (response.isSuccessful) {
                Log.d("서버 응답", response.body().toString())
                response.body()?.toString()
            } else {
                Log.e("서버 에러", response.errorBody()?.string() ?: "Unknown error")
                null
            }
        } catch (e: Exception) {
            Log.e("네트워크 예외", e.toString())
            null
        }
    }
}

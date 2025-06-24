package com.example.pororing.save

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.pororing.MyApplication
import com.example.pororing.MyNotificationHelper
import com.example.pororing.databinding.ActivityResultBinding
import com.example.pororing.mainPage.MainActivity
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ResultActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityResultBinding
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tts = TextToSpeech(this, this)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val fontSizeStr = prefs.getString("font_size", "16")
        val fontColor = prefs.getString("font_color", "#585858")

        val fontSizeFloat = fontSizeStr?.toFloatOrNull() ?: 16f
        binding.resultText.textSize = fontSizeFloat

        try {
            binding.resultText.setTextColor(android.graphics.Color.parseColor(fontColor))
        } catch (e: IllegalArgumentException) {
            binding.resultText.setTextColor(android.graphics.Color.parseColor("#585858"))
        }

        val dtoString = intent.getStringExtra("server_response")
        var productName = ""

        if (dtoString != null) {

            val regex = Regex("""geminiResult=(.*)\)""", RegexOption.DOT_MATCHES_ALL)
            val match = regex.find(dtoString)
            val geminiResult = match?.groupValues?.get(1)?.trim()

            val nameRegex = Regex("""name=(.*?),""")
            val nameMatch = nameRegex.find(dtoString)
            val name = nameMatch?.groupValues?.get(1)?.trim()

            if (geminiResult != null) {
                binding.resultText.text = geminiResult
            } else {
                Log.e("파싱 실패", "geminiResult를 추출할 수 없습니다. 원문: $dtoString")
            }

            if (name != null) {
                productName = name

                val retrofit = Retrofit.Builder()

                    .baseUrl("https://apis.data.go.kr/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()

                val api = retrofit.create(ApiService::class.java)

                val call = api.getJsonList(
                    serviceKey = "",
                    prdlstNm = productName,
                    returnType = "json",
                    pageNo = 1,
                    numOfRows = 1
                )
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                try {
                                    val jsonObject = JSONObject(body)
                                    val items = jsonObject.getJSONObject("body").getJSONArray("items")
                                    if (items.length() > 0) {
                                        val item = items.getJSONObject(0).getJSONObject("item")
                                        val imgurl2 = item.optString("imgurl2", "")
                                        if (imgurl2.isNotEmpty()) {
                                            runOnUiThread {
                                                Glide.with(this@ResultActivity)
                                                    .load(imgurl2)
                                                    .into(binding.resultImage)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("파싱 오류", "JSON 파싱 중 오류: ${e.message}")
                                }
                            }
                        } else {
                            Log.e("API 실패", "응답 코드: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e("API 호출 실패", t.message ?: "Unknown error")
                    }
                })
            } else {
                Log.e("파싱 실패", "name 추출 불가: $dtoString")
            }

        } else {
            Log.e("에러", "Intent로 받은 데이터가 null임")
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.saveBtn.setOnClickListener {
            if(MyApplication.checkAuth()){
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val data = mapOf(
                    "email" to MyApplication.email,
                    "date" to currentDate,
                    "name" to productName,
                    "content" to binding.resultText.text.toString(),
                )
                MyApplication.db.collection("saveInfo")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("TAG","data save OK")
                        val helper = MyNotificationHelper(this)
                        helper.showNotification("Firestore","저장되었습니다.")
                        finish()
                    }
                    .addOnFailureListener {
                        Log.d("TAG","data save ERROR")
                    }
            }else{
                Toast.makeText(this,"인증을 먼저 해주세요",Toast.LENGTH_SHORT).show()
            }
        }

        binding.speaking.setOnClickListener {
            val textToRead = binding.resultText.text.toString()
            if (textToRead.isNotEmpty()) {
                speakText(textToRead)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "지원하지 않는 언어입니다.")
            }
        } else {
            Log.e("TTS", "초기화 실패")
        }
    }

    private fun speakText(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
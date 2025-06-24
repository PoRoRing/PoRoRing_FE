package com.example.pororing.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pororing.MainJsonFragment
import com.example.pororing.PlusFragment
import com.example.pororing.R
import com.example.pororing.databinding.ActivityPlusBinding
import com.example.pororing.mainPage.MainActivity
import com.google.gson.Gson
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class PlusActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlusBinding

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://openapi.seoul.go.kr:8088/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var plusfragment = PlusFragment()
        val bundle = Bundle()

        binding.btnSearch.setOnClickListener {
            val loc = binding.edtLoc.text.toString()
            if (loc == "") {
                Toast.makeText(this, "시작 인덱스를 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                bundle.putString("searchIndex", binding.edtLoc.text.toString())}

            plusfragment = PlusFragment()
            plusfragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_content, plusfragment)
                .commit()
        }

        binding.backBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

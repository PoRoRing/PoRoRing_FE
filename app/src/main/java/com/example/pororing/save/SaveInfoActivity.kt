package com.example.pororing.save

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pororing.MainJsonFragment
import com.example.pororing.R
import com.example.pororing.databinding.ActivitySaveInfoBinding
import com.example.pororing.mainPage.MainActivity

class SaveInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySaveInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        var jsonfragment = MainJsonFragment()
        val bundle = Bundle()

        jsonfragment = MainJsonFragment()
        jsonfragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_content, jsonfragment)
            .commit()
    }
}
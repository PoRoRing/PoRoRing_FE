package com.example.pororing.myPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.pororing.DBHelper
import com.example.pororing.MyApplication
import com.example.pororing.databinding.ActivityMyPageBinding
import com.example.pororing.mainPage.MainActivity

class MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.email.text = "${MyApplication.email}"

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val userName = prefs.getString("name", "Guest")
        binding.name.text = userName

        val db = DBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM MEMBER LIMIT 1", null)

        if (cursor.moveToFirst()) {
            val age = cursor.getInt(cursor.getColumnIndexOrThrow("age"))
            val heightWeight = cursor.getString(cursor.getColumnIndexOrThrow("height_weight"))
            val job = cursor.getString(cursor.getColumnIndexOrThrow("job"))
            val health = cursor.getString(cursor.getColumnIndexOrThrow("health"))
            val allergy = cursor.getString(cursor.getColumnIndexOrThrow("allergy"))
            val etc = cursor.getString(cursor.getColumnIndexOrThrow("etc"))

            binding.age.text = age.toString()
            binding.hAndW.text = heightWeight
            binding.job.text = job
            binding.health.text = health
            binding.allergy.text = allergy
            binding.etc.text = etc
        }

        cursor.close()
        db.close()

        binding.backBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.patch.setOnClickListener {
            binding.patch.visibility = View.GONE
            binding.saveBtn.visibility = View.VISIBLE

            // 나이
            binding.age.visibility = View.GONE
            binding.editAge.setText(binding.age.text.toString())
            binding.editAge.visibility = View.VISIBLE

            // 키/몸무게
            binding.hAndW.visibility = View.GONE
            binding.editHAndW.setText(binding.hAndW.text.toString())
            binding.editHAndW.visibility = View.VISIBLE

            // 직업
            binding.job.visibility = View.GONE
            binding.editJob.setText(binding.job.text.toString())
            binding.editJob.visibility = View.VISIBLE

            // 건강 정보
            binding.health.visibility = View.GONE
            binding.editHealth.setText(binding.health.text.toString())
            binding.editHealth.visibility = View.VISIBLE

            // 알러지
            binding.allergy.visibility = View.GONE
            binding.editAllergy.setText(binding.allergy.text.toString())
            binding.editAllergy.visibility = View.VISIBLE

            // 기타
            binding.etc.visibility = View.GONE
            binding.editEtc.setText(binding.etc.text.toString())
            binding.editEtc.visibility = View.VISIBLE

        }

        binding.saveBtn.setOnClickListener {
            val db = DBHelper(this).writableDatabase

            val cursor = db.rawQuery("SELECT COUNT(*) FROM MEMBER", null)
            cursor.moveToFirst()
            val exists = cursor.getInt(0) > 0
            cursor.close()

            val age = binding.editAge.text.toString().toIntOrNull() ?: 0
            val heightWeight = binding.editHAndW.text.toString()
            val job = binding.editJob.text.toString()
            val health = binding.editHealth.text.toString()
            val allergy = binding.editAllergy.text.toString()
            val etc = binding.editEtc.text.toString()

            if (exists) {
                db.execSQL("""
        UPDATE MEMBER SET 
        age = ?, height_weight = ?, job = ?, health = ?, allergy = ?, etc = ?
        WHERE _id = (SELECT _id FROM MEMBER LIMIT 1)
    """.trimIndent(), arrayOf(age, heightWeight, job, health, allergy, etc))
            } else {
                db.execSQL("""
        INSERT INTO MEMBER (age, height_weight, job, health, allergy, etc)
        VALUES (?, ?, ?, ?, ?, ?)
    """.trimIndent(), arrayOf(age, heightWeight, job, health, allergy, etc))
            }
            db.close()

            binding.patch.visibility = View.VISIBLE
            binding.saveBtn.visibility = View.GONE

            binding.editAge.visibility = View.GONE
            binding.age.text = binding.editAge.text.toString()
            binding.age.visibility = View.VISIBLE

            binding.editHAndW.visibility = View.GONE
            binding.hAndW.text = binding.editHAndW.text.toString()
            binding.hAndW.visibility = View.VISIBLE

            binding.editJob.visibility = View.GONE
            binding.job.text = binding.editJob.text.toString()
            binding.job.visibility = View.VISIBLE

            binding.editHealth.visibility = View.GONE
            binding.health.text = binding.editHealth.text.toString()
            binding.health.visibility = View.VISIBLE

            binding.editAllergy.visibility = View.GONE
            binding.allergy.text = binding.editAllergy.text.toString()
            binding.allergy.visibility = View.VISIBLE

            binding.editEtc.visibility = View.GONE
            binding.etc.text = binding.editEtc.text.toString()
            binding.etc.visibility = View.VISIBLE
        }

    }
}
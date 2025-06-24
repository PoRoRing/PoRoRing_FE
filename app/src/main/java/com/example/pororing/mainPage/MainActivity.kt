package com.example.pororing.mainPage

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.example.pororing.R
import com.example.pororing.SettingActivity
import com.example.pororing.converter.ImageConverterActivity
import com.example.pororing.MainJsonFragment
import com.example.pororing.MyApplication
import com.example.pororing.databinding.ActivityMainBinding
import com.example.pororing.myPage.MyPageActivity
import com.example.pororing.save.SaveInfoActivity
import com.example.pororing.search.PlusActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: NavigationView
    private lateinit var menuButton: ImageView

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.navView.itemIconTintList = null

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        menuButton = binding.hamburger

        navView.setNavigationItemSelectedListener(this)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.selectImgBtn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }

        val jsonfragment = MainJsonFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_content, jsonfragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val userName = prefs.getString("name", "Guest")
        val bgResourceName = prefs.getString("color", "draw_background")

        binding.name.text = "$userName"

        val resId = resources.getIdentifier(bgResourceName, "drawable", packageName)
        if (resId != 0) {
            binding.navView.setBackgroundResource(resId)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                val intent = Intent(this, ImageConverterActivity::class.java)
                intent.putExtra("image_uri", imageUri.toString())
                startActivity(intent)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> startActivity(Intent(this, MyPageActivity::class.java))
            R.id.nav_save_info -> startActivity(Intent(this, SaveInfoActivity::class.java))
            R.id.nav_setting -> startActivity(Intent(this, SettingActivity::class.java))
            R.id.plus -> startActivity(Intent(this, PlusActivity::class.java))
            R.id.logout -> {
                MyApplication.auth.signOut()
                MyApplication.email = null
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }
}
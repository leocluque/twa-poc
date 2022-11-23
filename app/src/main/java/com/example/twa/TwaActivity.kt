package com.example.twa

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.twa.databinding.ActivityMainBinding
import com.google.androidbrowserhelper.trusted.TwaLauncher


class TwaActivity : AppCompatActivity() {


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkDeepLink()
        val twaLauncher = TwaLauncher(this)


        binding.openBtn.setOnClickListener {
            twaLauncher.launch(Uri.parse("https://ktor-twa.herokuapp.com/"))
        }
        binding.openTabs.setOnClickListener {
            startActivity(Intent(this, CustomTabsActivity2::class.java))
        }
        binding.open2.setOnClickListener {
            startActivity(Intent(this, CustomTabsActivity::class.java))
        }
        binding.open4.setOnClickListener {
            startActivity(Intent(this, TestCallbackTwaActivity::class.java))
        }

        binding.open5.setOnClickListener {
            startActivity(Intent(this, CustomTabsSessionActivity::class.java))
        }
    }

    private fun checkDeepLink() {
        val intent = intent
        val data = intent.data
        if (data != null) {
            val newIntent = Intent(this, ChallengeActivity::class.java)
            newIntent.putExtra("challenge", "${data.lastPathSegment}")
            startActivity(newIntent)
        }
    }
}


package com.example.twa

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.browser.trusted.TrustedWebActivityIntent
import com.example.twa.databinding.ActivityMainBinding
import com.google.androidbrowserhelper.trusted.TwaLauncher


class MainActivity : AppCompatActivity() {


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
            startActivity(Intent(this, MainActivity2::class.java))
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


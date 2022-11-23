package com.example.twa

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.*
import com.example.twa.databinding.ActivityMainBinding
import com.google.androidbrowserhelper.trusted.TwaLauncher
import java.util.*


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


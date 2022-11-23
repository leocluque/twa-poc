package com.example.twa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.twa.databinding.ActivityChallengeBinding

class ChallengeActivity : AppCompatActivity() {

    private val binding: ActivityChallengeBinding by lazy {
        ActivityChallengeBinding.inflate(layoutInflater)
    }

    val challenge by lazy {
        intent.extras?.getString("challenge")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.textView.text = challenge
    }
}
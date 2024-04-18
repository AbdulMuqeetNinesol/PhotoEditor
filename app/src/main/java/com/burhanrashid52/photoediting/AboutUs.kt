package com.burhanrashid52.photoediting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import editphotos.backgroundremover.photobackgroundchanger.databinding.ActivityAboutUsBinding

class AboutUs : AppCompatActivity() {
    private val binding : ActivityAboutUsBinding by lazy {
        ActivityAboutUsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.finishScreen.setOnClickListener {
            finish()
        }
    }
}
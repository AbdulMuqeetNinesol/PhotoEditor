package com.burhanrashid52.photoediting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.burhanrashid52.photoediting.Ads.openAppOnPlayStore
import com.burhanrashid52.photoediting.Ads.sendAnalytics
import com.burhanrashid52.photoediting.Ads.shareIt
import editphotos.backgroundremover.photobackgroundchanger.databinding.ActivitySettingBinding

class setting : AppCompatActivity() {
    private val binding : ActivitySettingBinding by lazy {
        ActivitySettingBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rateUs.setOnClickListener {
            sendAnalytics("setting","rate_Us_btn_click")
            val intent = Intent(this,rateUs::class.java)
            startActivity(intent)
        }
        binding.arrowback.setOnClickListener {
            sendAnalytics("setting","back_btn_click")
            finish()
        }
        binding.aboutus.setOnClickListener {
            sendAnalytics("setting","about_Us_btn_click")
            val intent = Intent(this,AboutUs::class.java)
            startActivity(intent)
        }
        binding.shareApp.setOnClickListener {
            sendAnalytics("setting","share_btn_click")
            shareIt()
        }

    }
}
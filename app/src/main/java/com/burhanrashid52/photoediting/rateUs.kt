package com.burhanrashid52.photoediting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.burhanrashid52.photoediting.Ads.openAppOnPlayStore
import editphotos.backgroundremover.photobackgroundchanger.databinding.ActivityRateUsBinding

class rateUs : AppCompatActivity() {
    private val binding : ActivityRateUsBinding by lazy {
        ActivityRateUsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.next.setOnClickListener {
            openAppOnPlayStore(this)
        }

    }
}
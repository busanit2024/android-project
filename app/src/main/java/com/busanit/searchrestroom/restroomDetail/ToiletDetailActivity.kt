package com.busanit.searchrestroom.restroomDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.databinding.ActivityToiletDetailBinding


class ToiletDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityToiletDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}
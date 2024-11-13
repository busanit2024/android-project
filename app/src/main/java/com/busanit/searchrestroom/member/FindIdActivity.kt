package com.busanit.searchrestroom.member

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.databinding.ActivityFindIdBinding

class FindIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}
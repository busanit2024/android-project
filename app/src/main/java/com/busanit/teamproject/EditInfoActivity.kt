package com.busanit.teamproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.busanit.teamproject.databinding.ActivityEditInfoBinding

class EditInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<ImageView>(R.id.X).setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

}
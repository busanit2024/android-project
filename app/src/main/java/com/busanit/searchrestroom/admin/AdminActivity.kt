package com.busanit.searchrestroom.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private var db: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        val deleteRequestCount = db?.DeleteRequestDao()?.getCount() ?: 0
        binding.badge.text = deleteRequestCount.toString()

        binding.deleteBtn.setOnClickListener {
            val intent = Intent(this, AdminDeleteActivity::class.java)
            startActivity(intent)
        }

        binding.userBtn.setOnClickListener {

        }

        binding.reviewBtn.setOnClickListener {

        }

        // 뒤로 가기 버튼 클릭 시
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val deleteRequestCount = db?.DeleteRequestDao()?.getCount() ?: 0
        binding.badge.text = deleteRequestCount.toString()
    }
}
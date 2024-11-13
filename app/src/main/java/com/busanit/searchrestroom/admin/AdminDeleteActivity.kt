package com.busanit.searchrestroom.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityAdminDeleteDetailBinding

class AdminDeleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDeleteDetailBinding
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDeleteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()

        // 샘플 데이터
        val deleteItems = listOf(
            RequestDelete(3, "건물명1", "서울시 강남구", "2024-11-13", "테스트"),
            RequestDelete(4, "건물명1", "서울시 강남구", "2024-11-13", "테스트")
        )

        // RecyclerView 설정
        binding.deleteList.layoutManager = LinearLayoutManager(this)
        binding.deleteList.adapter = DeleteAdapter(deleteItems)

        // 뒤로 가기 버튼 클릭 시
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

}
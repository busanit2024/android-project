package com.busanit.teamproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit.teamproject.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 샘플 데이터
        val favoriteItems = listOf(
            FavoriteItem(R.drawable.ic_star, "건물명", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명", "서울시 마포구")
        )

        // RecyvlerView 설정
        binding.favoriteList.layoutManager = LinearLayoutManager(this)
        binding.favoriteList.adapter = FavoriteAdapter(favoriteItems)

        // 뒤로 가기 버튼 클릭 시
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}

package com.busanit.searchrestroom.myPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 샘플 데이터
        val favoriteItems = listOf(
            FavoriteItem(R.drawable.ic_star, "건물명1", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명2", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명3", "서울시 마포구"),
            FavoriteItem(R.drawable.ic_star, "건물명4", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명5", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명6", "서울시 마포구"),
            FavoriteItem(R.drawable.ic_star, "건물명7", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명8", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명9", "서울시 마포구"),
            FavoriteItem(R.drawable.ic_star, "건물명10", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명11", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명12", "서울시 마포구"),
            FavoriteItem(R.drawable.ic_star, "건물명13", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명14", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명15", "서울시 마포구"),
            FavoriteItem(R.drawable.ic_star, "건물명16", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명17", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명18", "서울시 마포구"),
            FavoriteItem(R.drawable.ic_star, "건물명19", "서울시 강남구"),
            FavoriteItem(R.drawable.ic_star, "건물명20", "서울시 서초구"),
            FavoriteItem(R.drawable.ic_star, "건물명21", "서울시 마포구")
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

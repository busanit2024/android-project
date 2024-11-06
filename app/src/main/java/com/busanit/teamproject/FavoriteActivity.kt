package com.busanit.teamproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val recyclerView: RecyclerView = findViewById(R.id.favorite_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 샘플 데이터 추가
        val favoriteItems = listOf(
            FavoriteItem(R.drawable.ic_star, "건물명", "상세주소"),
            FavoriteItem(R.drawable.ic_star, "건물명", "상세주소"),
            FavoriteItem(R.drawable.ic_star, "건물명", "상세주소")
        )

        val adapter = FavoriteAdapter(favoriteItems)
        recyclerView.adapter = adapter
    }
}

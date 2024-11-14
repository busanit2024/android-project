package com.busanit.searchrestroom.myPage

import BookmarkRepository
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.databinding.ActivityFavoriteBinding
import com.busanit.searchrestroom.restroomDetail.RestroomDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Repository 초기화
        val db = AppDatabase.getDatabase(this)
        bookmarkRepository = BookmarkRepository(db!!.bookmarkDao(), db.restroomDao())

        // RecyclerView 설정
        setupRecyclerView()

        // 북마크된 화장실 목록 로드
        loadBookmarkedRestrooms()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(mutableListOf()) { restroom ->
            // 아이템 클릭 시 상세 페이지로 이동
            val intent = Intent(this, RestroomDetailActivity::class.java)
            intent.putExtra("restroom", restroom)
            intent.putExtra("restroom_id", restroom.restroomId)
            startActivity(intent)
        }
        binding.favoriteList.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = this@FavoriteActivity.adapter
        }
    }

    private fun loadBookmarkedRestrooms() {
        if (AuthHelper.isLoggedIn()) {
            val memberId = AuthHelper.getMemberId()
            lifecycleScope.launch {
                try {
                    val bookmarkedRestrooms = withContext(Dispatchers.IO) {
                        bookmarkRepository.getBookmarkedRestrooms(memberId)
                    }
                    adapter.updateList(bookmarkedRestrooms)
                } catch (e: Exception) {
                    Toast.makeText(this@FavoriteActivity,
                        "북마크 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBookmarkedRestrooms()  // 화면이 다시 보일 때마다 목록 갱신
    }
}
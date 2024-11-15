package com.busanit.searchrestroom.myPage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.databinding.ActivityMyReviewBinding
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.database.ReviewImage
import com.busanit.searchrestroom.member.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyReviewBinding
    private var appDatabase: AppDatabase? = null
  private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

      appDatabase = AppDatabase.getDatabase(applicationContext)

        // 로그인한 사용자의 member_id 가져오기
        val memberId = sharedPreferences.getInt("member_id", -1)
        if (memberId == -1) {
            Toast.makeText(this, "로그인이 필요합니다!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 리뷰 로드
        loadReviews(memberId)

        // 뒤로 가기 버튼 클릭 시
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadReviews(memberId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val reviewDao = appDatabase!!.reviewDao()
            val reviews = reviewDao.getReviewByMemberId(memberId)

            withContext(Dispatchers.Main) {
                // RecyclerView에 데이터 설정
                setupRecyclerView(reviews)
            }
        }
    }

    private suspend fun setupRecyclerView(reviews: List<Review>) {
        binding.myReviewList.layoutManager = LinearLayoutManager(this)
        binding.myReviewList.adapter = MyReviewAdapter(
            reviews.toMutableList()
        )
    }

    private fun getReviewImages(reviewId: Int): List<ReviewImage> {
        val reviewImageDao = appDatabase!!.reviewImageDao()
        return reviewImageDao.getReviewImageById(reviewId) // 메소드 이름 수정
    }



}

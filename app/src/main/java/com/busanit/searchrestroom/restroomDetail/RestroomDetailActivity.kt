package com.busanit.searchrestroom.restroomDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.dao.BookmarkDao
import com.busanit.searchrestroom.dao.ReviewDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Bookmark
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomDetailBinding
import com.busanit.searchrestroom.reviewReg.FilterOption
import com.busanit.searchrestroom.reviewReg.FilterOptionState
import com.busanit.searchrestroom.reviewReg.ReviewAdapter
import com.busanit.searchrestroom.reviewReg.ReviewRegActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RestroomDetailActivity : AppCompatActivity() {

    private lateinit var bookmarkDao: BookmarkDao
    private var memberId: Int = 0
    private var restroomId: Int = 0
    private lateinit var reviewDao: ReviewDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRestroomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // onCreate에서 reviewDao 초기화
        reviewDao = AppDatabase.getDatabase(application).reviewDao()

        // 메인에서 인텐트로 class 받기
        val restroom : Restroom? = intent.getParcelableExtra("restroom")

        // 리뷰 작성화면에서 데이터 받기
        val reviewContent = intent.getStringExtra("reviewContent")
        val restroomId = intent.getIntExtra("restroomId", 0)

        restroom?.let{
            binding.restroomName.text = it.restroomName
            binding.location.text = it.location
            binding.openTime.text = it.openTime

            binding.unisexOrNot.apply {
                text = if (restroom?.unisex == true) "남녀공용" else ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE // 텍스트가 없으면 숨김
            }
            binding.comfort.apply {
                text = if(it.diaper == true) "기저귀 교환대" else ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }
            binding.comfort.apply {
                text  = if(it.accessible == true) "장애인 화장실" else ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }

            val restroomId = it.restroomId
            //memberId = user?.uid
        }


//        // 북마크 체크박스 상태 초기화
//        //setBookmarkState(binding.restroomBookmark)
//
//        // 북마크 체크박스 클릭 이벤트 처리
//        binding.restroomBookmark.setOnCheckedChangeListener { _, isChecked ->
//            onBookmarkCheckedChanged(isChecked)
//        }


        //정보 수정 버튼 클릭 이벤트
        binding.rewriteInfo.setOnClickListener{
            val intent = Intent(this, RestroomUpdateActivity::class.java)
            startActivity(intent)
        }


        // 리뷰작성 버튼 클릭 이벤트
        binding.writeReview.setOnClickListener {
            val intent = Intent(this, ReviewRegActivity::class.java)
            intent.putExtra("restroomId", restroomId)
            intent.putExtra("restroom", restroom)
            startActivity(intent)
        }

        displayReviews(binding)


        // 로그인 여부에 따라 삭제, 수정 나오도록 하기
    }


    // 리뷰 불러오기
    private fun displayReviews(binding: ActivityRestroomDetailBinding) {
        CoroutineScope(Dispatchers.IO).launch {
            val reviews = reviewDao.getLatestReviewsWithFilterByRestroomId(restroomId)

            // 각 리뷰에 대해 필터 옵션을 설정
            val reviewWithSelectedOptions = reviews.map { review ->
                val filterOptions = reviewDao.getFilterOptionsForReview(review.reviewId)
                val selectedOptions = filterOptions.mapNotNull { filterOption ->
                    val option = FilterOption.findByTypeAndName(filterOption.filterType, filterOption.optionName)
                    option?.let { FilterOptionState(option = it) }
                }
                review to selectedOptions
            }

            withContext(Dispatchers.Main) {
                // 어댑터에 매핑된 리뷰와 필터 옵션 리스트 전달
                val reviewAdapter = ReviewAdapter(reviewWithSelectedOptions)
                binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this@RestroomDetailActivity)
                binding.reviewRecyclerView.adapter = reviewAdapter
            }
        }
    }


    @SuppressLint("MissingInflatedId")
    private fun createReviewLayout(review: ReviewDao.ReviewWithFilter): View {
        val reviewLayout = layoutInflater.inflate(R.layout.item_review_view, null)

        // 리뷰 데이터 매핑
        val reviewTextView = reviewLayout.findViewById<TextView>(R.id.reviewText)
        reviewTextView.text = review.content

        val reviewerInfoTextView = reviewLayout.findViewById<TextView>(R.id.reviewerInfo)
        reviewerInfoTextView.text = "${review.nickname} / ${review.regTime}"

//        val filterOptionsTextView = reviewLayout.findViewById<TextView>(R.id.filterOptions)
//        filterOptionsTextView.text = review.selectedOptions.joinToString(", ") { it.optionName }

        return reviewLayout
    }
}
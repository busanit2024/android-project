package com.busanit.searchrestroom.restroomDetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.dao.BookmarkDao
import com.busanit.searchrestroom.database.Bookmark
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomDetailBinding
import com.busanit.searchrestroom.reviewReg.ReviewRegActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RestroomDetailActivity : AppCompatActivity() {

    private lateinit var bookmarkDao: BookmarkDao
    private var memberId: Int = 0
    private var restroomId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRestroomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 메인에서 인텐트로 class 받기
        val restroom : Restroom? = intent.getParcelableExtra("restroom")

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

            restroomId = it.restroomId
            //memberId = user?.uid
        }

        // 북마크 체크박스 상태 초기화
        //setBookmarkState(binding.restroomBookmark)

        // 북마크 체크박스 클릭 이벤트 처리
        binding.restroomBookmark.setOnCheckedChangeListener { _, isChecked ->
            onBookmarkCheckedChanged(isChecked)
        }


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

        //리뷰 뷰화면
        // 전체 리뷰 평점 계산 -> 별

        // 사용자 / 작성일자 불러오기

        // 로그인 여부에 따라 삭제, 수정 나오도록 하기

        // 첨부된 사진 가져오기

        // 댓글 내용 불러오기


    }

    // 멤버 불러와야함
    // 북마크 체크박스 상태가 변경되었을 때 처리
    private fun onBookmarkCheckedChanged(isChecked: Boolean) {
        val bookmark = Bookmark(
            bookmarkId = 0,  // 자동 증가
            restroomId = restroomId,
            memberId = memberId
        )

        if (isChecked) {
            // 북마크 추가
            addBookmark(bookmark)
        } else {
            // 북마크 삭제
            removeBookmark(bookmark)
        }
    }

    // 북마크 추가
    private fun addBookmark(bookmark: Bookmark) {
        CoroutineScope(Dispatchers.IO).launch {
            bookmarkDao.insert(bookmark)
        }
    }

    // 북마크 삭제
    private fun removeBookmark(bookmark: Bookmark) {
        CoroutineScope(Dispatchers.IO).launch {
            bookmarkDao.delete(bookmark)
        }
    }
}
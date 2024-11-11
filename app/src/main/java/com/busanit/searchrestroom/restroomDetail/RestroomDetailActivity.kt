package com.busanit.searchrestroom.restroomDetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.busanit.searchrestroom.dao.RestroomDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomDetailBinding
import com.busanit.searchrestroom.reviewReg.ReviewRegActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RestroomDetailActivity : AppCompatActivity() {
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

        }

        // 북마크 저장 여부에 따라 표시 변경

        // 화장실 정보 불러오기

        //정보 수정 버튼 클릭 이벤트
        binding.rewriteInfo.setOnClickListener{
            val intent = Intent(this, RestrooomUpdateActivity::class.java)
            startActivity(intent)
        }

        // 리뷰작성 버튼 클릭 이벤트
        binding.writeReview.setOnClickListener {
            val intent = Intent(this, ReviewRegActivity::class.java)
            startActivity(intent)
        }

        //리뷰 뷰화면
        // 전체 리뷰 평점 계산 -> 별

        // 사용자 / 작성일자 불러오기

        // 로그인 여부에 따라 삭제, 수정 나오도록 하기

        // 첨부된 사진 가져오기

        // 댓글 내용 불러오기




    }
}
package com.busanit.searchrestroom.restroomDetail

import android.content.Intent
import android.os.Bundle
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

        // db 인스턴스 생성
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app_database"
        ).build()

        val dao = db.restroomDao()



        // 메인에서 인텐트로 id 받기



        // 건물명 / 주소 불러오기
        db.restroomDao()

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
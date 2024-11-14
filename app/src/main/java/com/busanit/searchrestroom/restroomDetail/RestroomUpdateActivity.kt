package com.busanit.searchrestroom.restroomDetail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.DeleteRequest
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomUpdateBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RestroomUpdateActivity : AppCompatActivity(){
  private lateinit var binding: ActivityRestroomUpdateBinding
  private var db: AppDatabase? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityRestroomUpdateBinding.inflate(layoutInflater)
    setContentView(binding.root)

    db = AppDatabase.getDatabase(applicationContext)
    val restroom: Restroom? = intent.getParcelableExtra("restroom")

    // Detail에서 받은 정보 보여주기

    // 수정하기 버튼


    // 삭제 요청 버튼
    val requestMessage = EditText(this)
    requestMessage.hint = "요청사유를 입력해주세요."

    binding.deleteRequestButton.setOnClickListener {
      AlertDialog.Builder(this).run {
        setTitle("삭제 요청")
        setMessage("${restroom?.restroomName ?: "(알수없음)"} 의 삭제를 요청합니다.")
        setView(requestMessage)
        setPositiveButton("요청하기") { _, _ ->
          val message = requestMessage.text.toString()
          handleDeleteRequest(restroom, message)
        }
        setNegativeButton("취소", null)
        show()
      }
    }
  }

  //삭제요청 DB에 추가
  private fun handleDeleteRequest(restroom: Restroom?, message: String) {
    val regTime = Date()
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedTime = outputFormat.format(regTime)
    if (restroom != null) {
      val request = DeleteRequest(
        requestId = 0,
        memberId = AuthHelper.getMemberId(),
        restroomId = restroom.restroomId,
        requestMessage = message,
        regTime = formattedTime.toString()
      )
      db?.DeleteRequestDao()?.insert(request)
      Toast.makeText(this, "삭제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
    } else {
      Toast.makeText(this, "화장실 정보가 없습니다.", Toast.LENGTH_SHORT).show()
    }

  }


}
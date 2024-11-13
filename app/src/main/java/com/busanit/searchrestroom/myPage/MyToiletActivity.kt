package com.busanit.searchrestroom.myPage

import BookmarkRepository
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityMyToiletBinding
import com.busanit.searchrestroom.databinding.ItemMyToiletBinding
import com.busanit.searchrestroom.restroomDetail.RestroomDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyToiletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyToiletBinding
    private lateinit var bookmarkRepository: BookmarkRepository
    private val toiletList = mutableListOf<Restroom>()
    private lateinit var adapter: ToiletAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyToiletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Repository 초기화
        val db = AppDatabase.getDatabase(this)
        bookmarkRepository = BookmarkRepository(db.bookmarkDao(), db.restroomDao())

        // RecyclerView 설정
        adapter = ToiletAdapter(toiletList)
        binding.myToliet.layoutManager = LinearLayoutManager(this)
        binding.myToliet.adapter = adapter

        // 북마크된 화장실 목록 로드
        loadBookmarkedRestrooms()

        binding.backButton.setOnClickListener {
            finish()
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
                    toiletList.clear()
                    toiletList.addAll(bookmarkedRestrooms)
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(this@MyToiletActivity, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Adapter 수정
    inner class ToiletAdapter(private val toiletList: List<Restroom>) :
        RecyclerView.Adapter<ToiletAdapter.ToiletViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToiletViewHolder {
            val itemBinding = ItemMyToiletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ToiletViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ToiletViewHolder, position: Int) {
            val restroom = toiletList[position]
            holder.bind(restroom)
        }

        override fun getItemCount(): Int = toiletList.size

        inner class ToiletViewHolder(private val itemBinding: ItemMyToiletBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            fun bind(restroom: Restroom) {
                itemBinding.buildingName.text = restroom.restroomName
                itemBinding.address.text = restroom.location

                // 아이템 클릭 시 상세 페이지로 이동
                itemBinding.root.setOnClickListener {
                    val intent = Intent(this@MyToiletActivity, RestroomDetailActivity::class.java)
                    intent.putExtra("restroom", restroom)
                    intent.putExtra("restroom_id", restroom.restroomId)
                    startActivity(intent)
                }
            }
        }
    }
}
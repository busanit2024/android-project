package com.busanit.searchrestroom.myPage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.databinding.ActivityMyToiletBinding
import com.busanit.searchrestroom.databinding.ItemMyToiletBinding

class MyToiletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyToiletBinding
    private val toiletList = mutableListOf<Toilet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바인딩 초기화
        binding = ActivityMyToiletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        binding.myToliet.layoutManager = LinearLayoutManager(this)
        binding.myToliet.adapter = ToiletAdapter(toiletList)

        // 샘플 데이터 추가
        addSampleData()

        // 뒤로가기 버튼 설정
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addSampleData() {
        toiletList.add(Toilet("건물1", "서울시 강남구"))
        toiletList.add(Toilet("건물2", "서울시 서초구"))
        toiletList.add(Toilet("건물3", "서울시 송파구"))
        toiletList.add(Toilet("건물4", "서울시 중구"))
        toiletList.add(Toilet("건물5", "서울시 마포구"))
        toiletList.add(Toilet("건물6", "서울시 강동구"))
        toiletList.add(Toilet("건물7", "서울시 강북구"))
        toiletList.add(Toilet("건물8", "서울시 강서구"))
        toiletList.add(Toilet("건물9", "서울시 관악구"))
        toiletList.add(Toilet("건물10", "서울시 광진구"))
        toiletList.add(Toilet("건물11", "서울시 동대문구"))
        toiletList.add(Toilet("건물12", "서울시 동작구"))
        toiletList.add(Toilet("건물13", "서울시 금천구"))
        toiletList.add(Toilet("건물14", "서울시 구로구"))
        toiletList.add(Toilet("건물15", "서울시 노원구"))
        toiletList.add(Toilet("건물16", "서울시 도봉구"))
        toiletList.add(Toilet("건물17", "서울시 종로구"))
        toiletList.add(Toilet("건물18", "서울시 중랑구"))
        toiletList.add(Toilet("건물19", "서울시 용산구"))
        toiletList.add(Toilet("건물20", "서울시 성동구"))
        toiletList.add(Toilet("건물21", "서울시 강남구"))
        toiletList.add(Toilet("건물22", "서울시 서초구"))
        toiletList.add(Toilet("건물23", "서울시 송파구"))
        toiletList.add(Toilet("건물24", "서울시 중구"))
        toiletList.add(Toilet("건물25", "서울시 마포구"))
        toiletList.add(Toilet("건물26", "서울시 강동구"))
        toiletList.add(Toilet("건물27", "서울시 강북구"))
        toiletList.add(Toilet("건물28", "서울시 강서구"))
        toiletList.add(Toilet("건물29", "서울시 관악구"))
        toiletList.add(Toilet("건물30", "서울시 광진구"))

        // 데이터가 변경되었음을 어댑터에 알림
        binding.myToliet.adapter?.notifyDataSetChanged()
    }

    // 데이터 클래스 정의
    data class Toilet(val buildingName: String, val address: String)

    // Adapter 정의
    inner class ToiletAdapter(private val toiletList: List<Toilet>) :
        RecyclerView.Adapter<ToiletAdapter.ToiletViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToiletViewHolder {
            val itemBinding = ItemMyToiletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ToiletViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ToiletViewHolder, position: Int) {
            val toilet = toiletList[position]
            holder.bind(toilet)
        }

        override fun getItemCount(): Int = toiletList.size

        // ViewHolder 정의
        inner class ToiletViewHolder(private val itemBinding: ItemMyToiletBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            fun bind(toilet: Toilet) {
                itemBinding.buildingName.text = toilet.buildingName
                itemBinding.address.text = toilet.address
            }
        }
    }
}

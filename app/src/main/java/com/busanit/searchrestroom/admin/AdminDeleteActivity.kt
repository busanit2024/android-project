package com.busanit.searchrestroom.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.DeleteRequestWithRestroom
import com.busanit.searchrestroom.databinding.ActivityAdminDeleteDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminDeleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDeleteDetailBinding
    private var db: AppDatabase? = null
    private lateinit var deleteItems: List<DeleteRequestWithRestroom>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDeleteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(applicationContext)

        CoroutineScope(Dispatchers.IO).launch {
            deleteItems = db!!.DeleteRequestDao().getDeleteRequestWithRestroom()

            // RecyclerView 설정
            binding.deleteList.layoutManager = LinearLayoutManager(this@AdminDeleteActivity)
            binding.deleteList.adapter = DeleteAdapter(deleteItems)
        }

        // 뒤로 가기 버튼 클릭 시
        binding.backBtn.setOnClickListener {
            finish()
        }
    }


}
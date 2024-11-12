package com.busanit.searchrestroom.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivitySearchlistBinding

class SearchListActivity : AppCompatActivity() {
  lateinit var binding : ActivitySearchlistBinding
  var datas : MutableList<Restroom>? = null
  lateinit var adapter : RestroomAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySearchlistBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.backButton.setOnClickListener {
      finish()
    }

    datas = intent.getParcelableArrayListExtra("locations")
    val currentLat = intent.getDoubleExtra("currentLat", 0.0)
    val currentLong = intent.getDoubleExtra("currentLong", 0.0)

    val layoutManager = LinearLayoutManager(this)
    binding.searchRecyclerView.layoutManager = layoutManager
    adapter = RestroomAdapter(datas!!, currentLat, currentLong)
    binding.searchRecyclerView.adapter = adapter
    binding.searchRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
  }
}
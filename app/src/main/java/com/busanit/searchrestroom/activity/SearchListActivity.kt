package com.busanit.searchrestroom.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.LoginActivity
import com.busanit.searchrestroom.MenuHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivitySearchlistBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SearchListActivity : AppCompatActivity() {
  private lateinit var auth : FirebaseAuth
  lateinit var binding : ActivitySearchlistBinding
  var datas : MutableList<Restroom>? = null
  lateinit var adapter : RestroomAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySearchlistBinding.inflate(layoutInflater)
    setContentView(binding.root)

    FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    auth = Firebase.auth

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

    //메뉴바 아이템 연결
    binding.bottomNavigation.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.menu_login -> {
          val intent = Intent(this, LoginActivity::class.java)
          startActivity(intent)
          true
        }
        ///다른 액티비티로 이동하는 코드 추가 필요
        else -> false
      }
    }
  }

  private var isLoggedIn = false

  val authStateListener = FirebaseAuth.AuthStateListener {
      auth ->
    val currentUser = auth.currentUser
    Log.d("test", "current user : $currentUser")
    isLoggedIn = currentUser != null

    MenuHelper.updateMenuItems(binding.bottomNavigation.menu, isLoggedIn)

    binding.bottomNavigation.invalidate()
  }

  override fun onStart() {
    super.onStart()
    auth.addAuthStateListener (authStateListener)
  }

  override fun onStop() {
    super.onStop()
    auth.removeAuthStateListener (authStateListener)
  }

  override fun onDestroy() {
    super.onDestroy()
    FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)

  }
}
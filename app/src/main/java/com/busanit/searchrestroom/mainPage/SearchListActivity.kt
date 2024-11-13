package com.busanit.searchrestroom.mainPage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.member.LoginActivity
import com.busanit.searchrestroom.MenuHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivitySearchlistBinding
import com.busanit.searchrestroom.myPage.FavoriteActivity
import com.busanit.searchrestroom.myPage.MyPageActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SearchListActivity : AppCompatActivity() {
  lateinit var binding : ActivitySearchlistBinding
  var datas : MutableList<Restroom>? = null
  lateinit var adapter : RestroomAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySearchlistBinding.inflate(layoutInflater)
    setContentView(binding.root)

    MenuHelper.updateMenuItems(binding.bottomNavigation.menu, AuthHelper.isLoggedIn())
    binding.bottomNavigation.invalidate()

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
        R.id.menu_home -> {
          true
        }
        R.id.menu_login -> {
          val intent = Intent(this, LoginActivity::class.java)
          startActivity(intent)
          true
        }
        R.id.menu_mypage -> {
          val intent = Intent(this, MyPageActivity::class.java)
          startActivity(intent)
          true
        }
        R.id.menu_bookmark -> {
          val intent = Intent(this, FavoriteActivity::class.java)
          startActivity(intent)
          true
        }
        else -> false
      }
    }

  }

}
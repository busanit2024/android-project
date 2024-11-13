package com.busanit.searchrestroom

import android.view.Menu

object MenuHelper {
  fun updateMenuItems(menu: Menu, isLoggedIn: Boolean) {
    val loginItem = menu.findItem(R.id.menu_login)
    val mypageItem = menu.findItem(R.id.menu_mypage)

      mypageItem.isVisible = isLoggedIn
    loginItem.isVisible = !isLoggedIn

  }
}
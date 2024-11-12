package com.busanit.searchrestroom

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import com.busanit.searchrestroom.activity.MainActivity

object MenuHelper {
  fun updateMenuItems(menu: Menu, isLoggedIn: Boolean) {
    val loginItem = menu.findItem(R.id.menu_login)
    val mypageItem = menu.findItem(R.id.menu_mypage)

    // Only update visibility if the item is not currently being handled
    if (loginItem.itemId != currentlyHandledItemId) {
      loginItem.isVisible = !isLoggedIn
    }
    if (mypageItem.itemId != currentlyHandledItemId) {
      mypageItem.isVisible = isLoggedIn
    }
  }

  var currentlyHandledItemId = -1
}
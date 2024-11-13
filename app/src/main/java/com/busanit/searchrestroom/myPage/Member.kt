package com.busanit.searchrestroom.myPage

import android.media.Image
import android.provider.MediaStore.Images

class Member(
    val memberId: Int,
    val nickname: String,
    val email: String,
    val profile: Images
)
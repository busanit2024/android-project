package com.busanit.searchrestroom.myPage

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.room.Room
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.dao.MemberDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Member
import com.busanit.searchrestroom.databinding.ActivityEditInfoBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.jvm.Throws

class EditInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditInfoBinding
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    // 안드로이드 10 이상이라면 WRITE_EXTERNAL_STORAGE는 필요하지 않을 수 있다.
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var currentPhotoPath: String
    private var currentMember: Member? = null // 로그인한 사용자의 정보
    private var photoUri: Uri? = null
    private lateinit var sharedPreferences: SharedPreferences


    // 카메라로 사진 촬영
    private val requestCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            binding.profileImage.setImageBitmap(bitmap)
        }
    }

    // 갤러리에서 이미지 선택
    private val requestGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        try {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val calRatio = calculateInSampleSize(
                    it.data!!.data!!,
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize)
                )
                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio

                val inputStream = contentResolver.openInputStream(it.data!!.data!!)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream?.close()

                bitmap?.let {
                    val imagePath = saveBitmapToFile(bitmap, "profile_image.jpg")
                    photoUri = Uri.parse(imagePath)
                    binding.profileImage.setImageBitmap(bitmap)
                } ?: run {
                    Log.d("test", "bitmap null")
                    Toast.makeText(this, "이미지 로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("갤러리 미선택", "갤러리에서 이미지를 선택하지 않았습니다.")
                Toast.makeText(this, "이미지를 선택하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("갤러리 오류", "갤러리 선택 중 오류 발생: ${e.message}")
            Toast.makeText(this, "갤러리 선택 중 오류 발생!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String): String {
        val file = File(filesDir, fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sharedPreferences에서 memberId를 가져오기
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val memberId = AuthHelper.getMemberId()

        // 로그인한 사용자의 정보를 DB에서 가져오기
        loadMemberInfo(memberId)

        // 프로필 이미지 클릭 시 권한 체크
        binding.profileImage.setOnClickListener {
            if (checkPermission()) {
                showImagePickerDialog()
            } else {
                requestPermissions()
            }
        }

        // 정보 업데이트 버튼 클릭 시
        binding.infoUpdate.setOnClickListener {
            validateAndUpdateInfo()
        }

        // X 버튼 클릭 시
        findViewById<ImageView>(R.id.X).setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadMemberInfo(memberId: Int) {
        // Coroutine을 사용해 데이터베이스에서 회원 정보 불러오기
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@EditInfoActivity)

            // currentMember가 null일 경우 ID를 사용하여 멤버 정보를 가져옴
            if (memberId != -1) {
                currentMember = db!!.memberDao().getMemberById(memberId)
            }

            withContext(Dispatchers.Main) {
                // currentMember가 null이 아닌 경우 처리
                if (currentMember != null) {
                    Log.d("test", currentMember?.email.toString())
                    Log.d("test", currentMember?.nickname.toString())
                    // UI 업데이트
                    binding.email.setText(currentMember?.email)
                    binding.email.isEnabled = false // 이메일은 수정 불가
                    binding.newNickname.setText(currentMember?.nickname)
                    setProfileImage()
                } else {
                    Log.d("test", "No member found with the given ID.")
                }
            }
        }
    }

    private fun validateAndUpdateInfo() {
        val currentPassword = binding.currentPassword.text.toString()
        val newPassword = binding.newPassword.text.toString()
        val newNickname = binding.newNickname.text.toString()

        // 기존 비밀번호 확인
        if (currentPassword.isEmpty()) {
            Toast.makeText(this, "기존 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        } else {
            if (currentPassword != currentMember?.password) {
                Toast.makeText(this, "기존 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // 바꿀 비밀번호가 기존 비밀번호와 같으면 리턴(빈 값일 때는 그냥 넘김)
         if (newPassword == currentPassword) {
            Toast.makeText(this, "새 비밀번호는 기존 비밀번호와 달라야 합니다!", Toast.LENGTH_SHORT).show()
            return
        }

        // 데이터베이스 업데이트 로직
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@EditInfoActivity)

            db!!.memberDao().updateNickname(currentMember!!.memberId, newNickname)    // 닉네임 업데이트
            if (newPassword.isNotEmpty()) {
                if (newPassword != currentPassword) {
                    db!!.memberDao().updatePassword(currentMember!!.memberId, newPassword)  // 비밀번호 업데이트
                    FirebaseAuth.getInstance().currentUser?.updatePassword(newPassword)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this@EditInfoActivity, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@EditInfoActivity, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this@EditInfoActivity, "새 비밀번호는 기존 비밀번호와 달라야 합니다!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
            }

            if (photoUri != null) {
                db!!.memberDao().updateProfilePic(currentMember!!.memberId, photoUri.toString())
                with(sharedPreferences.edit()) {
                    putString("profileImageUri", photoUri.toString())
                    apply()
                }
            }


            withContext(Dispatchers.Main) {

                Toast.makeText(this@EditInfoActivity, "정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)    // 결과 설정
                finish()  // 수정 후 액티비티 종료
            }
        }
    }

    private fun setProfileImage() {
        val uriString = sharedPreferences.getString("profileImageUri", null)
        if (uriString != null) {
            val uri = Uri.parse(uriString)
            binding.profileImage.setImageURI(uri)
        } else {
            binding.profileImage.setImageResource(R.drawable.profile)
        }
    }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)


        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(CAMERA_PERMISSION),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Log.d("PermissionResult", "권한 요청 결과: $grantResults")
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showImagePickerDialog()
            } else {

                Toast.makeText(this, "권한이 필요합니다!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun showImagePickerDialog() {
        val options = arrayOf("카메라로 촬영", "갤러리에서 선택")
        AlertDialog.Builder(this)
            .setTitle("프로필 이미지 설정")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> launchCamera()
                    1 -> {
                        try {
                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            galleryIntent.type = "image/*"
                            requestGalleryLauncher.launch(galleryIntent)
                        } catch (e: Exception) {
                            Log.e("갤러리 오류", "갤러리에서 이미지 선택 중 오류 발생: ${e.message}")
                            Toast.makeText(this, "갤러리에서 이미지 선택 중 오류 발생!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .show()
    }

    private fun launchCamera() {
        if (!checkPermission()) {
            requestPermissions()
            return
        }

            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    // 이미지 파일 생성
                    val photoFile: File? = createImageFile()
                    if (photoFile != null) {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.busanit.searchrestroom.fileprovider",
                            photoFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        requestCameraLauncher.launch(takePictureIntent)
                        photoUri = photoURI
                    } else {
                        Toast.makeText(this, "이미지 파일 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Log.e("카메라 오류", "카메라 촬영 중 오류 발생: ${e.message}")
                Toast.makeText(this, "카메라 촬영 중 오류 발생!", Toast.LENGTH_SHORT).show()
            }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        try {
            val inputStream = contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
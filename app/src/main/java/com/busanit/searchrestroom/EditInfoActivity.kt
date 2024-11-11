package com.busanit.searchrestroom

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.busanit.searchrestroom.dao.MemberDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Member
import com.busanit.searchrestroom.databinding.ActivityEditInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditInfoBinding
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    private val WRITE_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var currentPhotoPath: String
    private var currentMember: Member? = null // 로그인한 사용자의 정보
    private lateinit var memberDao: MemberDao

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
            val calRatio = calculateInSampleSize(
                it.data!!.data!!,
                resources.getDimensionPixelSize(R.dimen.imgSize),
                resources.getDimensionPixelSize(R.dimen.imgSize)
            )
            val option = BitmapFactory.Options()
            option.inSampleSize = calRatio

            val inputStream = contentResolver.openInputStream(it.data!!.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
            inputStream!!.close()

            bitmap?.let {
                binding.profileImage.setImageBitmap(bitmap)
            } ?: let {
                Log.d("test", "bitmap null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인한 사용자의 정보를 DB에서 가져오기
        loadMemberInfo()

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

    private fun loadMemberInfo() {
        // Coroutine을 사용해 데이터베이스에서 회원 정보 불러오기
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                this@EditInfoActivity,
                AppDatabase::class.java, "app_database"
            ).build()

            val member: Member = Member(1, "test@naver.com", "둘리", "1234", null, null, null, false, true)

//            db.memberDao().insert(member)

            val memberDao = db.memberDao()
            currentMember = memberDao.getMemberById(1)  // member_id가 1일 때 한정(나중에 수정)

            withContext(Dispatchers.Main) {
                Log.d("test", currentMember?.email.toString())
                Log.d("test", currentMember?.nickname.toString())
                // UI 업데이트
                binding.email.setText(currentMember?.email)
                binding.email.isEnabled = false // 이메일은 수정 불가
                binding.newNickname.setText(currentMember?.nickname)
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

        // 바꿀 비밀번호가 비어있거나 기존 비밀번호와 같으면 리턴
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "새 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        } else if (newPassword == currentPassword) {
            Toast.makeText(this, "새 비밀번호는 기존 비밀번호와 달라야 합니다!", Toast.LENGTH_SHORT).show()
            return
        }

        // 데이터베이스 업데이트 로직
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@EditInfoActivity)
            db.memberDao().updateNickname(currentMember!!.memberId, newNickname)    // 닉네임 업데이트
            if (newPassword.isNotEmpty()) {
                if (newPassword != currentPassword) {
                    db.memberDao().updatePassword(currentMember!!.memberId, newPassword)  // 비밀번호 업데이트
                } else {
                    Toast.makeText(this@EditInfoActivity, "새 비밀번호는 기존 비밀번호와 달라야 합니다!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
            } else {
                Toast.makeText(this@EditInfoActivity, "새 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditInfoActivity, "정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                finish()  // 수정 후 액티비티 종료
            }
        }
    }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)
        val readStoragePermission = ContextCompat.checkSelfPermission(this, READ_STORAGE_PERMISSION)
        val writeStoragePermission = ContextCompat.checkSelfPermission(this, WRITE_STORAGE_PERMISSION)

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                readStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(CAMERA_PERMISSION, READ_STORAGE_PERMISSION, WRITE_STORAGE_PERMISSION),
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

    private fun showImagePickerDialog() {
        val options = arrayOf("카메라로 촬영", "갤러리에서 선택")
        AlertDialog.Builder(this)
            .setTitle("프로필 이미지 설정")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(packageManager) != null) {
                            // 이미지 파일 생성
                            val photoFile: File? = createImageFile()
                            photoFile?.also {
                                val photoURI: Uri = FileProvider.getUriForFile(
                                    this,
                                    "com.busanit.searchrestroom.fileprovider",
                                    it
                                )
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                requestCameraLauncher.launch(takePictureIntent)
                            }
                        } else {
                            Toast.makeText(this, "카메라 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryIntent.type = "image/*"
                        requestGalleryLauncher.launch(galleryIntent)
                    }
                }
            }
            .show()
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_$timeStamp",  /* prefix */
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

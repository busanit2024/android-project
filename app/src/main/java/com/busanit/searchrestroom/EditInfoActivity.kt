package com.busanit.searchrestroom

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.busanit.searchrestroom.databinding.ActivityEditInfoBinding

class EditInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditInfoBinding
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    private val WRITE_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val PERMISSION_REQUEST_CODE = 100

    // 카메라로 사진 촬영
    private val requestCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val bitmap = result.data?.extras?.get("data") as Bitmap
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
            Toast.makeText(this, "정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        // X 버튼 클릭 시
        findViewById<ImageView>(R.id.X).setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
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
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showImagePickerDialog()
            } else {
                Toast.makeText(this, "권한이 필요합니다!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun showImagePickerDialog() {
        // 카메라와 갤러리를 선택할 수 있는 다이얼로그 호출
        val options = arrayOf("카메라로 촬영", "갤러리에서 선택")
        AlertDialog.Builder(this)
            .setTitle("프로필 이미지 설정")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        requestCameraLauncher.launch(cameraIntent)
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

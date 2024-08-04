package com.irempamukcu.picturestosketch

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.irempamukcu.picturestosketch.databinding.ActivityAddArtBinding
import java.io.ByteArrayOutputStream
import java.lang.Exception

class AddArt : AppCompatActivity() {
    private lateinit var binding: ActivityAddArtBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher()
        binding.cover.setOnClickListener {
            addArt(binding.root)
        }

        binding.button.setOnClickListener{
            saveArt(binding.root)
        }
    }

    private fun addArt(view : View) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view, "Galeri için izin gereklidir.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver") {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view, "Galeri için izin gereklidir.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    val imageData = intentFromResult.data
                    if(imageData != null){
                        try{
                            selectedBitmap = if(Build.VERSION.SDK_INT >= 28){
                                val source = ImageDecoder.createSource(this@AddArt.contentResolver, imageData)
                                ImageDecoder.decodeBitmap(source)
                            } else {
                                MediaStore.Images.Media.getBitmap(contentResolver, imageData)
                            }
                            binding.cover.setImageBitmap(selectedBitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(this@AddArt, "İzinler gereklidir", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveArt(view : View) {
        val title = binding.addTitle.text.toString()
        val detail = binding.addDetail.text.toString()


        if(selectedBitmap != null){

            val outputStream = ByteArrayOutputStream()
            selectedBitmap!!.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                val db = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
                db.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, arttitle VARCHAR, artdetail VARCHAR, artimage BLOB)")
                val dataString = "INSERT INTO arts(arttitle, artdetail, artimage) VALUES (?,?,?)"

                val statement = db.compileStatement(dataString)
                statement.bindString(1,title)
                statement.bindString(2,detail)
                statement.bindBlob(3,byteArray)
                statement.execute()

            }catch (e : Exception){
                e.printStackTrace()
            }

            val intent = Intent(this@AddArt,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }
    }

}

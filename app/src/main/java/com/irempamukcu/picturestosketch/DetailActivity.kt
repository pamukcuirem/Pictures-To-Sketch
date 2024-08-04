package com.irempamukcu.picturestosketch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.irempamukcu.picturestosketch.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val picID = intent.getIntExtra(PIC_EXTRA_ID, -1)
        val pic = picFromID(picID)

        if (pic != null) {
            binding.cover.setImageBitmap(pic.picture)
            binding.title.text = pic.title
            binding.detail.text = pic.description
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.litterbin.setOnClickListener {
            if (pic != null) {
                deleteImage(pic.id)
            }
        }
    }

    private fun picFromID(picID: Int): Pic? {
        return picList.find { it.id == picID }
    }

    private fun deleteImage(picID: Int) {
        try {
            val db = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null)
            db.execSQL("DELETE FROM arts WHERE id = $picID")


            picList.removeAll { it.id == picID }

            Toast.makeText(this, "Resim silindi", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Resim silme sorunu", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.irempamukcu.picturestosketch

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.irempamukcu.picturestosketch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), PicClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CardAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.pic_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_pic) {
            val intent = Intent(this@MainActivity, AddArt::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        data()

        binding.recyclerViewPics.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = CardAdapter(picList, this@MainActivity).also {
                this@MainActivity.adapter = it
            }
        }
    }


    override fun onResume() {
        super.onResume()

        data()
        adapter.notifyDataSetChanged()
    }


    override fun onClick(pic: Pic) {
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra(PIC_EXTRA_ID, pic.id)
        startActivity(intent)
    }

    private fun data() {
        try {
            picList.clear() // Clear the list before adding new data
            val db = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null)
            val cursor = db.rawQuery("SELECT * FROM arts", null)
            val arttitleCursor = cursor.getColumnIndex("arttitle")
            val artdetailCursor = cursor.getColumnIndex("artdetail")
            val artimageCursor = cursor.getColumnIndex("artimage")
            val idCursor = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val title = cursor.getString(arttitleCursor)
                val detail = cursor.getString(artdetailCursor)
                val id = cursor.getInt(idCursor)
                val byteArray = cursor.getBlob(artimageCursor)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                val art = Pic(bitmap, title, detail, id)
                picList.add(art)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}

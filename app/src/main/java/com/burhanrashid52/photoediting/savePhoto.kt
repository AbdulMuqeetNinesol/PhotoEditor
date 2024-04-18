package com.burhanrashid52.photoediting

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import editphotos.backgroundremover.photobackgroundchanger.R
import editphotos.backgroundremover.photobackgroundchanger.databinding.ActivitySavePhotoBinding
import java.io.File

class savePhoto : AppCompatActivity() {
private val binding: ActivitySavePhotoBinding by lazy {
    ActivitySavePhotoBinding.inflate(layoutInflater)
}
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoList : ArrayList<Photo>
    private lateinit var photoAdapter: PhotoAdapter
    private  var directoryPath="/storage/emulated/0/Pictures"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        photoList = getImageListFromDirectory(directoryPath)
        photoAdapter=PhotoAdapter(photoList)
        photoAdapter.updateData(photoList)

        // Notify the adapter that the data has changed
        photoAdapter.notifyDataSetChanged()
        Log.d("TAG", "onCreate: $photoList")
          binding.recyclerView.apply {

              if(photoList.isEmpty()){
                  binding.textAvailable.visibility= View.VISIBLE
              }else{
                  binding.textAvailable.visibility= View.GONE
              }
              hasFixedSize()
              layoutManager=GridLayoutManager(this@savePhoto,3)
              adapter=photoAdapter
          }
        binding.finishh.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        photoList = getImageListFromDirectory(directoryPath)

        photoAdapter=PhotoAdapter(photoList)
        binding.recyclerView.apply {
            if(photoList.isEmpty()){
                binding.textAvailable.visibility= View.VISIBLE
            }else{
                binding.textAvailable.visibility= View.GONE
            }
            hasFixedSize()
            layoutManager=GridLayoutManager(this@savePhoto,3)
            adapter=photoAdapter

        }
        super.onResume()
    }

    private fun init() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this,2)

//        addDataToList()
        photoAdapter = PhotoAdapter(photoList)
        recyclerView.adapter = photoAdapter
    }
    private fun getImageListFromDirectory(directoryPath: String): ArrayList<Photo> {
        val photoList = ArrayList<Photo>()
        val folder = File(directoryPath)
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                if (file.isFile && isImageFile(file.path)) {
                    val uri = Uri.fromFile(File(file.path))
                    photoList.add(Photo(uri))
                }
            }
        }
        return photoList
    }

    private fun isImageFile(filePath: String): Boolean {
        val extension = filePath.substringAfterLast('.')
        return arrayOf("jpg", "jpeg", "png", "bmp", "gif").contains(extension.toLowerCase())
    }

//    private fun addDataToList(){
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.original))
//        photoList.add(Photo(R.drawable.original))
//        photoList.add(Photo(R.drawable.original))
//        photoList.add(Photo(R.drawable.original))
//        photoList.add(Photo(R.drawable.original))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//        photoList.add(Photo(R.drawable.`object`))
//    }
}
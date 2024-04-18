package com.burhanrashid52.photoediting

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.RecyclerView
import editphotos.backgroundremover.photobackgroundchanger.R
import java.io.File

class PhotoAdapter(private var photoList: ArrayList<Photo>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private var variable : Int = 1

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val nn : TextView = itemView.findViewById(R.id.nn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_layout_list_item,parent,false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

       val photo = photoList[position]
        holder.imageView.setImageURI(photo.imageUri)
//        holder.nn.setOnClickListener {
//            removeItem(position)
//        }

        holder.imageView.setOnClickListener {
            val intent = Intent(holder.itemView.context, Framethree::class.java)
            intent.putExtra("imageUri", photo.imageUri.toString())
            holder.itemView.context.startActivity(intent)
        }


        holder.nn.setText("photo "+variable++)
        holder.nn.setOnClickListener {
            val intent = Intent(holder.itemView.context, Framethree::class.java)
            intent.putExtra("imageUri", photo.imageUri.toString())
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    private fun removeItem(position: Int) {

        val fileToDelete = File(photoList[position].imageUri.path!!)
        if (fileToDelete.exists()) {
            fileToDelete.delete()
        }
        photoList.removeAt(position)
        notifyItemRemoved(position)
    }


    fun updateData(newDataList: ArrayList<Photo>) {
        photoList = newDataList
    }
}
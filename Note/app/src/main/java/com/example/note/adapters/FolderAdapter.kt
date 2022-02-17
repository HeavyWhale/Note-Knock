package com.example.note.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.database.Folder
import com.example.note.database.Model

object FolderAdapter : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_container_folder, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folders = (Model::getFolders)()
        holder.setFolder(folders[position])
    }

    override fun getItemCount(): Int {
        return (Model::getFolderSize)()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val folderName: TextView = itemView.findViewById(R.id.folderName)

        fun setFolder(folder: Folder) {
            folderName.text = folder.getName()
        }
    }
}
package com.example.note.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.database.Folder
import com.example.note.database.Model

class FolderAdapter(private val listener: OnFolderClickListener) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

//    private lateinit var folderListener: FolderListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_container_folder, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folders = (Model::getFolders)()
        holder.setFolder(folders[position])
//        holder.layoutFolder.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View?) {
//                folderListener.onFolderClicked(folders[position], position)
//            }
//        })
    }

    override fun getItemCount(): Int {
        return (Model::getFolderSize)()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val folderName: TextView = itemView.findViewById(R.id.folderName)
        val folderSize: TextView = itemView.findViewById(R.id.folderSize)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val folders = (Model::getFolders)()
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onFolderClick(folders[position], position)
            }
        }

        fun setFolder(folder: Folder) {
            folderName.text = folder.getName()
            folderSize.text = folder.getNotesSize().toString()
        }
    }

    interface OnFolderClickListener {
        fun onFolderClick(folder: Folder, position: Int)
    }
}
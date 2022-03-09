package com.example.note.FolderList

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.entities.Folder

class FolderAdapter(private val onClick: (Folder) -> Unit) :
    ListAdapter<Folder, FolderAdapter.FolderViewHolder>(FoldersDiffCallback) {

    class FolderViewHolder(itemView: View, val onClick: (Folder) -> Unit) :
        RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener  {

        private val folderName: TextView = itemView.findViewById(R.id.folderName)
        private val folderSize: TextView = itemView.findViewById(R.id.folderSize)
        private var currentFolder: Folder? = null

        init {
            itemView.setOnClickListener { currentFolder?.let { onClick(it) } }
            itemView.setOnCreateContextMenuListener(this)
        }

        fun bind(folder: Folder) {
            with (folder) {
                currentFolder = this
                folderName.text = name
                folderSize.text = Model.getNotesCountByFolderID(id).toString()
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu.add(0, 1, bindingAdapterPosition, R.string.delete)   // 0=?, 1=id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_container_folder, parent, false)
        return FolderViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object FoldersDiffCallback : DiffUtil.ItemCallback<Folder>() {
    override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem.name == newItem.name
    }
}
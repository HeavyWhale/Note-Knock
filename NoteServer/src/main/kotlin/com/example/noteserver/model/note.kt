package com.example.noteserver.model

import javax.persistence.*

@Entity
@Table(name = "NOTES")
data class Note(
    @Id val id: Int?,
    var title: String = "",
    var body: String = "",
    var createTime: Long,
    var modifyTime: Long,
    var image: String,
    var folderID: Int = -1
)

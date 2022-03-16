package com.example.noteserver.model

import javax.persistence.*

@Entity
@Table(name = "NOTES")
data class Note(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    var title: String = "",
    var body: String = "",
    @Column(updatable = false)
    var createTime: Long,
    var modifyTime: Long,
    var folderID: Int = -1
)

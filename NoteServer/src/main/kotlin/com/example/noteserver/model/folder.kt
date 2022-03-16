package com.example.noteserver.model

import javax.persistence.*

@Entity
@Table(name = "FOLDERS")
data class Folder(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    var name: String = ""
)
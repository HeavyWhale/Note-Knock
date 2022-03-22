package com.example.noteserver.model

import javax.persistence.*

@Entity
@Table(name = "FOLDERS")
data class Folder(
    @Id val id: Int?,
    var name: String = ""
)
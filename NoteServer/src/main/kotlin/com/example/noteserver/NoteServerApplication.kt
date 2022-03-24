package com.example.noteserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NoteServerApplication

fun main(args: Array<String>) {
    runApplication<NoteServerApplication>(*args)
}
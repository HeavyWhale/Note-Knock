package com.example.note.database.dao

interface BaseDao {
    enum class OPERATION { INSERT, UPDATE, DELETE, DELETE_BY_FOLDER }

    abstract fun pullFromServer(baseURL: String)
//    abstract fun pushToServer(item: BaseEntity, operation: OPERATION, baseURL: String)
    abstract fun clear()
}
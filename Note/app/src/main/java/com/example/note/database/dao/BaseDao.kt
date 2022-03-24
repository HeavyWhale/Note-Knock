package com.example.note.database.dao

interface BaseDao {
    enum class OPERATION { INSERT, UPDATE, DELETE, MULTIPLE_DELETE }

    abstract fun pullFromServer(baseURL: String)
//    abstract fun pushToServer(item: BaseEntity, operation: OPERATION, baseURL: String)
    abstract fun clear()
}
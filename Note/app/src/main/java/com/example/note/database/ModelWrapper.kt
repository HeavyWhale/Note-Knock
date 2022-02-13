package com.example.note.database

class ModelWrapper {
    companion object {
        var model: Model? = null
    }

    fun Model(): Model {
        if (model == null) model = Model()
        return model as Model
    }
}
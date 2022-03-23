package dev.haenara.sample.core

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

suspend fun main() {
    val cats = MyClass().getCatFromApi()
    println(cats.joinToString(separator = "\n") { it.toString() })
}

class MyClass {
    suspend fun getCatFromApi(): List<Cat> {
        val client = HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
        }

        return client.get<List<Cat>>("https://api.thecatapi.com/v1/images/search?limit=5")
    }

    companion object {
        private val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    }
}

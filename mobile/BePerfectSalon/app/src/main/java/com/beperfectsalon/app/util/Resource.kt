package com.beperfectsalon.app.util

/** Generic wrapper for anything loaded asynchronously (Firestore query, auth call, etc). */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

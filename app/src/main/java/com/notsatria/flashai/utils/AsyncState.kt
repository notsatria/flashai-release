package com.notsatria.flashai.utils

sealed interface AsyncState<out T> {
    data object Loading : AsyncState<Nothing>
    data class Success<T>(val value: T) : AsyncState<T>
    data class Error(val message: String) : AsyncState<Nothing>
}
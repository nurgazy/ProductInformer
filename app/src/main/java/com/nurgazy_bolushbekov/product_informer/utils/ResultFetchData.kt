package com.nurgazy_bolushbekov.product_informer.utils

sealed class ResultFetchData<out T> {
    data class Success<out T>(val data: T) : ResultFetchData<T>()
    data class Error(val exception: Throwable) : ResultFetchData<Nothing>()
    object Loading : ResultFetchData<Nothing>()
}
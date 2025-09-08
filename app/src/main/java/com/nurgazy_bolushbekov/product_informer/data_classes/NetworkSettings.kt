package com.nurgazy_bolushbekov.product_informer.data_classes

import com.nurgazy_bolushbekov.product_informer.utils.Protocol

data class NetworkSettings(
    val protocol: Protocol,
    val serverUrl: String,
    val port: Int,
    val publicationName: String,
    val isFullSpecification: Boolean,
    val baseUrl: String,
    val userName: String,
    val password: String
)

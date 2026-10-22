package com.nurgazy_bolushbekov.product_informer.data_classes

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder

@OptIn(ExperimentalSerializationApi::class)
object CellStockListSerializer : KSerializer<List<CellStockResponse>?> {
    private val delegate = ListSerializer(CellStockResponse.serializer())

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: List<CellStockResponse>?) {
        if (value != null) {
            encoder.encodeSerializableValue(delegate, value)
        } else {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): List<CellStockResponse>? {
        val jsonDecoder = decoder as? JsonDecoder ?: return null
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonArray) {
            jsonDecoder.json.decodeFromJsonElement(delegate, element)
        } else {
            // Если 1С возвращает строку ("Нет в ячейках"), число или primitive вместо массива -> возвращаем null
            null
        }
    }
}

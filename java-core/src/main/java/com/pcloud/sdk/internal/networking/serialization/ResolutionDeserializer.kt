package com.pcloud.sdk.internal.networking.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.pcloud.sdk.Resolution
import java.lang.reflect.Type

internal object ResolutionDeserializer : JsonDeserializer<Resolution> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Resolution {
        if (!json.isJsonPrimitive || !json.asJsonPrimitive.isString) throw JsonParseException("Invalid size value $json.")
        json.asString.let { size ->
            val delimiterIndex = size.indexOf('x')
            if (delimiterIndex == -1) {
                throw JsonParseException("Invalid resolution format: $size")
            }

            try {
                return Resolution(size.substring(0, delimiterIndex).toInt(),
                    size.substring(delimiterIndex + 1).toInt())
            } catch (e: NumberFormatException) {
                throw JsonParseException(e)
            }
        }
    }
}
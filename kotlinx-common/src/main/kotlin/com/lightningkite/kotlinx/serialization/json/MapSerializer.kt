package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.AnyReflection
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.StringReflection

object MapSerializer {
    fun reader(forSerializer: JsonSerializer): JsonTypeReader<Map<*, *>> = { typeInfo ->
        val keySubtype = typeInfo?.typeParameters?.getOrNull(0)?.takeUnless{ it.isStar }?.type ?: KxType(AnyReflection, true)
        val valueSubtype = typeInfo?.typeParameters?.getOrNull(1)?.takeUnless{ it.isStar }?.type ?: KxType(AnyReflection, true)
        val output = LinkedHashMap<Any?, Any?>()
        if(keySubtype.base == StringReflection){
            beginObject {
                while(hasNext()) {
                    val name = nextName()
                    with(forSerializer){
                        output[name] = readAny(valueSubtype)
                    }
                }
            }
        } else {
            beginObject {
                while(hasNext()) {
                    val keyText = nextName()
                    with(forSerializer){
                        val key = JsonReader(keyText.iterator()).readAny<Any>(keySubtype)
                        output[key] = readAny(valueSubtype)
                    }
                }
            }
        }
        output
    }
    fun writer(forSerializer: JsonSerializer): JsonTypeWriter<Map<*, *>> = { typeInfo, value ->
        val keySubtype = typeInfo?.typeParameters?.getOrNull(0)?.takeUnless{ it.isStar }?.type ?: KxType(AnyReflection, true)
        val valueSubtype = typeInfo?.typeParameters?.getOrNull(1)?.takeUnless{ it.isStar }?.type ?: KxType(AnyReflection, true)
        if(keySubtype.base == StringReflection) {
            writeObject {
                for(entry in value){
                    writeEntry(entry.key as String){
                        with(forSerializer){
                            val entryValue = entry.value
                            if(entryValue == null)
                                writeNull()
                            else
                                writeAny(entryValue, valueSubtype)
                        }
                    }
                }
            }
        } else {
            writeObject {
                for(entry in value){
                    writeEntry(entry.key as String){
                        with(forSerializer){
                            val entryValue = entry.value
                            if(entryValue == null)
                                writeNull()
                            else
                                writeAny(entryValue, valueSubtype)
                        }
                    }
                }
            }
        }
    }
}
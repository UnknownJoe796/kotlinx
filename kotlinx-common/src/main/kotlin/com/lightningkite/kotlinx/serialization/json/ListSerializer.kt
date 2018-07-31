package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.AnyReflection
import com.lightningkite.kotlinx.reflection.KxType

object ListSerializer {
    fun reader(forSerializer: JsonSerializer): JsonTypeReader<List<*>> = { typeInfo ->
        val subtype = typeInfo?.typeParameters?.firstOrNull()?.takeUnless{ it.isStar }?.type ?: KxType(AnyReflection, true)
        val output = ArrayList<Any?>()
        beginArray {
            while(hasNext()) {
                with(forSerializer){
                    output += readAny<Any>(subtype)
                }
            }
        }
        output
    }
    fun <T> reader(forSerializer: JsonSerializer, transform:(ArrayList<Any?>)->T): JsonTypeReader<T> = { typeInfo ->
        val subtype = typeInfo?.typeParameters?.firstOrNull()?.takeUnless{ it.isStar }?.type ?: KxType(AnyReflection, true)
        val output = ArrayList<Any?>()
        beginArray {
            while(hasNext()) {
                with(forSerializer){
                    output += readAny<Any>(subtype)
                }
            }
        }
        transform(output)
    }
    fun writer(forSerializer: JsonSerializer): JsonTypeWriter<List<*>> = { typeInfo, value ->
        val subtype = typeInfo?.typeParameters?.firstOrNull()?.takeUnless{ it.isStar }?.type ?: KxType(AnyReflection, true)
        if(subtype.nullable) {
            writeArray {
                writeEntry {
                    for(item in value){
                        with(forSerializer){
                            if(item == null)
                                writeNull()
                            else
                                writeAny(item, subtype)
                        }
                    }
                }
            }
        } else {
            writeArray {
                writeEntry {
                    for(item in value){
                        with(forSerializer){
                            writeAny(item!!, subtype)
                        }
                    }
                }
            }
        }
    }
}
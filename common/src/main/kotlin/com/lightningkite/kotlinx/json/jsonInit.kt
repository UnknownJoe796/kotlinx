package com.lightningkite.kotlinx.json

import com.lightningkite.kotlinx.locale.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.JSON

val MyJson = JSON(nonstrict = true, context = SerialContext()).apply{
    context!!.registerSerializer(Unit::class, object : KSerializer<Unit>{
        override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("kotlin.Unit")
        override fun load(input: KInput){}
        override fun save(output: KOutput, obj: Unit) {}
    })
    context!!.registerSerializer(Date::class, DateSerializer)
    context!!.registerSerializer(Time::class, TimeSerializer)
    context!!.registerSerializer(TimeStamp::class, TimeStampSerializer)
}
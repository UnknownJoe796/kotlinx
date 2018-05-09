package com.lightningkite.kotlinx.json

import com.lightningkite.kotlinx.locale.Date
import com.lightningkite.kotlinx.locale.Time
import com.lightningkite.kotlinx.locale.TimeStamp
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl

object DateSerializer : KSerializer<Date>{
    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("com.lightningkite.kotlinx.locale.Date")

    override fun load(input: KInput): Date {
        return Date(input.readIntValue())
    }

    override fun save(output: KOutput, obj: Date) {
        output.writeIntValue(obj.daysSinceEpoch)
    }
}

object TimeSerializer : KSerializer<Time>{
    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("com.lightningkite.kotlinx.locale.Time")

    override fun load(input: KInput): Time {
        return Time(input.readIntValue())
    }

    override fun save(output: KOutput, obj: Time) {
        output.writeIntValue(obj.millisecondsSinceMidnight)
    }
}

object TimeStampSerializer : KSerializer<TimeStamp>{
    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("com.lightningkite.kotlinx.locale.TimeStamp")

    override fun load(input: KInput): TimeStamp {
        return TimeStamp(input.readLongValue())
    }

    override fun save(output: KOutput, obj: TimeStamp) {
        output.writeLongValue(obj.millisecondsSinceEpoch)
    }
}
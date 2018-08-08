package com.lightningkite.kotlinx.serialization

interface AnyReaderWriter<IN, OUT, RESULT> : AnyReader<IN>, AnyWriter<OUT, RESULT>
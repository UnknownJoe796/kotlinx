package com.lightningkite.kotlinx.serialization

interface TypeReaderObtainerWriterRepository<IN, OUT, RESULT> : TypeReaderRepository<IN>, TypeWriterRepository<OUT, RESULT>
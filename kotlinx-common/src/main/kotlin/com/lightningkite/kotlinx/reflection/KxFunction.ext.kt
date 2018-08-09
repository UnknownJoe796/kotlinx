package com.lightningkite.kotlinx.reflection

fun <T> KxFunction<T>.callGiven(map: Map<String, Any?>): T {
    //Compile the list of arguments for the constructor
    val orderedArguments = ArrayList<Any?>()
    for (arg in arguments) {
        val value = map[arg.name] ?: arg.default!!.invoke(orderedArguments)
        orderedArguments.add(value)
    }
    return call(orderedArguments)
}
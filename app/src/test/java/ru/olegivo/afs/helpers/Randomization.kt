package ru.olegivo.afs.helpers

import java.util.*
import kotlin.random.Random

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun getRandomString(lenght: Int? = 4, minLength: Int = 1, maxLength: Int = 10): String {
    val length = lenght ?: (Random.nextInt(minLength, maxLength))
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}

fun getRandomBoolean() = Random.nextBoolean()
fun getRandomInt() = Random.nextInt()
fun getRandomLong() = Random.nextLong()
fun getRandomDate() = Date(getRandomLong())
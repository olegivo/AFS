/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *  
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.helpers

import java.util.Date
import kotlin.random.Random

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun getRandomString(
    lenght: Int? = 4,
    minLength: Int = 1,
    maxLength: Int = 10,
    prefix: String = "",
    postfix: String = ""
): String {
    val length = lenght ?: (Random.nextInt(minLength, maxLength))
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString(separator = "", prefix = prefix, postfix = postfix)
}

fun getRandomBoolean() = Random.nextBoolean()
fun getRandomInt(from: Int? = null, until: Int? = null) =
    until?.let { u ->
        from?.let { f ->
            Random.nextInt(from = f, until = u)
        } ?: Random.nextInt(until = u)
    } ?: Random.nextInt()

fun getRandomLong() = Random.nextLong()
fun getRandomDate() = Date(getRandomLong())

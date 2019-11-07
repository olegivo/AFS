package ru.olegivo.afs

import ru.olegivo.afs.helpers.getRandomBoolean

fun <T> (() -> T).repeat(times: Int) = (1..times).map { this() }

fun <T> List<T>.randomSubList() = filter { getRandomBoolean() }
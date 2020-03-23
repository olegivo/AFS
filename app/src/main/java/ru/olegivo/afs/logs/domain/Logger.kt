package ru.olegivo.afs.logs.domain

interface Logger {
    fun log(message: String, tag: String?)
}
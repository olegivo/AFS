package ru.olegivo.afs

abstract class BaseTestOf<T : Any> : BaseTest() {
    protected abstract fun createInstance(): T

    protected val instance: T by lazy { createInstance() }
}
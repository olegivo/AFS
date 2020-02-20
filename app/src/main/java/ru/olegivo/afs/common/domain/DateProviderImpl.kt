package ru.olegivo.afs.common.domain

import java.util.*
import javax.inject.Inject

class DateProviderImpl @Inject constructor() : DateProvider {
    override fun getDate() = Date()
}

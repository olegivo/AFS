package ru.olegivo.afs.schedule.network.models

import com.squareup.moshi.JsonClass
import ru.olegivo.afs.common.domain.ErrorWrapper

@JsonClass(generateAdapter = true)
data class ReserveRequestError(
    val result: String,
    val errors: List<String>,
    val code: Int
): ErrorWrapper {
    override fun getError(): String {
        return errors.joinToString("\n")
    }
}
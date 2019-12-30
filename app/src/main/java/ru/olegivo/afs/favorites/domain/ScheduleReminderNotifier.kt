package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.schedules.domain.models.Schedule

interface ScheduleReminderNotifier {
    fun showNotificationToShowDetails(schedule: Schedule): Completable
    fun showNotificationToReserve(
        schedule: Schedule,
        fio: String,
        phone: String
    ): Completable
    fun showAlreadyReserved(schedule: Schedule): Completable
    fun showHasNoSlotsAPosteriori(schedule: Schedule): Completable
    fun showHasNoSlotsAPriori(schedule: Schedule): Completable
    fun showTheTimeHasGone(schedule: Schedule): Completable
    fun showSuccessReserved(schedule: Schedule): Completable
}

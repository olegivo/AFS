package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.schedules.domain.models.Schedule

interface ScheduleReminderNotifier {
    fun showNotification(schedule: Schedule): Completable
}

package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.schedules.domain.models.Schedule

interface FavoriteAlarmPlanner {
    fun planFavoriteRecordReminder(schedule: Schedule): Completable
}
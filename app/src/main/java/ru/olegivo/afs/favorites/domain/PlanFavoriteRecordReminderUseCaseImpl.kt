package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject

class PlanFavoriteRecordReminderUseCaseImpl @Inject constructor(
    private val favoriteAlarmPlanner: FavoriteAlarmPlanner
) : PlanFavoriteRecordReminderUseCase {

    override fun invoke(schedule: Schedule): Completable =
        favoriteAlarmPlanner.planFavoriteRecordReminder(schedule)

}

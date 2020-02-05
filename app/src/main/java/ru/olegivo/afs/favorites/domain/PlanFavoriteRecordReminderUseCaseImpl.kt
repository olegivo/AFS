package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.schedules.domain.models.Schedule
import javax.inject.Inject

class PlanFavoriteRecordReminderUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val favoriteAlarmPlanner: FavoriteAlarmPlanner,
    private val dateProvider: DateProvider
) : PlanFavoriteRecordReminderUseCase {

    override fun invoke(schedule: Schedule): Completable =
        { dateProvider.getDate() }.toSingle()
            .flatMapCompletable { now ->
                val threshold = schedule.recordTo ?: schedule.datetime
                if (threshold > now) {
                    favoritesRepository.hasPlannedReminderToRecord(schedule)
                        .flatMapCompletable { hasPlannedReminderToRecord ->
                            if (hasPlannedReminderToRecord) {
                                Completable.complete()
                            } else {
                                favoritesRepository.addReminderToRecord(schedule)
                                    .andThen(Completable.defer {
                                        favoriteAlarmPlanner.planFavoriteRecordReminder(schedule)
                                    })
                            }
                        }
                } else {
                    Completable.complete()
                }
            }

}

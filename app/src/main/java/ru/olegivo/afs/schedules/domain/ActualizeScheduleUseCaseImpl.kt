package ru.olegivo.afs.schedules.domain

import io.reactivex.Completable
import io.reactivex.Scheduler
import ru.olegivo.afs.favorites.domain.FavoritesRepository
import ru.olegivo.afs.favorites.domain.models.filterByFavorites
import ru.olegivo.afs.favorites.domain.PlanFavoriteRecordReminderUseCase
import javax.inject.Inject
import javax.inject.Named

class ActualizeScheduleUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository,
    private val planFavoriteRecordReminder: PlanFavoriteRecordReminderUseCase,
    @Named("computation") private val computationScheduler: Scheduler
) :
    ActualizeScheduleUseCase {

    override fun invoke(clubId: Int): Completable =
        scheduleRepository.actualizeSchedules(clubId)
            .flatMapCompletable { schedules ->
                favoritesRepository.getFavoriteFilters()
                    .observeOn(computationScheduler)
                    .map { favoriteFilters -> schedules.filterByFavorites(favoriteFilters) }
                    .flatMapCompletable { favoriteSchedules ->
                        Completable.concat(
                            favoriteSchedules.map {
                                planFavoriteRecordReminder(it)
                            }
                        )
                    }
            }
}
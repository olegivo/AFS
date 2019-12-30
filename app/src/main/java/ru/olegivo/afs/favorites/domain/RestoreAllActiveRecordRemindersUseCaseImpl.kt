package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.common.domain.DateProvider
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import javax.inject.Inject

class RestoreAllActiveRecordRemindersUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val favoritesRepository: FavoritesRepository,
    private val dateProvider: DateProvider,
    private val scheduleReminderNotifier: ScheduleReminderNotifier
) : RestoreAllActiveRecordRemindersUseCase {

    override fun invoke(): Completable =
        favoritesRepository.getActiveRecordReminderSchedules(dateProvider.getDate())
            .flatMap {
                scheduleRepository.getSchedules(it)
            }
            .flatMapCompletable { schedules ->
                Completable.concat(
                    schedules.map {
                        scheduleReminderNotifier.showNotification(it)
                            .doOnError { throwable ->
                                throwable.printStackTrace() /*TODO: log error*/
                            }
                            .onErrorComplete()
                    }
                )
            }
}
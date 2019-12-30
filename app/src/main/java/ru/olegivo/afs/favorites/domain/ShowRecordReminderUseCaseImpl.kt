package ru.olegivo.afs.favorites.domain

import io.reactivex.Completable
import ru.olegivo.afs.schedules.domain.ScheduleRepository
import javax.inject.Inject

class ShowRecordReminderUseCaseImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleReminderNotifier: ScheduleReminderNotifier
) : ShowRecordReminderUseCase {

    override fun invoke(scheduleId: Long): Completable =
        scheduleRepository.getSchedule(scheduleId)
            .flatMapCompletable { schedule ->
                scheduleReminderNotifier.showNotification(schedule)
            }
}
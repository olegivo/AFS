package ru.olegivo.afs.schedules.domain

import io.reactivex.Completable
import javax.inject.Inject

class ActualizeScheduleUseCaseImpl @Inject constructor(private val scheduleRepository: ScheduleRepository) :
    ActualizeScheduleUseCase {

    override fun invoke(clubId: Int): Completable =
        scheduleRepository.actualizeSchedules(clubId)
            .ignoreElement()
}
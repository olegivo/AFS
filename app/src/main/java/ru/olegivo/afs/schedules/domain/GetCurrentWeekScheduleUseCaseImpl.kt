package ru.olegivo.afs.schedules.domain

import javax.inject.Inject

class GetCurrentWeekScheduleUseCaseImpl @Inject constructor(private val scheduleRepository: ScheduleRepository) :
    GetCurrentWeekScheduleUseCase {
    override fun invoke(clubId: Int) = scheduleRepository.getCurrentWeekSchedule(clubId)
}
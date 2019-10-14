package ru.olegivo.afs.reserve.network

import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import ru.olegivo.afs.clubs.network.ClubsNetworkSourceImpl
import ru.olegivo.afs.common.network.AuthorizedApiTest
import ru.olegivo.afs.reserve.network.models.ReserveRequest
import ru.olegivo.afs.schedule.network.ScheduleNetworkSourceImpl
import java.util.*

class ReserveNetworkSourceImplTest : AuthorizedApiTest() {
    @Test
    fun reserve_WHEN_the_available_slots_is_0() {
        val scheduler = TestScheduler()

        val networkSourceImpl = ClubsNetworkSourceImpl(api, scheduler)
        val scheduleNetworkSource = ScheduleNetworkSourceImpl(api, scheduler, scheduler)
        val reserveNetworkSource = ReserveNetworkSourceImpl(api, scheduler, scheduler)

        val now = Date()
        val testObserver1 = reserveNetworkSource.getSlots(375, listOf(101514102019))
            .test()

        scheduler.triggerActions()

        val single = testObserver1
            .assertNoErrors()
            .values().single()

        val testObserver = networkSourceImpl.getClubs()
            .map { it.first() }
            .flatMapCompletable { club ->
                scheduleNetworkSource
                    .getSchedule(club.id)
                    .flatMapCompletable { schedules ->
                        val sortedBy = schedules.sortedBy { it.datetime }
                        val preEntrySchedules = sortedBy
                            .filter { it.preEntry }
                        //.filter { it.totalSlots == 0 }
                        reserveNetworkSource.getSlots(club.id, preEntrySchedules.map { it.id })
                            .map { slots ->
                                slots.first { it.slots ?: 0 == 0 }.id
                            }
                            .flatMapCompletable { scheduleId ->
                                reserveNetworkSource.reserve(
                                    ReserveRequest(
                                        "Тестович А.Б.",
                                        "79817564213",
                                        scheduleId,
                                        club.id
                                    )
                                )
                            }
                    }
            }
            .test()

        scheduler.triggerActions()

        testObserver
            .assertNoErrors()
            .assertComplete()

    }
}
package ru.olegivo.afs.schedule.network

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.clubs.network.ClubsNetworkSourceImpl
import ru.olegivo.afs.common.network.AuthorizedApiTest
import ru.olegivo.afs.schedule.data.ScheduleNetworkSource
import ru.olegivo.afs.schedule.network.models.Schedules

class ScheduleNetworkSourceImplTest : AuthorizedApiTest() {

    @Test
    fun getSchedule() {
        val scheduler = TestScheduler()
        val testObserver = ClubsNetworkSourceImpl(api, scheduler).getClubs()
            .flatMap { clubs ->
                ScheduleNetworkSourceImpl(api, scheduler)
                    .getSchedule(clubs.first().id)
            }
            .test()

        scheduler.triggerActions()

        val schedules =
            testObserver
                .assertNoErrors()
                .values()
                .single()
        assertThat(schedules.schedule).isNotEmpty
    }

    @Test
    fun `getSchedule_flatten_next`() {
        val scheduler = TestScheduler()
        val scheduleNetworkSource: ScheduleNetworkSource = ScheduleNetworkSourceImpl(api, scheduler)
        val testObserver = ClubsNetworkSourceImpl(api, scheduler).getClubs()
            .flatMap { clubs ->
                scheduleNetworkSource
                    .getSchedule(clubs.first().id)
            }
            .toFlowable()
            .flatMap { schedules ->
                Flowable.create<Schedules>({ emitter ->
                    emitter.onNext(schedules)
                    var current: Schedules? = schedules
                    while (current?.next != null) {
                        Log.d("getSchedule_flatten", "next = ${current.next}")
                        current = scheduleNetworkSource.getNextSchedule(current)
                        current?.let { emitter.onNext(it) }
                    }
                }, BackpressureStrategy.ERROR)
            }
            .test()

        scheduler.triggerActions()

        val schedules =
            testObserver
                .assertNoErrors()
                .values()
        assertThat(schedules).isNotEmpty
    }

    @Test
    fun `getSchedule_flatten_prev`() {
        val scheduler = TestScheduler()
        val scheduleNetworkSource: ScheduleNetworkSource = ScheduleNetworkSourceImpl(api, scheduler)
        val testObserver = ClubsNetworkSourceImpl(api, scheduler).getClubs()
            .flatMap { clubs ->
                scheduleNetworkSource
                    .getSchedule(clubs.first().id)
            }
            .toFlowable()
            .flatMap { schedules ->
                Flowable.create<Schedules>({ emitter ->
                    emitter.onNext(schedules)
                    var current: Schedules? = schedules
                    while (current?.next != null) {
                        Log.d("getSchedule_flatten", "prev = ${current.prev}")
                        current = scheduleNetworkSource.getPrevSchedule(current)
                        current?.let { emitter.onNext(it) }
                    }
                }, BackpressureStrategy.ERROR)
            }
            .test()

        scheduler.triggerActions()

        val schedules =
            testObserver
                .assertNoErrors()
                .values()
        assertThat(schedules).isNotEmpty
    }
}
package ru.olegivo.afs.schedules.network

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.clubs.network.ClubsNetworkSourceImpl
import ru.olegivo.afs.common.network.AuthorizedApiTest
import ru.olegivo.afs.schedules.data.ScheduleNetworkSource
import ru.olegivo.afs.schedules.network.models.Schedules

class ScheduleNetworkSourceImplTest : AuthorizedApiTest() {

    @Test
    fun getSchedule_all_clubs() {
        val scheduler = TestScheduler()
        val flatMapObservable = ClubsNetworkSourceImpl(api, scheduler).getClubs()
            .flatMapObservable { clubs ->
                val scheduleNetworkSource = ScheduleNetworkSourceImpl(api, scheduler, scheduler)
                val observables = Observable.fromIterable(clubs.map { it.id })
                    .map { clubId ->
                        scheduleNetworkSource
                            .getSchedule(clubId)
                            .toObservable()
                    }
                Observable.merge(observables)
            }
        val testObserver = flatMapObservable
            .test()

        scheduler.triggerActions()

        val schedules =
            testObserver
                .assertNoErrors()
                .values()

        assertThat(schedules).isNotEmpty
        schedules.forEach {
            assertThat(it).isNotEmpty
        }
    }

    @Test
    fun getSchedule_flatten_next() {
        val scheduler = TestScheduler()
        val scheduleNetworkSource: ScheduleNetworkSource =
            ScheduleNetworkSourceImpl(api, scheduler, scheduler)
        val testObserver = ClubsNetworkSourceImpl(api, scheduler).getClubs()
            .flatMap { clubs ->
                scheduleNetworkSource
                    .getSchedules(clubs.first().id)
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
    fun getSchedule_flatten_prev() {
        val scheduler = TestScheduler()
        val scheduleNetworkSource: ScheduleNetworkSource =
            ScheduleNetworkSourceImpl(api, scheduler, scheduler)
        val testObserver = ClubsNetworkSourceImpl(api, scheduler).getClubs()
            .flatMap { clubs ->
                scheduleNetworkSource
                    .getSchedules(clubs.first().id)
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

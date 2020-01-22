package ru.olegivo.afs.schedules.db

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import ru.olegivo.afs.extensions.parallelMap
import ru.olegivo.afs.extensions.parallelMapList
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.schedules.data.ScheduleDbSource
import ru.olegivo.afs.schedules.data.models.DataSchedule
import ru.olegivo.afs.schedules.db.models.DictionaryEntry
import ru.olegivo.afs.schedules.db.models.DictionaryKind
import ru.olegivo.afs.schedules.db.models.ReservedSchedule
import ru.olegivo.afs.schedules.db.models.toData
import ru.olegivo.afs.schedules.db.models.toDb
import ru.olegivo.afs.schedules.domain.models.Schedule
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ScheduleDbSourceImpl @Inject constructor(
    private val reserveDao: ReserveDao,
    private val scheduleDao: ScheduleDao,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("computation") private val computationScheduler: Scheduler
) : ScheduleDbSource {

    override fun setScheduleReserved(schedule: Schedule): Completable =
        reserveDao.addReservedSchedule(ReservedSchedule(schedule.id, schedule.datetime))
            .subscribeOn(ioScheduler)

    override fun getReservedScheduleIds(from: Date, until: Date): Single<List<Long>> =
        reserveDao.getReservedScheduleIds(from, until)
            .subscribeOn(ioScheduler)

    override fun getSchedules(clubId: Int, from: Date, until: Date): Maybe<List<DataSchedule>> =
        scheduleDao.getSchedules(clubId, from, until)
            .subscribeOn(ioScheduler)
            .parallelMapList(computationScheduler) { it.toData() }

    override fun getSchedules(ids: List<Long>): Single<List<DataSchedule>> =
        scheduleDao.getSchedules(ids)
            .subscribeOn(ioScheduler)
            .parallelMapList(computationScheduler) { it.toData() }

    override fun putSchedules(schedules: List<DataSchedule>): Completable =
        schedules.toSingle()
            .flatMap { list ->
                getDictionary(list)
                    .flatMap {
                        scheduleDao.putDictionary(it)
                            .andThen(Single.just(list))
                    }
            }
            .parallelMapList(computationScheduler) { it.toDb() }
            .observeOn(ioScheduler)
            .flatMapCompletable {
                scheduleDao.putSchedules(it)
            }

    private fun getDictionary(list: List<DataSchedule>) =
        list.toFlowable()
            .let { flowable ->
                Flowable.concat(
                    flowable.distinctParallelMapDictionary(
                        keySelector = { activityId },
                        valueSelector = { activity },
                        dictionaryKind = DictionaryKind.Activity
                    ),
                    flowable.distinctParallelMapDictionary(
                        keySelector = { groupId },
                        valueSelector = { group },
                        dictionaryKind = DictionaryKind.Group
                    )
                )
            }.toList()

    private fun Flowable<DataSchedule>.distinctParallelMapDictionary(
        keySelector: DataSchedule.() -> Int,
        valueSelector: DataSchedule.() -> String,
        dictionaryKind: DictionaryKind
    ) =
        distinct(keySelector)
            .parallelMap(computationScheduler) {
                DictionaryEntry(
                    dictionaryKind.value,
                    it.keySelector(),
                    it.valueSelector()
                )
            }

    override fun getSchedule(id: Long): Single<DataSchedule> =
        scheduleDao.getSchedule(id)
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .map { it.toData() }

    override fun isScheduleReserved(scheduleId: Long): Single<Boolean> =
        reserveDao.isScheduleReserved(scheduleId)
            .subscribeOn(ioScheduler)

}

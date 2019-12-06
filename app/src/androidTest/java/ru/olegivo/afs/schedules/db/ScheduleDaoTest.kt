package ru.olegivo.afs.schedules.db

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.common.db.AfsDaoTest
import ru.olegivo.afs.helpers.checkSingleValue
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.data.models.createDataSchedule
import ru.olegivo.afs.schedules.db.models.toDb

class ScheduleDaoTest : AfsDaoTest<ScheduleDao>({ schedules }) {
    @Test
    fun getSchedule_RETURNS_putted_entity() {
        val entity = createDataSchedule().toDb()
        dao.putSchedules(listOf(entity))
            .andThen(dao.getSchedule(entity.id))
            .test()
            .checkSingleValue {
                assertThat(it).isEqualTo(entity)
            }
    }

    @Test
    fun getSchedules_RETURNS_putted_only_entities_inside_date_range_WHEN_putted_entities_inside_and_outside_of_date_range() {
        val clubId = getRandomInt()
        val schedules = { createDataSchedule().copy(clubId = clubId).toDb() }.repeat(10)
        val dates = schedules.map { it.datetime }.sorted()
        val from = dates.drop(1).first()
        val until = dates.last()

        dao.putSchedules(schedules)
            .andThen(dao.getSchedules(clubId, from, until))
            .test()
            .checkSingleValue { result ->
                val expected = schedules.filter { it.datetime >= from && it.datetime < until }
                assertThat(result).containsExactlyInAnyOrderElementsOf(expected)
            }
    }
}

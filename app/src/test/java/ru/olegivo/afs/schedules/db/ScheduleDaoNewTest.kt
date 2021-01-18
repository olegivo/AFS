/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.schedules.db

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.common.andThenDeferMaybe
import ru.olegivo.afs.common.db.BaseDaoNewTest
import ru.olegivo.afs.common.toADate
import ru.olegivo.afs.common.toDate
import ru.olegivo.afs.helpers.checkSingleValue
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.randomSubList
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.data.models.createDataSchedule
import ru.olegivo.afs.schedules.db.models.toDb

class ScheduleDaoNewTest : BaseDaoNewTest<ScheduleDaoNew>(
    { afsDatabaseNew, testScheduler ->
        ScheduleDaoNew(afsDatabaseNew, testScheduler)
    }
) {
    @Test
    fun getSchedule_RETURNS_only_relevant_entity() {
        val objects = { createDataSchedule().toDb() }.repeat(4)
        val entity = objects.random()
        dao.upsertCompletable(objects)
            .andThen(dao.getSchedule(entity.id))
            .assertResult {
                assertThat(it).isEqualTo(entity)
            }
    }

    @Test
    fun getSchedules_RETURNS_only_relevant_entities() {
        val objects = { createDataSchedule().toDb() }.repeat(4)
        val subList = objects.randomSubList()
        dao.upsertCompletable(objects)
            .andThen(dao.getSchedules(subList.map { it.id }))
            .assertResult {
                assertThat(it).containsExactlyInAnyOrderElementsOf(subList)
            }
    }

    @Test
    fun getSchedules_RETURNS_only_relevant_entities_inside_date_range() {
        val clubId = getRandomInt()

        val schedules = {
            createDataSchedule()
                .copy(clubId = clubId)
                .toDb()
        }.repeat(10)
        val dates = schedules.map { it.datetime.toDate() }.sorted()
        val from = dates.drop(1).first()
        val until = dates.last()

        val expected =
            schedules.filter { it.datetime.toDate() >= from && it.datetime.toDate() < until }

        dao.upsertCompletable(schedules)
            .andThenDeferMaybe {
                dao.getSchedules(clubId, from.toADate(), until.toADate())
            }
            .test().andTriggerActions()
            .assertNoErrors()
            .checkSingleValue { result ->
                assertThat(result).containsExactlyInAnyOrderElementsOf(expected)
            }
    }

    @Test
    fun filterSchedules_RETURNS_only_relevant_entities() {
        val entity = createDataSchedule().toDb()
        val objects = listOf(
            entity,
            entity.copy(id = entity.id + 1, clubId = entity.clubId + 1),
            entity.copy(id = entity.id + 2, groupId = entity.groupId + 1),
            entity.copy(id = entity.id + 3, activityId = entity.activityId + 1)
        )

        dao.upsertCompletable(objects)
            .andThen(
                dao.filterSchedules(
                    clubId = entity.clubId,
                    groupId = entity.groupId,
                    activityId = entity.activityId
                )
            )
            .assertResult {
                assertThat(it).containsExactly(entity)
            }
    }
}

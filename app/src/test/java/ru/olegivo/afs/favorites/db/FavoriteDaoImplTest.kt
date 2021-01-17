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

package ru.olegivo.afs.favorites.db

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.db.BaseDaoNewTest
import ru.olegivo.afs.common.toADate
import ru.olegivo.afs.shared.favorites.db.models.RecordReminderScheduleEntity
import ru.olegivo.afs.favorites.db.models.createFavoriteFilterEntity
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.repeat
import java.util.Date

class FavoriteDaoImplTest : BaseDaoNewTest<FavoriteDaoImpl>(
    { afsDatabaseNew, _ ->
        FavoriteDaoImpl(afsDatabaseNew)
    }
) {

    @Test
    fun getFavoriteFilters_RETURNS_all() {
        val objects = { createFavoriteFilterEntity().copy(id = getRandomInt()) }.repeat(4)
        dao.upsert(objects)
        runBlocking {
            assertThat(dao.getFavoriteFilters()).containsExactlyInAnyOrderElementsOf(objects)
        }
    }

    @Test
    fun removeFilter_REMOVES_only_relevant() {
        val objects = { createFavoriteFilterEntity().copy(id = getRandomInt()) }.repeat(4)
        val entity = objects.random()
        dao.upsert(objects)
        dao.removeFilter(
            groupId = entity.groupId,
            activityId = entity.activityId,
            dayOfWeek = entity.dayOfWeek,
            minutesOfDay = entity.minutesOfDay
        )
        runBlocking {
            assertThat(dao.getFavoriteFilters()).containsExactlyInAnyOrderElementsOf(objects - entity)
        }
    }

    @Test
    fun exist_RETURNS_true_WHEN_has_relevant() {
        val objects = { createFavoriteFilterEntity().copy(id = getRandomInt()) }.repeat(4)
        val entity = objects.random()
        dao.upsert(objects)
        runBlocking {
            assertThat(
                dao.exist(
                    groupId = entity.groupId,
                    activityId = entity.activityId,
                    dayOfWeek = entity.dayOfWeek,
                    minutesOfDay = entity.minutesOfDay
                )
            ).isTrue
        }
    }

    @Test
    fun exist_RETURNS_false_WHEN_has_no_relevant() {
        val entity = createFavoriteFilterEntity().copy(id = getRandomInt())
        val objects = listOf(
            entity.copy(id = entity.id + 1, groupId = entity.groupId + 1),
            entity.copy(id = entity.id + 2, activityId = entity.activityId + 1),
            entity.copy(id = entity.id + 3, dayOfWeek = entity.dayOfWeek + 1),
            entity.copy(id = entity.id + 4, minutesOfDay = entity.minutesOfDay + 1)
        )
        dao.upsert(objects)
        runBlocking {
            assertThat(
                dao.exist(
                    groupId = entity.groupId,
                    activityId = entity.activityId,
                    dayOfWeek = entity.dayOfWeek,
                    minutesOfDay = entity.minutesOfDay
                )
            ).isFalse
        }
    }

    @Test
    fun getActiveRecordReminderScheduleIds_RETURNS_only_relevant_ids() {
        val moment = Date()
        val entity = RecordReminderScheduleEntity(
            scheduleId = getRandomLong(),
            dateFrom = moment.add(minutes = -1).toADate(),
            dateUntil = moment.add(minutes = 1).toADate()
        )
        val objects = listOf(
            entity,
            // earlier:
            entity.copy(
                scheduleId = entity.scheduleId + 1,
                dateFrom = moment.add(hours = -1).toADate(),
                dateUntil = moment.add(seconds = -1).toADate()
            ),
            // later:
            entity.copy(
                scheduleId = entity.scheduleId + 2,
                dateFrom = moment.add(seconds = 1).toADate(),
                dateUntil = moment.add(hours = 1).toADate()
            )
        )
        objects.forEach { dao.addReminderToRecord(it) }
        runBlocking {
            assertThat(dao.getActiveRecordReminderScheduleIds(moment.toADate())).containsOnly(entity.scheduleId)
        }
    }

    @Test
    fun hasPlannedReminderToRecord_RETURNS_true() {
        val entity = RecordReminderScheduleEntity(
            scheduleId = getRandomLong(),
            dateFrom = getRandomDate().toADate(),
            dateUntil = getRandomDate().toADate()
        )
        dao.addReminderToRecord(entity)
        runBlocking {
            assertThat(dao.hasPlannedReminderToRecord(entity.scheduleId)).isTrue
        }
    }

    @Test
    fun hasPlannedReminderToRecord_RETURNS_false() {
        val entity = RecordReminderScheduleEntity(
            scheduleId = getRandomLong(),
            dateFrom = getRandomDate().toADate(),
            dateUntil = getRandomDate().toADate()
        )
        dao.addReminderToRecord(entity.copy(scheduleId = entity.scheduleId + 1))
        runBlocking {
            assertThat(dao.hasPlannedReminderToRecord(entity.scheduleId)).isFalse
        }
    }
}

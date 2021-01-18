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
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.db.BaseDaoNewTest
import ru.olegivo.afs.common.toADate
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.shared.schedules.db.models.ReservedScheduleEntity
import java.util.Date

class ReserveDaoNewTest : BaseDaoNewTest<ReserveDaoNew>(
    { afsDatabaseNew, testScheduler -> ReserveDaoNew(afsDatabaseNew, testScheduler) }
) {
    @Test
    fun getReservedScheduleIds_RETURNS_only_relevant_ids() {
        val moment = Date()
        val from = moment.add(days = -1)
        val until = moment.add(days = 1)

        val entity1 = ReservedScheduleEntity(
            id = getRandomLong(),
            datetime = from.toADate()
        )
        val entity2 = ReservedScheduleEntity(
            id = getRandomLong(),
            datetime = until.add(seconds = -1).toADate()
        )
        val objects = listOf(
            entity1,
            entity2,
            ReservedScheduleEntity(
                id = getRandomLong(),
                datetime = from.add(seconds = -1).toADate()
            ),
            ReservedScheduleEntity(
                id = getRandomLong(),
                datetime = until.toADate()
            )
        )
        dao.upsertCompletable(objects)
            .andThen(dao.getReservedScheduleIds(from.toADate(), until.toADate()))
            .assertResult {
                assertThat(it).containsExactlyInAnyOrder(entity1.id, entity2.id)
            }
    }

    @Test
    fun isScheduleReserved_RETURNS_true() {
        val entity =
            ReservedScheduleEntity(
                id = getRandomLong(),
                datetime = getRandomDate().toADate()
            )
        dao.upsertCompletable(listOf(entity))
            .andThen(dao.isScheduleReserved(entity.id))
            .assertResult {
                assertThat(it).isTrue()
            }
    }

    @Test
    fun isScheduleReserved_RETURNS_false() {
        val entity =
            ReservedScheduleEntity(
                id = getRandomLong(),
                datetime = getRandomDate().toADate()
            )
        dao.upsertCompletable(listOf(entity))
            .andThen(dao.isScheduleReserved(entity.id + 1))
            .assertResult {
                assertThat(it).isFalse()
            }
    }
}

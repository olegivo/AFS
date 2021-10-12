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

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.common.add
import ru.olegivo.afs.common.db.BaseDaoNewTest
import ru.olegivo.afs.common.toInstantX
import ru.olegivo.afs.helpers.getRandomDate
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.shared.reserve.db.models.ReservedSchedules
import ru.olegivo.afs.shared.schedules.db.ReserveDaoImpl
import java.util.Date

class ReserveDaoImplTest : BaseDaoNewTest<ReserveDaoImpl>(
    { afsDatabaseNew, _ -> ReserveDaoImpl(afsDatabaseNew) }
) {
    @Test
    fun getReservedScheduleIds_RETURNS_only_relevant_ids() {
        val moment = Date()
        val from = moment.add(days = -1)
        val until = moment.add(days = 1)

        val entity1 = ReservedSchedules(
            id = getRandomLong(),
            datetime = from.toInstantX()
        )
        val entity2 = ReservedSchedules(
            id = getRandomLong(),
            datetime = until.add(seconds = -1).toInstantX()
        )
        val objects = listOf(
            entity1,
            entity2,
            ReservedSchedules(
                id = getRandomLong(),
                datetime = from.add(seconds = -1).toInstantX()
            ),
            ReservedSchedules(
                id = getRandomLong(),
                datetime = until.toInstantX()
            )
        )
        dao.upsert(objects)
        runBlocking {
            assertThat(dao.getReservedScheduleIds(from.toInstantX(), until.toInstantX()))
                .containsExactlyInAnyOrder(entity1.id, entity2.id)
        }
    }

    @Test
    fun isScheduleReserved_RETURNS_true() {
        val entity =
            ReservedSchedules(
                id = getRandomLong(),
                datetime = getRandomDate().toInstantX()
            )
        dao.upsert(listOf(entity))
        runBlocking {
            assertThat(dao.isScheduleReserved(entity.id)).isTrue
        }
    }

    @Test
    fun isScheduleReserved_RETURNS_false() {
        val entity =
            ReservedSchedules(
                id = getRandomLong(),
                datetime = getRandomDate().toInstantX()
            )
        dao.upsert(listOf(entity))
        runBlocking {
            assertThat(dao.isScheduleReserved(entity.id + 1)).isFalse
        }
    }
}

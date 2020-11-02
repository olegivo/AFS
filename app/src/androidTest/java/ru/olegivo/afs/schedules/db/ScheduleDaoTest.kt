/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
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
import ru.olegivo.afs.common.db.AfsDaoTest
import ru.olegivo.afs.helpers.checkSingleValue
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.repeat
import ru.olegivo.afs.schedules.data.models.createDataSchedule
import ru.olegivo.afs.schedules.db.models.toDb
import ru.olegivo.afs.suite.DbTest

@DbTest
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

        val schedules = {
            createDataSchedule()
                .copy(clubId = clubId)
                .toDb()
        }.repeat(10)
        val dates = schedules.map { it.datetime }.sorted()
        val from = dates.drop(1).first()
        val until = dates.last()

        val expected = schedules.filter { it.datetime >= from && it.datetime < until }

        dao.putSchedules(schedules)
            .subscribeOn(testScheduler)
            .andThenDeferMaybe {
                dao.getSchedules(clubId, from, until)
            }
            .subscribeOn(testScheduler)
            .test().andTriggerActions()
            .assertNoErrors()
            .checkSingleValue { result ->
                assertThat(result).containsExactlyInAnyOrderElementsOf(expected)
            }
    }
}

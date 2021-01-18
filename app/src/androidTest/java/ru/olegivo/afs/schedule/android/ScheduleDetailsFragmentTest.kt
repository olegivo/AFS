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

package ru.olegivo.afs.schedule.android

import org.junit.Test
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.common.android.BaseIntegratedIsolatedUITest
import ru.olegivo.afs.common.date
import ru.olegivo.afs.common.toADate
import ru.olegivo.afs.favorites.db.models.toDb
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import ru.olegivo.afs.helpers.getRandomInt
import ru.olegivo.afs.helpers.getRandomLong
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.schedules.db.models.ScheduleEntity
import ru.olegivo.afs.schedules.presentation.models.SportsActivityDisplay
import ru.olegivo.afs.suite.IntegratedIsolatedUITest
import java.util.Calendar

@IntegratedIsolatedUITest
class ScheduleDetailsFragmentTest :
    BaseIntegratedIsolatedUITest<ScheduleDetailsFragmentFixture, ScheduleDetailsFragmentScreen>() {

    override fun createFixture(externalDependencies: ExternalDependencies) =
        ScheduleDetailsFragmentFixture(externalDependencies)

    @Test
    fun showScheduleToReserve_DISPLAYS_recording_WHEN_preEntry() {
        val clubId = getRandomInt()
        val filter = FavoriteFilter(
            clubId = clubId,
            groupId = getRandomInt(),
            group = "Игровые виды спорта 4",
            activityId = getRandomInt(),
            activity = "Волейбол клиенты 4",
            dayOfWeek = Calendar.SATURDAY,
            minutesOfDay = 60 * 20
        )
        val favoritesItem = FavoritesItem(
            filter = filter,
            duty = "сб, 20:00"
        )

        val filterEntity = favoritesItem.filter.toDb().copy(id = getRandomInt())
        val scheduleEntity = ScheduleEntity(
            id = getRandomLong(),
            clubId = clubId,
            groupId = filter.groupId,
            group = filter.group,
            activityId = filter.activityId,
            activity = filter.activity,
            datetime = date(2020, 11, 19, 20, 0).toADate(),
            length = getRandomInt(),
            preEntry = true,
            totalSlots = 1,
            recordFrom = null,
            recordTo = null
        )
        fixture.prepareFromFavorites(listOf(filterEntity), favoritesItem, scheduleEntity)

        val sportsActivity = SportsActivityDisplay(
            hasAvailableSlots = true,
            preEntry = scheduleEntity.preEntry,
            datetime = getRandomString(prefix = "datetime"),
            group = scheduleEntity.group,
            activity = scheduleEntity.activity,
            recordingPeriod = null,
            slotsCount = null
        )

        ScheduleDetailsFragmentScreen {
            shouldDisplayRecording(true, sportsActivity)
        }
    }
}

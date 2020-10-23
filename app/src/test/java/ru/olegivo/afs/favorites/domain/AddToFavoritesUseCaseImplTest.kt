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

package ru.olegivo.afs.favorites.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.getMinutesOfDay
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.schedules.domain.models.createSchedule

class AddToFavoritesUseCaseImplTest : BaseTestOf<AddToFavoritesUseCase>() {
    override fun createInstance() = AddToFavoritesUseCaseImpl(favoritesRepository)

    //<editor-fold desc="mocks">
    override fun getAllMocks() = arrayOf<Any>(
        favoritesRepository
    )

    private val favoritesRepository: FavoritesRepository = mock()

    //</editor-fold>

    @Test
    fun `invoke ADDS favorites filter`() {
        val schedule = createSchedule()
        val expectedFilter = FavoriteFilter(
            groupId = schedule.groupId,
            group = schedule.group,
            activityId = schedule.activityId,
            activity = schedule.activity,
            dayOfWeek = schedule.getDayOfWeek(),
            minutesOfDay = schedule.datetime.getMinutesOfDay()
        )

        given(favoritesRepository.addFilter(expectedFilter)).willReturn(Completable.complete())

        instance(schedule)
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(favoritesRepository).addFilter(expectedFilter)
    }
}

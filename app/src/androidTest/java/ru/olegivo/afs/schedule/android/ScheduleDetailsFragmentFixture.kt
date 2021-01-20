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

import ru.olegivo.afs.BaseFixture
import ru.olegivo.afs.ExternalDependencies
import ru.olegivo.afs.common.android.ChainRuleHolder
import ru.olegivo.afs.favorites.android.FavoritesFragmentFixture
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import ru.olegivo.afs.shared.favorites.db.models.FavoriteFilters
import ru.olegivo.afs.shared.schedules.db.models.Schedules

class ScheduleDetailsFragmentFixture(
    externalDependencies: ExternalDependencies,
    private val favoritesFragmentFixture: FavoritesFragmentFixture =
        FavoritesFragmentFixture(externalDependencies)
) : BaseFixture<ScheduleDetailsFragmentScreen>(externalDependencies, ScheduleDetailsFragmentScreen),
    ChainRuleHolder by favoritesFragmentFixture {

    fun prepareFromFavorites(
        filters: List<FavoriteFilters>,
        favoritesItemToClick: FavoritesItem,
        scheduleEntity: Schedules
    ) {
        favoritesFragmentFixture.prepare(filters)
        favoritesFragmentFixture.prepareItemClick(favoritesItemToClick.filter, scheduleEntity)
        favoritesFragmentFixture.screen {
            clickOnItem(favoritesItemToClick)
        }
        triggerActions()
        favoritesFragmentFixture.checkItemClick(scheduleEntity)
    }
}

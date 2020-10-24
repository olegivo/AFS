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

package ru.olegivo.afs.common.android

import ru.olegivo.afs.common.presentation.BrowserDestination
import ru.olegivo.afs.common.presentation.Destination
import ru.olegivo.afs.common.presentation.Navigator
import ru.olegivo.afs.favorites.android.FavoritesScreen
import ru.olegivo.afs.favorites.presentation.models.FavoritesDestination
import ru.olegivo.afs.schedule.android.ReserveScreen
import ru.olegivo.afs.schedule.presentation.models.ReserveDestination
import ru.olegivo.afs.schedules.android.ScheduleScreen
import ru.olegivo.afs.schedules.presentation.models.ScheduleDestination
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen
import javax.inject.Inject

class NavigatorImpl @Inject constructor(val router: Router) : Navigator {
    override fun navigateTo(destination: Destination) {
        val screen: Screen = when (destination) {
            is ReserveDestination -> ReserveScreen(destination)
            is ScheduleDestination -> ScheduleScreen(destination)
            is BrowserDestination -> BrowserScreen(destination)
            is FavoritesDestination -> FavoritesScreen(destination)
            else -> TODO("Not implemented ($destination)")
        }
        router.navigateTo(screen)
    }

    override fun navigateBack() {
        router.exit()
    }
}

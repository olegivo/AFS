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
 */package ru.olegivo.afs.favorites.analytics

import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.analytics.models.AnalyticEvent
import ru.olegivo.afs.analytics.models.EventName

object FavoritesAnalytics {

    object ShowRecordReminder : AnalyticEvent.Custom(EventName("favorite_show_record_reminder"))

    object PlanFavoriteRecordReminder : AnalyticEvent.Custom(EventName("favorite_plan_record_reminder"))

    object RestoreActiveRecordReminder : AnalyticEvent.Custom(EventName("favorite_restore_active_record_reminder"))

    object Screens {
        object Favorites : ScreenNameProvider {
            override val screenName: String = "favorites"
        }
    }
}

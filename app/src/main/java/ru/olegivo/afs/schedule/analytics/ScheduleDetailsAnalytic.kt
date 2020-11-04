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
 */package ru.olegivo.afs.schedule.analytics

import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.analytics.models.AnalyticEvent
import ru.olegivo.afs.analytics.models.EventName

object ScheduleDetailsAnalytic {
    object Screens {
        object ScheduleDetails : ScreenNameProvider {
            override val screenName: String = "schedule_details"

            object OnReserveClicked : AnalyticEvent.Custom(EventName("schedule_details_reserve_clicked"))

            object OnRemoveFromFavoritesClicked : AnalyticEvent.Custom(EventName("schedule_details_remove_favorite_clicked"))

            object OnAddToFavoritesClicked : AnalyticEvent.Custom(EventName("schedule_details_add_favorite_clicked"))

            object OnViewAgreementClicked : AnalyticEvent.Custom(EventName("schedule_details_view_agreement_clicked"))

            object SaveAgreementAccepted : AnalyticEvent.Custom(EventName("schedule_details_agreement_accepted"))
        }
    }
}

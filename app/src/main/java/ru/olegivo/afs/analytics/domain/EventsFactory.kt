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

package ru.olegivo.afs.analytics.domain

import com.google.firebase.analytics.FirebaseAnalytics
import ru.olegivo.afs.analytics.models.AnalyticEvent
import ru.olegivo.afs.analytics.models.Event
import ru.olegivo.afs.analytics.models.EventName
import javax.inject.Inject

interface EventsFactory {
    fun createEvent(
        analyticEvent: AnalyticEvent
    ): Event
}

class FirebaseEventsFactory @Inject constructor() : EventsFactory {

    override fun createEvent(analyticEvent: AnalyticEvent): Event {
        val extra = analyticEvent.extra.toMutableMap()

        val eventName = when (analyticEvent) {
            is AnalyticEvent.ScreenView -> {
                extra += mutableMapOf(Parameters.screenName to analyticEvent.screenName)
                    .apply { analyticEvent.screenClass?.also { put(Parameters.screenClass, it) } }
                Names.screenView
            }
            is AnalyticEvent.Custom -> {
                analyticEvent.name
            }
        }

        return Event(
            name = eventName.value,
            parameters = extra
        )
    }

    object Parameters {
        const val screenName = FirebaseAnalytics.Param.SCREEN_NAME
        const val screenClass = FirebaseAnalytics.Param.SCREEN_CLASS
    }

    object Names {
        val screenView = EventName(FirebaseAnalytics.Event.SCREEN_VIEW)
    }
}

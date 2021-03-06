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

package ru.olegivo.afs.analytics.data

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.toCompletable
import ru.olegivo.afs.analytics.domain.AnalyticsRepository
import ru.olegivo.afs.analytics.models.Event
import javax.inject.Inject
import javax.inject.Named

class FirebaseAnalyticsRepository @Inject constructor(
    private val firebaseAnalyticsNetworkSource: FirebaseAnalyticsNetworkSource,
    @Named("io") private val ioScheduler: Scheduler
) : AnalyticsRepository {

    override fun logEvent(event: Event): Completable =
        { firebaseAnalyticsNetworkSource.logEvent(event.name, event.parameters) }
            .toCompletable()
            .subscribeOn(ioScheduler)
}

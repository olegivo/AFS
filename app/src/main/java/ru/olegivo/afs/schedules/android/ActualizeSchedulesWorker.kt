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

package ru.olegivo.afs.schedules.android

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class ActualizeSchedulesWorker constructor(
    appContext: Context,
    params: WorkerParameters,
    private val getCurrentClub: GetCurrentClubUseCase,
    private val actualizeSchedule: ActualizeScheduleUseCase
) : RxWorker(appContext, params) {

    override fun createWork(): Single<Result> =
        getCurrentClub()
            .doOnSubscribe { Timber.d("Actualizing schedules...") }
            .flatMapCompletable { clubId -> actualizeSchedule(clubId) }
            .andThen(Single.just(Result.success()))

    companion object {
        const val TAG = "ActualizeSchedulesWorker"
    }

    class Factory @Inject constructor(
        private val getCurrentClub: Provider<GetCurrentClubUseCase>,
        private val actualizeSchedule: Provider<ActualizeScheduleUseCase>
    ) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters) =
            ActualizeSchedulesWorker(
                appContext,
                params,
                getCurrentClub.get(),
                actualizeSchedule.get(),
            )
    }
}

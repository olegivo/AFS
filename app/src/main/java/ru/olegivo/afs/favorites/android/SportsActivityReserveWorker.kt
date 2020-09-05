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

package ru.olegivo.afs.favorites.android

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.domain.ScheduleReminderNotifier
import ru.olegivo.afs.schedule.domain.GetSportsActivityUseCase
import ru.olegivo.afs.schedule.domain.ReserveUseCase
import ru.olegivo.afs.schedule.domain.models.ReserveResult
import ru.olegivo.afs.schedules.domain.models.Schedule
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class SportsActivityReserveWorker constructor(
    appContext: Context,
    private val params: WorkerParameters,
    private val getSportsActivity: GetSportsActivityUseCase,
    private val reserveUseCase: ReserveUseCase,
    private val scheduleReminderNotifier: ScheduleReminderNotifier
) : RxWorker(appContext, params) {

    override fun createWork(): Single<Result> =
        params.inputData.toSingle()
            .map { it.getSportsActivityReserveParameters() }
            .flatMapCompletable { process(it) }
            .andThen(Single.just(Result.success()))
            .doOnSubscribe { Timber.d("Begin reserve") }
            .doOnSuccess { Timber.d("Reserve successful") }
            .doOnError { Timber.e(it, "Reserve failed") }

    private fun process(reserveParameters: SportsActivityReserveParameters): Completable =
        with(reserveParameters) {
            getSportsActivity(clubId, scheduleId)
                .flatMapCompletable { sportsActivity ->
                    reserveUseCase.reserve(sportsActivity, fio, phone, true)
                        .flatMapCompletable { reserveResult ->
                            when (reserveResult) {
                                ReserveResult.Success -> {
                                    showSuccessReserved(sportsActivity.schedule)
                                }
                                ReserveResult.TheTimeHasGone -> {
                                    showTheTimeHasGone(sportsActivity.schedule)
                                }
                                ReserveResult.NoSlots.APriori -> {
                                    showHasNoSlotsAPriori(sportsActivity.schedule)
                                }
                                ReserveResult.NoSlots.APosteriori -> {
                                    showHasNoSlotsAPosteriori(sportsActivity.schedule)
                                }
                                ReserveResult.NameAndPhoneShouldBeStated -> {
                                    showNameAndPhoneShouldBeStated()
                                }
                                ReserveResult.AlreadyReserved -> {
                                    showAlreadyReserved(sportsActivity.schedule)
                                }
                                ReserveResult.HaveToAcceptAgreement -> {
                                    showHaveToAcceptAgreement()
                                }
                            }
                        }
                }
        }

    private fun showHaveToAcceptAgreement(): Completable = TODO("Impossible")

    private fun showNameAndPhoneShouldBeStated(): Completable = TODO("Impossible")

    private fun showAlreadyReserved(schedule: Schedule): Completable =
        scheduleReminderNotifier.showAlreadyReserved(schedule)

    private fun showHasNoSlotsAPosteriori(schedule: Schedule): Completable =
        scheduleReminderNotifier.showHasNoSlotsAPosteriori(schedule)

    private fun showHasNoSlotsAPriori(schedule: Schedule): Completable =
        scheduleReminderNotifier.showHasNoSlotsAPriori(schedule)

    private fun showTheTimeHasGone(schedule: Schedule): Completable =
        scheduleReminderNotifier.showTheTimeHasGone(schedule)

    private fun showSuccessReserved(schedule: Schedule): Completable =
        scheduleReminderNotifier.showSuccessReserved(schedule)

    companion object {
        const val TAG = "SportsActivityReserveWorker"

        fun createInputData(recordReminderParameters: SportsActivityReserveParameters) =
            recordReminderParameters.toWorkerParameters()
    }

    class Factory @Inject constructor(
        private val getSportsActivity: Provider<GetSportsActivityUseCase>,
        private val reserveUseCase: Provider<ReserveUseCase>,
        private val scheduleReminderNotifier: Provider<ScheduleReminderNotifier>
    ) : ChildWorkerFactory<SportsActivityReserveWorker>(SportsActivityReserveWorker::class) {
        override fun create(appContext: Context, params: WorkerParameters) =
            SportsActivityReserveWorker(
                appContext,
                params,
                getSportsActivity.get(),
                reserveUseCase.get(),
                scheduleReminderNotifier.get()
            )
    }
}

package ru.olegivo.afs.favorites.android

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
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

class SportsActivityReserveWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val getSportsActivity: GetSportsActivityUseCase,
    private val reserveUseCase: ReserveUseCase,
    private val scheduleReminderNotifier: ScheduleReminderNotifier
) : RxWorker(appContext, params) {

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

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
}
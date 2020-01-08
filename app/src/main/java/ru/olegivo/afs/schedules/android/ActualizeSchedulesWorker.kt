package ru.olegivo.afs.schedules.android

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Single
import ru.olegivo.afs.clubs.domain.GetCurrentClubUseCase
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.schedules.domain.ActualizeScheduleUseCase
import timber.log.Timber

class ActualizeSchedulesWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val getCurrentClub: GetCurrentClubUseCase,
    private val actualizeSchedule: ActualizeScheduleUseCase
) : RxWorker(appContext, params) {

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    override fun createWork(): Single<Result> =
        getCurrentClub()
            .doOnSubscribe{ Timber.d("Actualizing schedules...")}
            .flatMapCompletable { clubId -> actualizeSchedule(clubId) }
            .andThen(Single.just(Result.success()))

    companion object {
        const val TAG = "ActualizeSchedulesWorker"
    }
}
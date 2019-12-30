package ru.olegivo.afs.favorites.android

import android.content.Context
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.domain.RestoreAllActiveRecordRemindersUseCase
import ru.olegivo.afs.favorites.domain.ShowRecordReminderUseCase
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters

class FavoriteRecordReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val restoreAllActiveRecordReminders: RestoreAllActiveRecordRemindersUseCase,
    private val showRecordReminder: ShowRecordReminderUseCase
) : RxWorker(appContext, params) {

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    override fun createWork(): Single<Result> =
        params.inputData.toSingle()
            .flatMapCompletable { process(it) }
            .andThen(Single.just(Result.success()))

    private fun process(data: Data): Completable =
        if (data.isAfterRebootMode()) {
            restoreAllActiveRecordReminders()
        } else {
            val scheduleId = data.getScheduleId()
            showRecordReminder(scheduleId)
        }


    companion object {
        const val TAG = "FavoriteRecordReminderWorker"

        fun createInputData(recordReminderParameters: FavoriteRecordReminderParameters) =
            workDataOf("SCHEDULE_ID" to recordReminderParameters.scheduleId)

        fun createInputDataWithAfterRebootMode() = workDataOf("AFTER_REBOOT_MODE" to true)

        fun Data.getScheduleId() = getLong("SCHEDULE_ID", 0)
        fun Data.isAfterRebootMode() = getBoolean("AFTER_REBOOT_MODE", false)
    }
}
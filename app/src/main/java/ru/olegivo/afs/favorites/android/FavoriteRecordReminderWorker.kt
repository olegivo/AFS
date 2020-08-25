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
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.common.android.worker.ChildWorkerFactory
import ru.olegivo.afs.common.domain.ErrorReporter
import ru.olegivo.afs.extensions.toSingle
import ru.olegivo.afs.favorites.domain.RestoreAllActiveRecordRemindersUseCase
import ru.olegivo.afs.favorites.domain.ShowRecordReminderUseCase
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters
import javax.inject.Inject
import javax.inject.Provider

class FavoriteRecordReminderWorker constructor(
    appContext: Context,
    private val params: WorkerParameters,
    private val restoreAllActiveRecordReminders: RestoreAllActiveRecordRemindersUseCase,
    private val showRecordReminder: ShowRecordReminderUseCase,
    private val errorReporter: ErrorReporter
) : RxWorker(appContext, params) {

    override fun createWork(): Single<Result> =
        params.inputData.toSingle()
            .flatMapCompletable { process(it) }
            .andThen(Single.just(Result.success()))

    private fun process(data: Data): Completable =
        if (data.isAfterRebootMode()) {
            restoreAllActiveRecordReminders()
                .doOnError {
                    errorReporter.reportError(it, "Ошибка при попытке восстановить")
                }
        } else {
            val scheduleId = data.getScheduleId()
            showRecordReminder(scheduleId)
                .doOnError {
                    errorReporter.reportError(
                        it,
                        "Ошибка при попытке показать уведомление о необходимости записи на занятие"
                    )
                }
        }

    companion object {
        const val TAG = "FavoriteRecordReminderWorker"

        fun createInputData(recordReminderParameters: FavoriteRecordReminderParameters) =
            workDataOf("SCHEDULE_ID" to recordReminderParameters.scheduleId)

        fun createInputDataWithAfterRebootMode() = workDataOf("AFTER_REBOOT_MODE" to true)

        fun Data.getScheduleId() = getLong("SCHEDULE_ID", 0)
        fun Data.isAfterRebootMode() = getBoolean("AFTER_REBOOT_MODE", false)
    }

    class Factory @Inject constructor(
        private val restoreAllActiveRecordReminders: Provider<RestoreAllActiveRecordRemindersUseCase>,
        private val showRecordReminder: Provider<ShowRecordReminderUseCase>,
        private val errorReporter: Provider<ErrorReporter>
    ) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters) =
            FavoriteRecordReminderWorker(
                appContext,
                params,
                restoreAllActiveRecordReminders.get(),
                showRecordReminder.get(),
                errorReporter.get()
            )
    }
}

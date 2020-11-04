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

package ru.olegivo.afs.schedules.presentation

import ru.olegivo.afs.common.presentation.PresentationContract
import ru.olegivo.afs.schedules.presentation.models.Day

interface WeekScheduleContract {
    interface Presenter : PresentationContract.Presenter<View> {
        fun getClubId(): Int
        fun getDay(position: Int): Day
        fun onDayChanged(position: Int)
    }

    interface View :
        PresentationContract.View,
        PresentationContract.ErrorDisplay,
        PresentationContract.ViewWithProgress {
        fun onReady(position: Int)
    }
}

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

package ru.olegivo.afs.schedule.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.olegivo.afs.schedule.domain.models.Reserve
import ru.olegivo.afs.schedule.domain.models.ReserveContacts

interface ReserveRepository {
    fun getAvailableSlots(clubId: Int, scheduleId: Long): Single<Int>
    fun reserve(reserve: Reserve): Completable
    fun saveReserveContacts(reserveContacts: ReserveContacts): Completable
    fun getReserveContacts(): Maybe<ReserveContacts>
    fun isAgreementAccepted(): Single<Boolean>
    fun setAgreementAccepted(): Completable
    fun isStubReserve(): Single<Boolean>
    fun setStubReserve(isStubReserve: Boolean): Completable
}

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

package ru.olegivo.afs.clubs.network

import io.reactivex.Scheduler
import io.reactivex.Single
import ru.olegivo.afs.clubs.data.ClubsNetworkSource
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.network.Api
import javax.inject.Inject
import javax.inject.Named

class ClubsNetworkSourceImpl @Inject constructor(
    private val api: Api,
    @Named("io") private val ioScheduler: Scheduler
) : ClubsNetworkSource {
    override fun getClubs(): Single<List<Club>> {
        return api.getClubs().subscribeOn(ioScheduler)
    }
}

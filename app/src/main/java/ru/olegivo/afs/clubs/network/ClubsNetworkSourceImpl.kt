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

import ru.olegivo.afs.clubs.data.ClubsNetworkSource
import ru.olegivo.afs.common.network.Api
import ru.olegivo.afs.schedules.network.models.DomainClub
import ru.olegivo.afs.schedules.network.models.toDomain
import javax.inject.Inject

class ClubsNetworkSourceImpl @Inject constructor(private val api: Api) : ClubsNetworkSource {

    override suspend fun getClubs(): List<DomainClub> =
        api.getClubs().map { it.toDomain() }
}

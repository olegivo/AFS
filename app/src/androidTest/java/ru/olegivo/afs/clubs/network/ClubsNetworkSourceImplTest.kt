package ru.olegivo.afs.clubs.network

import org.junit.Test
import ru.olegivo.afs.common.network.AuthorizedApiTest

class ClubsNetworkSourceImplTest : AuthorizedApiTest() {
    @Test
    fun getClubs() {
        val clubs = ClubsNetworkSourceImpl(api)
            .getClubs()
            .test()
            .assertNoErrors()
            .values()
            .single()
    }
}
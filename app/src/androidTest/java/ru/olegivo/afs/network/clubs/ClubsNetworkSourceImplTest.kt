package ru.olegivo.afs.network.clubs

import org.junit.Test
import ru.olegivo.afs.network.AuthorizedApiTest

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
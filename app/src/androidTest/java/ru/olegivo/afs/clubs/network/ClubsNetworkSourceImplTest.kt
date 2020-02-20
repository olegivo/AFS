package ru.olegivo.afs.clubs.network

import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.common.network.AuthorizedApiTest

class ClubsNetworkSourceImplTest : AuthorizedApiTest() {
    @Test
    fun getClubs() {
        val scheduler = TestScheduler()

        val testObserver = ClubsNetworkSourceImpl(api, scheduler)
            .getClubs()
            .test()

        scheduler.triggerActions()

        val clubs = testObserver
            .assertNoErrors()
            .values()
            .single()
        assertThat(clubs).isNotEmpty
    }
}

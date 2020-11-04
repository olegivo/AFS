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

package ru.olegivo.afs.analytics.domain

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.analytics.data.FirebaseAnalyticsNetworkSource
import ru.olegivo.afs.analytics.data.FirebaseAnalyticsRepository
import ru.olegivo.afs.analytics.models.AnalyticEvent
import ru.olegivo.afs.analytics.models.EventName
import ru.olegivo.afs.helpers.getRandomString
import ru.olegivo.afs.repeat

class AnalyticsProviderImplTest : BaseTestOf<AnalyticsProvider>() {

    override fun createInstance() = AnalyticsProviderImpl(
        FirebaseAnalyticsRepository(
            firebaseAnalyticsNetworkSource = firebaseAnalyticsNetworkSource,
            ioScheduler = testScheduler
        ),
        eventsFactory = FirebaseEventsFactory()
    )

    //<editor-fold desc="Mocks">
    override fun getAllMocks(): Array<Any> = arrayOf(
        firebaseAnalyticsNetworkSource
    )

    private val firebaseAnalyticsNetworkSource: FirebaseAnalyticsNetworkSource = mock()
    //</editor-fold>

    @Test
    fun `logEvent SENDS event WHEN Custom event`() {
        val testData = TestData(EventName(getRandomString(prefix = "name")))

        val event = object : AnalyticEvent.Custom(
            name = testData.eventName,
            extra = testData.extra
        ) {}

        instance.logEvent(event)
            .test().andTriggerActions()
            .assertComplete()
            .assertNoErrors()

        verifySent(testData, emptyMap())
    }

    @Test
    fun `logEvent SENDS event WHEN ScreenView event with screenClass`() {
        val testData = TestData(FirebaseEventsFactory.Names.screenView)

        val event = AnalyticEvent.ScreenView(
            screenName = getRandomString(prefix = "name"),
            screenClass = getRandomString(prefix = "class"),
            parameters = testData.extra
        )

        instance.logEvent(event)
            .test().andTriggerActions()
            .assertComplete()
            .assertNoErrors()

        verifySent(
            testData,
            mapOf(
                FirebaseEventsFactory.Parameters.screenName to event.screenName,
                FirebaseEventsFactory.Parameters.screenClass to event.screenClass!!
            )
        )
    }

    @Test
    fun `logEvent SENDS event WHEN ScreenView event without screenClass`() {
        val testData = TestData(FirebaseEventsFactory.Names.screenView)

        val event = AnalyticEvent.ScreenView(
            screenName = getRandomString(prefix = "name"),
            parameters = testData.extra
        )

        instance.logEvent(event)
            .test().andTriggerActions()
            .assertComplete()
            .assertNoErrors()

        verifySent(
            testData,
            mapOf(
                FirebaseEventsFactory.Parameters.screenName to event.screenName
            )
        )
    }

    private fun verifySent(testData: TestData, specificExpectedExtra: Map<String, String>) {
        val captor = argumentCaptor<Map<String, String>>()
        verify(firebaseAnalyticsNetworkSource).logEvent(
            eq(testData.eventName.value),
            captor.capture()
        )
        val actualExtra = captor.lastValue

        val expected = mapOf<String, String>(
//            FirebaseEventsFactory.Param.TIMESTAMP to testData.formattedTimestamp,
//            FirebaseEventsFactory.Param.APP_TYPE to FirebaseEventsFactory.APP_TYPE_ANDROID,
//            FirebaseEventsFactory.Param.CPU_ARCH to testData.cpuArchitecture
        ) + specificExpectedExtra + testData.extra

        assertThat(actualExtra.keys).hasSameSizeAs(expected.keys)
        for ((key, expectedValue) in expected) {
            assertThat(actualExtra).containsKey(key)
            assertThat(actualExtra[key])
                .describedAs("the key $key contains unexpected value")
                .isEqualTo(expectedValue)
        }
    }

    private data class TestData(val eventName: EventName) {
        val extra: Map<String, String> =
            { getRandomString() }
                .repeat(5)
                .associate { "customKey$it" to getRandomString() }
    }
}

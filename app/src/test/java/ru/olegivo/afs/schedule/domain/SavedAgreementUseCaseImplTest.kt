package ru.olegivo.afs.schedule.domain

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.helpers.getRandomBoolean

class SavedAgreementUseCaseImplTest : BaseTestOf<SavedAgreementUseCase>() {

    override fun createInstance() = SavedAgreementUseCaseImpl(reserveRepository)

    //<editor-fold desc="mocks">
    private val reserveRepository: ReserveRepository = mock()

    override fun getAllMocks() = arrayOf<Any>(reserveRepository)
    //</editor-fold>

    @Test
    fun `isAgreementAccepted`() {
        val expected = getRandomBoolean()
        given(reserveRepository.isAgreementAccepted()).willReturn(Single.just(expected))

        val isAgreementAccepted = instance.isAgreementAccepted()
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()
            .values().single()

        assertThat(isAgreementAccepted).isEqualTo(expected)
        verify(reserveRepository).isAgreementAccepted()
    }

    @Test
    fun `setAgreementAccepted`() {
        given(reserveRepository.setAgreementAccepted()).willReturn(Completable.complete())

        instance.setAgreementAccepted()
            .test().andTriggerActions()
            .assertNoErrors()
            .assertComplete()

        verify(reserveRepository).setAgreementAccepted()
    }
}
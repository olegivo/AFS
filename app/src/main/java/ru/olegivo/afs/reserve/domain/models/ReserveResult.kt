package ru.olegivo.afs.reserve.domain.models

sealed class ReserveResult {
    object NoSlots {
        object APriori : ReserveResult()
        object APosteriori : ReserveResult()
    }

    object TheTimeHasGone : ReserveResult()
    object NameAndPhoneShouldBeStated : ReserveResult()
    object AlreadyReserved : ReserveResult()
    object Success : ReserveResult()
}

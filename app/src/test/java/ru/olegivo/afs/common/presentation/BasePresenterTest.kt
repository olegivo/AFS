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

package ru.olegivo.afs.common.presentation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Scheduler
import org.mockito.Mockito
import ru.olegivo.afs.BaseTestOf
import ru.olegivo.afs.common.domain.ErrorReporter
import kotlin.reflect.KClass

abstract class BasePresenterTest<TPresenter, TView>(kClass: KClass<TView>) :
    BaseTestOf<TPresenter>()
    where TPresenter : PresentationContract.Presenter<TView>,
          TView : PresentationContract.View {

    override fun createInstance(): TPresenter =
        createPresenter(
            mainScheduler = testScheduler,
            errorReporter = errorReporter
        )

    protected abstract fun createPresenter(
        mainScheduler: Scheduler,
        errorReporter: ErrorReporter
    ): TPresenter

    //<editor-fold desc="Mocks">
    protected val errorReporter: ErrorReporter = mock()
    protected val view: TView =
        Mockito.mock(kClass.java) // mockito-kotlin cannot use TView as reified type

    override fun getAllMocks() = getPresenterMocks().plus(
        elements = arrayOf(
            errorReporter,
            view
        )
    )

    protected abstract fun getPresenterMocks(): Array<Any>
    //</editor-fold>

    override fun setUp() {
        instance.unbindView()
        super.setUp()
    }

    protected fun verifyError(exception: RuntimeException, message: String) {
        (view as? PresentationContract.ErrorDisplay)?.let {
            verify(it).showErrorMessage(message)
        }
        verify(errorReporter).reportError(exception, message)
    }

    protected open fun <TContext : Any> bind(
        context: TContext,
        prepare: TContext.() -> Unit,
        verify: TContext.() -> Unit
    ) {
        context.prepare()

        instance.bindView(view)
            .andTriggerActions()

        verifyBindInteractions()
        context.verify()
    }

    protected fun bind() {
        bind(context = Unit, prepare = {}, verify = {})
    }

    protected open fun verifyBindInteractions() {
        (view as? PresentationContract.ViewWithProgress)?.let {
            verify(it).showProgress()
            verify(it).hideProgress()
        }
    }
}

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

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.olegivo.afs.common.domain.ErrorReporter

abstract class BasePresenter<TView : PresentationContract.View>
protected constructor(
    private val errorReporter: ErrorReporter
) : PresentationContract.Presenter<TView> {

    protected var view: TView? = null
    private var compositeDisposable: CompositeDisposable =
        CompositeDisposable()
    private var disposableSearch: Disposable? = null

    override fun bindView(view: TView) {
        this.view = view
    }

    override fun unbindView() {
        this.view = null
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    protected fun Disposable.addToComposite() {
        compositeDisposable.add(this)
    }

    protected fun onError(throwable: Throwable, message: String) {
        errorReporter.reportError(throwable, message)
        view?.also {
            when (it) {
                is PresentationContract.ErrorDisplay -> it.showErrorMessage(message)
            }
        }
    }
}

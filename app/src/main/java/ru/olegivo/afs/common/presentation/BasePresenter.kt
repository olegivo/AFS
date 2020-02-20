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

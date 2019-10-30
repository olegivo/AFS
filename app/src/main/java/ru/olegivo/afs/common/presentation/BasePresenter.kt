package ru.olegivo.afs.common.presentation

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<TView : PresentationContract.View>
protected constructor(

) : PresentationContract.Presenter<TView> {

    protected var view: TView? = null
    protected var compositeDisposable: CompositeDisposable =
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
        // TODO: log error centrally
        throwable.printStackTrace()
        view?.also {
            when (it) {
                is PresentationContract.ErrorDisplay -> it.showErrorMessage(message)
            }
        }
    }

}
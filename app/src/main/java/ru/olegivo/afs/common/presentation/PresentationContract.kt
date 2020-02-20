package ru.olegivo.afs.common.presentation

interface PresentationContract {
    interface View

    interface ViewWithProgress : View {
        fun showProgress()
        fun hideProgress()
    }

    interface CanDisplayErrors : View

    interface ErrorDisplay : CanDisplayErrors {
        fun showErrorMessage(message: String)
    }

    interface ErrorDialog : CanDisplayErrors {
        fun showErrorDialog(title: String, message: String)
    }

    interface Presenter<TView : View> {
        fun bindView(view: TView)
        fun unbindView()
        fun onDestroy()
    }
}

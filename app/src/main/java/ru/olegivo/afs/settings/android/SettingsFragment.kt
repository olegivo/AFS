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

package ru.olegivo.afs.settings.android

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.olegivo.afs.R
import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.clubs.android.ChooseClubDialog
import ru.olegivo.afs.clubs.domain.models.Club
import ru.olegivo.afs.common.android.doOnApplyWindowInsets
import ru.olegivo.afs.databinding.FragmentSettingsBinding
import ru.olegivo.afs.settings.analytics.SettingsAnalytics
import ru.olegivo.afs.settings.presentation.SettingsContract
import javax.inject.Inject

class SettingsFragment @Inject constructor(
    private val presenter: SettingsContract.Presenter
) : Fragment(R.layout.fragment_settings),
    ScreenNameProvider by SettingsAnalytics.Screens.Settings,
    SettingsContract.View {

    private val viewBinding by viewBinding(FragmentSettingsBinding::bind)

    private val onCheckedChangeListener: (CompoundButton, Boolean) -> Unit = { _, isChecked ->
        presenter.onStubReserveChecked(isChecked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.toolbarLayout.toolbar.title = "Settings"

        viewBinding.chooseClubButton.setOnClickListener {
            presenter.onChooseClubClicked()
        }
        viewBinding.setDefaultClubButton.setOnClickListener {
            presenter.onSetDefaultClubClicked()
        }
        viewBinding.dropDbButton.setOnClickListener {
            presenter.onDropDbBClicked()
        }
        disableIsStubReserveCheckBox()

        viewBinding.toolbarLayout.appbarLayout.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop
            )
            insets
        }
        viewBinding.root.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
    }

    override fun enableIsStubReserveCheckBox(isStubReserve: Boolean) {
        if (viewBinding.isStubReserveCheckBox.isChecked != isStubReserve) {
            viewBinding.isStubReserveCheckBox.isChecked = isStubReserve
        }
        viewBinding.isStubReserveCheckBox.isEnabled = true
        viewBinding.isStubReserveCheckBox.setOnCheckedChangeListener(onCheckedChangeListener)
    }

    override fun disableIsStubReserveCheckBox() {
        viewBinding.isStubReserveCheckBox.isEnabled = false
        viewBinding.isStubReserveCheckBox.setOnCheckedChangeListener(null)
    }

    override fun showChooseClubDialog(clubs: List<Club>, selectedClub: Club?) {
        ChooseClubDialog.chooseClub(clubs, selectedClub, requireContext()) { club ->
            presenter.onCurrentClubSelected(club)
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}

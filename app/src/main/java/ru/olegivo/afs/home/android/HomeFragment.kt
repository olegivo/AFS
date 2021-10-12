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

package ru.olegivo.afs.home.android

import android.os.Bundle
import android.view.View
import androidx.annotation.DimenRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.accompanist.appcompattheme.AppCompatTheme
import ru.olegivo.afs.R
import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.common.android.doOnApplyWindowInsets
import ru.olegivo.afs.compose.MyButton
import ru.olegivo.afs.databinding.FragmentHomeBinding
import ru.olegivo.afs.home.analytics.HomeAnalytics
import ru.olegivo.afs.home.presentation.HomeContract
import javax.inject.Inject

class HomeFragment @Inject constructor(
    private val presenter: HomeContract.Presenter
) : Fragment(R.layout.fragment_home),
    ScreenNameProvider by HomeAnalytics.Screens.Home,
    HomeContract.View {

    private val viewBinding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.root.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }

        viewBinding.container.setContent {
            AppCompatTheme {
                Column(
                    verticalArrangement = arrangement(R.dimen.medium),
                    modifier = Modifier.padding(R.dimen.medium)
                ) {
                    MyButton(
                        onClick = { presenter.onSettingsClicked() },
                        modifier = Modifier.fillMaxWidth(),
                        text = R.string.fragment_home_settings_button_text
                    )
                    MyButton(
                        onClick = { presenter.onFavoritesClicked() },
                        modifier = Modifier.fillMaxWidth(),
                        text = R.string.fragment_home_favorites_button_text
                    )
                    MyButton(
                        onClick = { presenter.onFavoritesClicked() },
                        modifier = Modifier.fillMaxWidth(),
                        text = R.string.fragment_home_schedules_button_text
                    )
                }
            }
        }
    }
}

@Composable
fun arrangement(@DimenRes dimenId: Int) =
    Arrangement.spacedBy(dimensionResource(id = dimenId))

@Composable
fun Modifier.padding(@DimenRes dimenId: Int) =
    padding(dimensionResource(id = dimenId))

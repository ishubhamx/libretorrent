/*
 * Copyright (C) 2025 TorrentHub Fork
 *
 * This file is part of TorrentHub (LibreTorrent fork).
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.libretorrent.ui.settings.pages;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import org.proninyaroslav.libretorrent.R;
import org.proninyaroslav.libretorrent.databinding.FragmentPreferenceBinding;
import org.proninyaroslav.libretorrent.ui.settings.CustomPreferenceFragment;

public class LegalSettingsFragment extends CustomPreferenceFragment {
    private AppCompatActivity activity;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.pref_legal);
        
        Preference disclaimerPref = findPreference(getString(R.string.pref_legal_disclaimer_title));
        if (disclaimerPref != null) {
            disclaimerPref.setOnPreferenceClickListener(preference -> {
                showLegalDisclaimerDialog();
                return true;
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var binding = FragmentPreferenceBinding.bind(view);
        binding.appBar.setTitle(R.string.pref_header_legal);

        if (activity == null) {
            activity = (AppCompatActivity) requireActivity();
        }
        binding.appBar.setNavigationOnClickListener((v) -> finish());

        super.onViewCreated(view, savedInstanceState);
    }

    private void showLegalDisclaimerDialog() {
        if (activity == null) return;
        
        new AlertDialog.Builder(activity)
            .setTitle(R.string.pref_legal_disclaimer_title)
            .setMessage(R.string.pref_legal_disclaimer_text)
            .setPositiveButton(R.string.ok, null)
            .show();
    }

    private void finish() {
        if (activity != null) {
            activity.getOnBackPressedDispatcher().onBackPressed();
        }
    }
}

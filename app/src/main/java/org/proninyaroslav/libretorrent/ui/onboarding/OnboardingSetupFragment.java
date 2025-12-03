/*
 * Copyright (C) 2025 P2P File Transfer Team
 *
 * This file is part of P2P File Transfer.
 *
 * P2P File Transfer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * P2P File Transfer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with P2P File Transfer.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.libretorrent.ui.onboarding;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.proninyaroslav.libretorrent.R;
import org.proninyaroslav.libretorrent.core.RepositoryHelper;
import org.proninyaroslav.libretorrent.core.settings.SettingsRepository;
import org.proninyaroslav.libretorrent.databinding.FragmentOnboardingSetupBinding;

public class OnboardingSetupFragment extends Fragment {
    private FragmentOnboardingSetupBinding binding;
    private OnboardingCompletionListener completionListener;
    private SettingsRepository pref;

    public interface OnboardingCompletionListener {
        void onOnboardingComplete();
        void onChooseStorageLocation();
    }

    public static OnboardingSetupFragment newInstance() {
        return new OnboardingSetupFragment();
    }

    public void setCompletionListener(OnboardingCompletionListener listener) {
        this.completionListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = RepositoryHelper.getSettingsRepository(requireContext().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingSetupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set initial Wi-Fi only state from preferences
        binding.wifiOnlySwitch.setChecked(pref.unmeteredConnectionsOnly());
        
        // Update preference when switch changes
        binding.wifiOnlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pref.unmeteredConnectionsOnly(isChecked);
        });

        // Handle storage location selection
        binding.chooseStorageButton.setOnClickListener(v -> {
            if (completionListener != null) {
                completionListener.onChooseStorageLocation();
            }
        });

        // Show current storage path if set
        Uri savePath = pref.saveTorrentsIn();
        if (savePath != null) {
            binding.storageLocationPath.setText(savePath.getPath());
            binding.storageLocationPath.setVisibility(View.VISIBLE);
        }

        binding.finishButton.setOnClickListener(v -> {
            if (completionListener != null) {
                completionListener.onOnboardingComplete();
            }
        });
    }

    public void updateStorageLocation(Uri uri) {
        if (binding != null && uri != null) {
            binding.storageLocationPath.setText(uri.getPath());
            binding.storageLocationPath.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

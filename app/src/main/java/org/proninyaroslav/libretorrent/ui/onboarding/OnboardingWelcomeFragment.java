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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.proninyaroslav.libretorrent.databinding.FragmentOnboardingWelcomeBinding;

public class OnboardingWelcomeFragment extends Fragment {
    private FragmentOnboardingWelcomeBinding binding;
    private OnboardingNavigationListener navigationListener;

    public interface OnboardingNavigationListener {
        void onNavigateToNext();
    }

    public static OnboardingWelcomeFragment newInstance() {
        return new OnboardingWelcomeFragment();
    }

    public void setNavigationListener(OnboardingNavigationListener listener) {
        this.navigationListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.getStartedButton.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.onNavigateToNext();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

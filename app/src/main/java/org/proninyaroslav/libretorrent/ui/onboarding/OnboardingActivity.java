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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.proninyaroslav.libretorrent.MainActivity;
import org.proninyaroslav.libretorrent.R;
import org.proninyaroslav.libretorrent.core.RepositoryHelper;
import org.proninyaroslav.libretorrent.core.settings.SettingsRepository;
import org.proninyaroslav.libretorrent.ui.base.ThemeActivity;

public class OnboardingActivity extends ThemeActivity 
        implements TermsOfServiceDialog.TermsAcceptanceListener,
                   OnboardingWelcomeFragment.OnboardingNavigationListener,
                   OnboardingSetupFragment.OnboardingCompletionListener {

    private static final String TAG = OnboardingActivity.class.getSimpleName();
    private static final int STORAGE_SELECTION_REQUEST_CODE = 1001;

    private SettingsRepository pref;
    private OnboardingSetupFragment setupFragment;
    private androidx.activity.OnBackPressedCallback onBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        pref = RepositoryHelper.getSettingsRepository(getApplicationContext());

        // Disable back button during onboarding
        onBackPressedCallback = new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Prevent back navigation during onboarding
                // User must complete or decline terms
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        // Check if terms have been accepted
        if (!pref.termsAccepted()) {
            showTermsOfService();
        } else {
            showWelcomeScreen();
        }
    }

    private void showTermsOfService() {
        TermsOfServiceDialog dialog = TermsOfServiceDialog.newInstance();
        dialog.show(getSupportFragmentManager(), "terms_dialog");
    }

    @Override
    public void onTermsAccepted() {
        pref.termsAccepted(true);
        showWelcomeScreen();
    }

    @Override
    public void onTermsDeclined() {
        Toast.makeText(this, R.string.terms_required, Toast.LENGTH_LONG).show();
        finish();
    }

    private void showWelcomeScreen() {
        OnboardingWelcomeFragment welcomeFragment = OnboardingWelcomeFragment.newInstance();
        welcomeFragment.setNavigationListener(this);
        showFragment(welcomeFragment);
    }

    @Override
    public void onNavigateToNext() {
        showSetupScreen();
    }

    private void showSetupScreen() {
        setupFragment = OnboardingSetupFragment.newInstance();
        setupFragment.setCompletionListener(this);
        showFragment(setupFragment);
    }

    @Override
    public void onChooseStorageLocation() {
        // Placeholder - storage selection will be added in future iteration
        // For now, users can change it in settings after onboarding
        Toast.makeText(this, 
            "You can configure storage location in Settings after completing setup", 
            Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOnboardingComplete() {
        pref.onboardingCompleted(true);
        pref.firstLaunch(false);
        
        // Navigate to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.onboarding_container, fragment)
                .commit();
    }
}

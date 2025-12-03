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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.proninyaroslav.libretorrent.databinding.DialogTermsOfServiceBinding;

public class TermsOfServiceDialog extends DialogFragment {
    private static final String TAG = TermsOfServiceDialog.class.getSimpleName();

    private DialogTermsOfServiceBinding binding;
    private TermsAcceptanceListener listener;

    public interface TermsAcceptanceListener {
        void onTermsAccepted();
        void onTermsDeclined();
    }

    public static TermsOfServiceDialog newInstance() {
        return new TermsOfServiceDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TermsAcceptanceListener) {
            listener = (TermsAcceptanceListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogTermsOfServiceBinding.inflate(LayoutInflater.from(getContext()));

        binding.acceptButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTermsAccepted();
            }
            dismiss();
        });

        binding.declineButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTermsDeclined();
            }
            dismiss();
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .create();
        
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);
        
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

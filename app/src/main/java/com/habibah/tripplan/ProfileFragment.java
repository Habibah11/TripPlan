package com.habibah.tripplan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private TextView tvAvatar, tvName, tvEmail, tvStatTrips, tvStatSpent;
    private TextInputEditText etName, etEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvAvatar = v.findViewById(R.id.tvAvatar);
        tvName = v.findViewById(R.id.tvProfileName);
        tvEmail = v.findViewById(R.id.tvProfileEmail);
        tvStatTrips = v.findViewById(R.id.tvStatTrips);
        tvStatSpent = v.findViewById(R.id.tvStatSpent);
        etName = v.findViewById(R.id.etProfileName);
        etEmail = v.findViewById(R.id.etProfileEmail);

        v.findViewById(R.id.btnSaveProfile).setOnClickListener(x -> {
            String name = Util.text(etName);
            if (name.isEmpty()) {
                etName.setError("Nama tidak boleh kosong");
                return;
            }
            PrefsManager p = new PrefsManager(requireContext());
            p.saveUserName(name);
            p.saveUserEmail(Util.text(etEmail));
            Util.toast(requireContext(), "Profil disimpan");
            showHeader();
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        PrefsManager p = new PrefsManager(requireContext());
        etName.setText(p.getUserName());
        etEmail.setText(p.getUserEmail());
        showHeader();
    }

    private void showHeader() {
        Context c = requireContext();
        PrefsManager p = new PrefsManager(c);
        DatabaseHelper db = DatabaseHelper.getInstance(c);
        String name = p.getUserName();
        tvAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase());
        tvName.setText(name);
        tvEmail.setText(p.getUserEmail().isEmpty() ? "Email belum diisi" : p.getUserEmail());
        tvStatTrips.setText(db.countAllTrips() + " perjalanan");
        tvStatSpent.setText(Util.formatMoney(c, db.totalExpenses()) + " dibelanjakan");
    }
}

package com.habibah.tripplan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private SwitchMaterial swDark, swConfirm, swTips;
    private Spinner spCurrency;
    private PrefsManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = new PrefsManager(requireContext());
        swDark = v.findViewById(R.id.swDark);
        swConfirm = v.findViewById(R.id.swConfirm);
        swTips = v.findViewById(R.id.swTips);
        spCurrency = v.findViewById(R.id.spCurrencySetting);
        spCurrency.setAdapter(Util.spinnerAdapter(requireContext(), R.array.currencies));

        v.findViewById(R.id.btnResetOnboarding).setOnClickListener(x -> {
            prefs.setOnboardingDone(false);
            Util.toast(requireContext(), "Onboarding akan tampil lagi saat aplikasi dibuka ulang");
        });

        v.findViewById(R.id.btnClearData).setOnClickListener(x ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Hapus semua data?")
                        .setMessage("Seluruh perjalanan, destinasi, itinerary, dan pengeluaran akan dihapus permanen.")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Hapus Semua", (d, w) -> {
                            DatabaseHelper.getInstance(requireContext()).clearAll();
                            Util.toast(requireContext(), "Semua data dihapus");
                        })
                        .show());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPrefs();
    }

    /** Memuat ulang nilai tersimpan ke kontrol, lalu memasang listener penyimpan. */
    private void loadPrefs() {
        swDark.setOnCheckedChangeListener(null);
        swConfirm.setOnCheckedChangeListener(null);
        swTips.setOnCheckedChangeListener(null);
        spCurrency.setOnItemSelectedListener(null);

        swDark.setChecked(prefs.isDarkMode());
        swConfirm.setChecked(prefs.isConfirmDelete());
        swTips.setChecked(prefs.isShowTips());
        spCurrency.setSelection(Util.currencyIndex(requireContext(), prefs.getCurrency()), false);

        swDark.setOnCheckedChangeListener((b, checked) -> {
            prefs.saveDarkMode(checked);
            prefs.applyTheme();
        });
        swConfirm.setOnCheckedChangeListener((b, checked) -> prefs.saveConfirmDelete(checked));
        swTips.setOnCheckedChangeListener((b, checked) -> prefs.saveShowTips(checked));
        spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String code = Util.currencyCode(parent.getItemAtPosition(position).toString());
                if (!code.equals(prefs.getCurrency())) {
                    prefs.saveCurrency(code);
                    Util.toast(requireContext(), "Mata uang diubah ke " + code);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

package com.habibah.tripplan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        final PrefsManager prefs = new PrefsManager(this);
        final TextInputEditText etName = findViewById(R.id.etName);
        final Spinner sp = findViewById(R.id.spCurrency);
        MaterialButton btn = findViewById(R.id.btnStart);

        sp.setAdapter(Util.spinnerAdapter(this, R.array.currencies));
        sp.setSelection(Util.currencyIndex(this, prefs.getCurrency()));

        btn.setOnClickListener(v -> {
            String name = Util.text(etName);
            if (name.isEmpty()) {
                etName.setError("Nama wajib diisi");
                return;
            }
            prefs.saveUserName(name);
            prefs.saveCurrency(Util.currencyCode(sp.getSelectedItem().toString()));
            prefs.setOnboardingDone(true);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}

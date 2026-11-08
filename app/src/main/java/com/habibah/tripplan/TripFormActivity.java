package com.habibah.tripplan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class TripFormActivity extends AppCompatActivity {

    public static final String EXTRA_TRIP_ID = "trip_id";

    private DatabaseHelper db;
    private Models.Trip editing;
    private TextInputEditText etName, etDest, etStart, etEnd, etBudget, etNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_form);
        db = DatabaseHelper.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etTripName);
        etDest = findViewById(R.id.etTripDest);
        etStart = findViewById(R.id.etStart);
        etEnd = findViewById(R.id.etEnd);
        etBudget = findViewById(R.id.etBudget);
        etNotes = findViewById(R.id.etNotes);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        etStart.setOnClickListener(v -> Util.pickDate(this, etStart));
        etEnd.setOnClickListener(v -> Util.pickDate(this, etEnd));

        long id = getIntent().getLongExtra(EXTRA_TRIP_ID, -1);
        if (id > 0) {
            editing = db.getTrip(id);
        }
        if (editing != null) {
            setTitle("Ubah Perjalanan");
            etName.setText(editing.name);
            etDest.setText(editing.destination);
            etStart.setText(editing.startDate);
            etEnd.setText(editing.endDate);
            etBudget.setText((long) editing.budget == editing.budget
                    ? String.valueOf((long) editing.budget) : String.valueOf(editing.budget));
            etNotes.setText(editing.notes);
        } else {
            setTitle("Tambah Perjalanan");
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String name = Util.text(etName);
        String dest = Util.text(etDest);
        String start = Util.text(etStart);
        String end = Util.text(etEnd);
        String budgetStr = Util.text(etBudget);

        if (name.isEmpty()) {
            etName.setError("Nama perjalanan wajib diisi");
            return;
        }
        if (dest.isEmpty()) {
            etDest.setError("Tujuan wajib diisi");
            return;
        }
        if (start.isEmpty()) {
            etStart.setError("Pilih tanggal mulai");
            return;
        }
        if (end.isEmpty()) {
            etEnd.setError("Pilih tanggal selesai");
            return;
        }
        if (end.compareTo(start) < 0) {
            etEnd.setError("Tanggal selesai tidak boleh sebelum tanggal mulai");
            return;
        }
        double budget = 0;
        if (!budgetStr.isEmpty()) {
            budget = Util.parseAmount(budgetStr);
            if (budget < 0) {
                etBudget.setError("Anggaran tidak valid");
                return;
            }
        }

        Models.Trip t = editing != null ? editing : new Models.Trip();
        t.name = name;
        t.destination = dest;
        t.startDate = start;
        t.endDate = end;
        t.budget = budget;
        t.notes = Util.text(etNotes);

        if (editing != null) {
            db.updateTrip(t);
            Util.toast(this, "Perjalanan diperbarui");
        } else {
            db.insertTrip(t);
            Util.toast(this, "Perjalanan disimpan");
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

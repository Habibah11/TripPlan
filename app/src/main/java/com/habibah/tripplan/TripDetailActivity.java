package com.habibah.tripplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class TripDetailActivity extends AppCompatActivity {

    private long tripId;
    private DatabaseHelper db;
    private Models.Trip trip;
    private TextView tvDest, tvDates, tvStatus, tvBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        db = DatabaseHelper.getInstance(this);
        tripId = getIntent().getLongExtra(TripFormActivity.EXTRA_TRIP_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvDest = findViewById(R.id.tvDetDest);
        tvDates = findViewById(R.id.tvDetDates);
        tvStatus = findViewById(R.id.tvDetStatus);
        tvBudget = findViewById(R.id.tvDetBudget);

        trip = db.getTrip(tripId);
        if (trip == null) {
            finish();
            return;
        }

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Destinasi"));
        tabs.addTab(tabs.newTab().setText("Itinerary"));
        tabs.addTab(tabs.newTab().setText("Anggaran"));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        showTab(0);
    }

    private void showTab(int pos) {
        Fragment f;
        if (pos == 0) f = DestinationsFragment.newInstance(tripId);
        else if (pos == 1) f = ItineraryFragment.newInstance(tripId);
        else f = BudgetFragment.newInstance(tripId);
        getSupportFragmentManager().beginTransaction().replace(R.id.detailContainer, f).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHeader();
    }

    /** Memuat ulang ringkasan perjalanan di bagian atas. */
    public void refreshHeader() {
        trip = db.getTrip(tripId);
        if (trip == null) {
            finish();
            return;
        }
        setTitle(trip.name);
        tvDest.setText(trip.destination);
        tvDates.setText(Util.range(trip.startDate, trip.endDate) + "  (" + Util.tripDays(trip) + " hari)");
        tvStatus.setText(Util.statusLabel(trip, Util.today()));
        tvBudget.setText("Anggaran " + Util.formatMoney(this, trip.budget)
                + "  \u2022  Terpakai " + Util.formatMoney(this, db.expensesForTrip(tripId)));
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem done = menu.findItem(R.id.action_done);
        if (done != null) done.setVisible(trip != null && trip.status == 0);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent i = new Intent(this, TripFormActivity.class);
            i.putExtra(TripFormActivity.EXTRA_TRIP_ID, tripId);
            startActivity(i);
            return true;
        } else if (id == R.id.action_delete) {
            Util.confirmDelete(this, "Hapus perjalanan ini beserta seluruh datanya?", () -> {
                db.deleteTrip(tripId);
                Util.toast(this, "Perjalanan dihapus");
                finish();
            });
            return true;
        } else if (id == R.id.action_done) {
            db.updateTripStatus(tripId, 1);
            Util.toast(this, "Perjalanan ditandai selesai dan masuk riwayat");
            refreshHeader();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

package com.habibah.tripplan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ItineraryFragment extends BaseListFragment {

    private static final String ARG_TRIP = "trip_id";

    public static ItineraryFragment newInstance(long tripId) {
        ItineraryFragment f = new ItineraryFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_TRIP, tripId);
        f.setArguments(b);
        return f;
    }

    private long tripId() {
        return requireArguments().getLong(ARG_TRIP);
    }

    @Override
    protected String title() {
        return null;
    }

    @Override
    protected String emptyText() {
        return "Belum ada jadwal.\nTap + untuk menambah kegiatan per hari dan jam.";
    }

    @Override
    protected boolean hasEdit() {
        return false;
    }

    @Override
    protected List<RowAdapter.Row> loadRows() {
        Models.Trip trip = db.getTrip(tripId());
        List<RowAdapter.Row> out = new ArrayList<>();
        for (Models.Itinerary it : db.getItineraries(tripId())) {
            RowAdapter.Row r = new RowAdapter.Row();
            r.id = it.id;
            r.title = it.time + "  \u2022  " + it.activity;
            String date = trip == null ? "" : Util.fmtDate(Util.addDays(trip.startDate, it.day - 1));
            String place = it.place.isEmpty() ? "" : it.place + "  \u2022  ";
            r.subtitle = place + date;
            r.meta = "Hari " + it.day;
            r.icon = R.drawable.ic_time;
            r.tag = it;
            out.add(r);
        }
        return out;
    }

    @Override
    protected void onAdd() {
        showForm(null);
    }

    @Override
    public void onRowClick(RowAdapter.Row row) {
        showForm((Models.Itinerary) row.tag);
    }

    @Override
    public void onRowDelete(final RowAdapter.Row row) {
        Util.confirmDelete(requireContext(), "Hapus kegiatan \"" + row.title + "\"?", () -> {
            db.deleteItinerary(row.id);
            Util.toast(requireContext(), "Kegiatan dihapus");
            refresh();
        });
    }

    private void showForm(final Models.Itinerary ex) {
        final Context c = requireContext();
        Models.Trip trip = db.getTrip(tripId());
        final int maxDay = trip == null ? 1 : Math.max(1, Util.tripDays(trip));

        View v = LayoutInflater.from(c).inflate(R.layout.dialog_itinerary, null);
        final TextInputEditText etDay = v.findViewById(R.id.etDay);
        final TextInputEditText etTime = v.findViewById(R.id.etTime);
        final TextInputEditText etActivity = v.findViewById(R.id.etActivity);
        final TextInputEditText etPlace = v.findViewById(R.id.etPlace);
        etDay.setHint("1 - " + maxDay);
        etTime.setOnClickListener(x -> Util.pickTime(c, etTime));

        if (ex != null) {
            etDay.setText(String.valueOf(ex.day));
            etTime.setText(ex.time);
            etActivity.setText(ex.activity);
            etPlace.setText(ex.place);
        } else {
            etDay.setText("1");
        }

        final AlertDialog d = new MaterialAlertDialogBuilder(c)
                .setTitle(ex == null ? "Tambah Jadwal" : "Ubah Jadwal")
                .setView(v)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", null)
                .create();
        d.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(x -> {
            int day;
            try {
                day = Integer.parseInt(Util.text(etDay));
            } catch (NumberFormatException e) {
                day = -1;
            }
            if (day < 1 || day > maxDay) {
                etDay.setError("Hari harus 1 sampai " + maxDay);
                return;
            }
            if (Util.text(etTime).isEmpty()) {
                etTime.setError("Pilih jam");
                return;
            }
            if (Util.text(etActivity).isEmpty()) {
                etActivity.setError("Kegiatan wajib diisi");
                return;
            }
            Models.Itinerary m = ex == null ? new Models.Itinerary() : ex;
            m.tripId = tripId();
            m.day = day;
            m.time = Util.text(etTime);
            m.activity = Util.text(etActivity);
            m.place = Util.text(etPlace);
            if (ex == null) db.insertItinerary(m);
            else db.updateItinerary(m);
            Util.toast(c, "Jadwal disimpan");
            d.dismiss();
            refresh();
        });
    }
}

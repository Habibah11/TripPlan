package com.habibah.tripplan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DestinationsFragment extends BaseListFragment {

    private static final String ARG_TRIP = "trip_id";

    public static DestinationsFragment newInstance(long tripId) {
        DestinationsFragment f = new DestinationsFragment();
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
        return "Belum ada destinasi.\nTap + untuk menambah destinasi wisata.";
    }

    @Override
    protected boolean hasEdit() {
        return false;
    }

    @Override
    protected List<RowAdapter.Row> loadRows() {
        List<RowAdapter.Row> out = new ArrayList<>();
        for (Models.Destination d : db.getDestinations(tripId())) {
            RowAdapter.Row r = new RowAdapter.Row();
            r.id = d.id;
            r.title = d.name;
            String loc = d.location.isEmpty() ? "" : "  \u2022  " + d.location;
            r.subtitle = d.category + loc;
            r.meta = d.visited ? "Dikunjungi" : "Belum";
            r.metaColor = d.visited ? requireContext().getColor(R.color.success) : 0;
            r.icon = R.drawable.ic_place;
            r.tag = d;
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
        showForm((Models.Destination) row.tag);
    }

    @Override
    public void onRowDelete(final RowAdapter.Row row) {
        Util.confirmDelete(requireContext(), "Hapus destinasi \"" + row.title + "\"?", () -> {
            db.deleteDestination(row.id);
            Util.toast(requireContext(), "Destinasi dihapus");
            refresh();
        });
    }

    private void showForm(final Models.Destination ex) {
        final Context c = requireContext();
        View v = LayoutInflater.from(c).inflate(R.layout.dialog_destination, null);
        final TextInputEditText etName = v.findViewById(R.id.etDestName);
        final TextInputEditText etLoc = v.findViewById(R.id.etDestLocation);
        final TextInputEditText etNotes = v.findViewById(R.id.etDestNotes);
        final Spinner sp = v.findViewById(R.id.spDestCategory);
        final CheckBox cb = v.findViewById(R.id.cbVisited);
        sp.setAdapter(Util.spinnerAdapter(c, R.array.dest_categories));

        if (ex != null) {
            etName.setText(ex.name);
            etLoc.setText(ex.location);
            etNotes.setText(ex.notes);
            cb.setChecked(ex.visited);
            Util.selectSpinner(sp, ex.category);
        }

        final AlertDialog d = new MaterialAlertDialogBuilder(c)
                .setTitle(ex == null ? "Tambah Destinasi" : "Ubah Destinasi")
                .setView(v)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", null)
                .create();
        d.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(x -> {
            String name = Util.text(etName);
            if (name.isEmpty()) {
                etName.setError("Nama destinasi wajib diisi");
                return;
            }
            Models.Destination m = ex == null ? new Models.Destination() : ex;
            m.tripId = tripId();
            m.name = name;
            m.location = Util.text(etLoc);
            m.notes = Util.text(etNotes);
            m.category = sp.getSelectedItem().toString();
            m.visited = cb.isChecked();
            if (ex == null) db.insertDestination(m);
            else db.updateDestination(m);
            Util.toast(c, "Destinasi disimpan");
            d.dismiss();
            refresh();
        });
    }
}

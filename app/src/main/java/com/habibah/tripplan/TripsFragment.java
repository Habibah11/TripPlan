package com.habibah.tripplan;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class TripsFragment extends BaseListFragment {

    @Override
    protected String title() {
        return "Perjalanan Saya";
    }

    @Override
    protected String emptyText() {
        return "Belum ada perjalanan.\nTap tombol + untuk menambah perjalanan baru.";
    }

    @Override
    protected List<RowAdapter.Row> loadRows() {
        String today = Util.today();
        List<RowAdapter.Row> out = new ArrayList<>();
        for (Models.Trip t : db.getTrips(false, today)) {
            RowAdapter.Row r = new RowAdapter.Row();
            r.id = t.id;
            r.title = t.name;
            r.subtitle = t.destination + "  \u2022  " + Util.range(t.startDate, t.endDate);
            r.meta = Util.statusLabel(t, today);
            r.icon = R.drawable.ic_plane;
            out.add(r);
        }
        return out;
    }

    @Override
    protected void onAdd() {
        startActivity(new Intent(requireContext(), TripFormActivity.class));
    }

    @Override
    public void onRowClick(RowAdapter.Row row) {
        Intent i = new Intent(requireContext(), TripDetailActivity.class);
        i.putExtra(TripFormActivity.EXTRA_TRIP_ID, row.id);
        startActivity(i);
    }

    @Override
    public void onRowEdit(RowAdapter.Row row) {
        Intent i = new Intent(requireContext(), TripFormActivity.class);
        i.putExtra(TripFormActivity.EXTRA_TRIP_ID, row.id);
        startActivity(i);
    }

    @Override
    public void onRowDelete(final RowAdapter.Row row) {
        Util.confirmDelete(requireContext(),
                "Hapus perjalanan \"" + row.title + "\" beserta seluruh destinasi, itinerary, dan pengeluarannya?",
                () -> {
                    db.deleteTrip(row.id);
                    Util.toast(requireContext(), "Perjalanan dihapus");
                    refresh();
                });
    }
}

package com.habibah.tripplan;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseListFragment {

    @Override
    protected String title() {
        return "Riwayat Perjalanan";
    }

    @Override
    protected String emptyText() {
        return "Belum ada riwayat.\nPerjalanan yang sudah selesai atau berakhir akan muncul di sini.";
    }

    @Override
    protected boolean hasFab() {
        return false;
    }

    @Override
    protected boolean hasEdit() {
        return false;
    }

    @Override
    protected List<RowAdapter.Row> loadRows() {
        String today = Util.today();
        List<RowAdapter.Row> out = new ArrayList<>();
        for (Models.Trip t : db.getTrips(true, today)) {
            RowAdapter.Row r = new RowAdapter.Row();
            r.id = t.id;
            r.title = t.name;
            r.subtitle = t.destination + "  \u2022  " + Util.range(t.startDate, t.endDate);
            r.meta = Util.formatMoney(requireContext(), db.expensesForTrip(t.id));
            r.icon = R.drawable.ic_history;
            out.add(r);
        }
        return out;
    }

    @Override
    protected void bindSummary() {
        String today = Util.today();
        int n = db.countHistoryTrips(today);
        if (n == 0) {
            tvSummary.setVisibility(View.GONE);
            return;
        }
        tvSummary.setVisibility(View.VISIBLE);
        tvSummary.setText(n + " perjalanan selesai  \u2022  Total pengeluaran "
                + Util.formatMoney(requireContext(), db.expensesForHistory(today)));
    }

    @Override
    public void onRowClick(RowAdapter.Row row) {
        Intent i = new Intent(requireContext(), TripDetailActivity.class);
        i.putExtra(TripFormActivity.EXTRA_TRIP_ID, row.id);
        startActivity(i);
    }

    @Override
    public void onRowDelete(final RowAdapter.Row row) {
        Util.confirmDelete(requireContext(), "Hapus \"" + row.title + "\" dari riwayat?", () -> {
            db.deleteTrip(row.id);
            Util.toast(requireContext(), "Riwayat dihapus");
            refresh();
        });
    }
}

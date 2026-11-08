package com.habibah.tripplan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends BaseListFragment {

    private static final String ARG_TRIP = "trip_id";

    public static BudgetFragment newInstance(long tripId) {
        BudgetFragment f = new BudgetFragment();
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
        return "Belum ada pengeluaran.\nTap + untuk mencatat pengeluaran.";
    }

    @Override
    protected boolean hasEdit() {
        return false;
    }

    @Override
    protected List<RowAdapter.Row> loadRows() {
        List<RowAdapter.Row> out = new ArrayList<>();
        for (Models.Expense e : db.getExpenses(tripId())) {
            RowAdapter.Row r = new RowAdapter.Row();
            r.id = e.id;
            r.title = e.title;
            r.subtitle = e.category + "  \u2022  " + Util.fmtDate(e.date);
            r.meta = Util.formatMoney(requireContext(), e.amount);
            r.icon = R.drawable.ic_wallet;
            r.tag = e;
            out.add(r);
        }
        return out;
    }

    @Override
    protected void bindSummary() {
        Context c = requireContext();
        Models.Trip trip = db.getTrip(tripId());
        if (trip == null) return;
        double spent = db.expensesForTrip(tripId());
        double remaining = trip.budget - spent;
        StringBuilder sb = new StringBuilder();
        sb.append("Anggaran: ").append(Util.formatMoney(c, trip.budget)).append("\n");
        sb.append("Terpakai: ").append(Util.formatMoney(c, spent)).append("\n");
        sb.append("Sisa: ").append(Util.formatMoney(c, remaining));
        boolean over = spent > trip.budget;
        if (over) sb.append("\nPeringatan: pengeluaran melebihi anggaran!");
        tvSummary.setText(sb.toString());
        tvSummary.setTextColor(c.getColor(over ? R.color.danger : R.color.text_primary));
        tvSummary.setVisibility(View.VISIBLE);

        int pct = trip.budget <= 0 ? (spent > 0 ? 100 : 0) : (int) Math.min(100, Math.round(spent / trip.budget * 100));
        progress.setMax(100);
        progress.setProgress(pct);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onAdd() {
        showForm(null);
    }

    @Override
    public void onRowClick(RowAdapter.Row row) {
        showForm((Models.Expense) row.tag);
    }

    @Override
    public void onRowDelete(final RowAdapter.Row row) {
        Util.confirmDelete(requireContext(), "Hapus pengeluaran \"" + row.title + "\"?", () -> {
            db.deleteExpense(row.id);
            Util.toast(requireContext(), "Pengeluaran dihapus");
            refresh();
            notifyHeader();
        });
    }

    private void notifyHeader() {
        if (getActivity() instanceof TripDetailActivity) {
            ((TripDetailActivity) getActivity()).refreshHeader();
        }
    }

    private void showForm(final Models.Expense ex) {
        final Context c = requireContext();
        View v = LayoutInflater.from(c).inflate(R.layout.dialog_expense, null);
        final TextInputEditText etTitle = v.findViewById(R.id.etExpTitle);
        final TextInputEditText etAmount = v.findViewById(R.id.etExpAmount);
        final TextInputEditText etDate = v.findViewById(R.id.etExpDate);
        final Spinner sp = v.findViewById(R.id.spExpCategory);
        sp.setAdapter(Util.spinnerAdapter(c, R.array.expense_categories));
        etDate.setOnClickListener(x -> Util.pickDate(c, etDate));

        if (ex != null) {
            etTitle.setText(ex.title);
            etAmount.setText((long) ex.amount == ex.amount ? String.valueOf((long) ex.amount) : String.valueOf(ex.amount));
            etDate.setText(ex.date);
            Util.selectSpinner(sp, ex.category);
        } else {
            etDate.setText(Util.today());
        }

        final AlertDialog d = new MaterialAlertDialogBuilder(c)
                .setTitle(ex == null ? "Catat Pengeluaran" : "Ubah Pengeluaran")
                .setView(v)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", null)
                .create();
        d.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(x -> {
            if (Util.text(etTitle).isEmpty()) {
                etTitle.setError("Keterangan wajib diisi");
                return;
            }
            double amount = Util.parseAmount(Util.text(etAmount));
            if (amount <= 0) {
                etAmount.setError("Jumlah harus lebih dari 0");
                return;
            }
            if (Util.text(etDate).isEmpty()) {
                etDate.setError("Pilih tanggal");
                return;
            }
            Models.Expense m = ex == null ? new Models.Expense() : ex;
            m.tripId = tripId();
            m.title = Util.text(etTitle);
            m.category = sp.getSelectedItem().toString();
            m.amount = amount;
            m.date = Util.text(etDate);
            if (ex == null) db.insertExpense(m);
            else db.updateExpense(m);
            Util.toast(c, "Pengeluaran disimpan");
            d.dismiss();
            refresh();
            notifyHeader();
        });
    }
}

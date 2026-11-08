package com.habibah.tripplan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/** Kerangka bersama untuk semua halaman berbentuk daftar. */
public abstract class BaseListFragment extends Fragment implements RowAdapter.Listener {

    protected DatabaseHelper db;
    protected PrefsManager prefs;
    protected RowAdapter adapter;
    protected TextView tvHeader, tvSummary, tvEmpty;
    protected ProgressBar progress;
    protected FloatingActionButton fab;

    /** Judul halaman, null jika tidak ditampilkan. */
    protected abstract String title();

    protected abstract String emptyText();

    protected abstract List<RowAdapter.Row> loadRows();

    protected boolean hasFab() {
        return true;
    }

    protected boolean hasEdit() {
        return true;
    }

    protected void onAdd() {
    }

    protected void bindSummary() {
    }

    @Override
    public void onRowEdit(RowAdapter.Row row) {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        db = DatabaseHelper.getInstance(requireContext());
        prefs = new PrefsManager(requireContext());

        tvHeader = v.findViewById(R.id.tvHeader);
        tvSummary = v.findViewById(R.id.tvSummary);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        progress = v.findViewById(R.id.progress);
        fab = v.findViewById(R.id.fab);

        String t = title();
        if (t == null) {
            tvHeader.setVisibility(View.GONE);
        } else {
            tvHeader.setText(t);
        }
        tvEmpty.setText(emptyText());

        RecyclerView rv = v.findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RowAdapter(this, hasEdit());
        rv.setAdapter(adapter);

        fab.setVisibility(hasFab() ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(x -> onAdd());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    protected void refresh() {
        List<RowAdapter.Row> rows = loadRows();
        adapter.setRows(rows);
        tvEmpty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
        bindSummary();
    }
}

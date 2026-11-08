package com.habibah.tripplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Adapter generik untuk semua daftar (perjalanan, destinasi, itinerary, pengeluaran, riwayat). */
public class RowAdapter extends RecyclerView.Adapter<RowAdapter.VH> {

    public static class Row {
        public long id;
        public String title = "";
        public String subtitle = "";
        public String meta = "";
        public int icon = R.drawable.ic_plane;
        public int metaColor = 0;
        public Object tag;
    }

    public interface Listener {
        void onRowClick(Row row);

        void onRowEdit(Row row);

        void onRowDelete(Row row);
    }

    private final List<Row> rows = new ArrayList<>();
    private final Listener listener;
    private final boolean showEdit;

    public RowAdapter(Listener listener, boolean showEdit) {
        this.listener = listener;
        this.showEdit = showEdit;
    }

    public void setRows(List<Row> newRows) {
        rows.clear();
        rows.addAll(newRows);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH h, int position) {
        Row r = rows.get(position);
        h.title.setText(r.title);
        h.subtitle.setText(r.subtitle);
        h.subtitle.setVisibility(r.subtitle == null || r.subtitle.isEmpty() ? View.GONE : View.VISIBLE);
        h.meta.setText(r.meta);
        h.meta.setVisibility(r.meta == null || r.meta.isEmpty() ? View.GONE : View.VISIBLE);
        h.meta.setTextColor(r.metaColor != 0 ? r.metaColor
                : ContextCompat.getColor(h.itemView.getContext(), R.color.primary));
        h.icon.setImageResource(r.icon);
        h.edit.setVisibility(showEdit ? View.VISIBLE : View.GONE);

        h.itemView.setOnClickListener(v -> {
            int p = h.getBindingAdapterPosition();
            if (p != RecyclerView.NO_POSITION) listener.onRowClick(rows.get(p));
        });
        h.edit.setOnClickListener(v -> {
            int p = h.getBindingAdapterPosition();
            if (p != RecyclerView.NO_POSITION) listener.onRowEdit(rows.get(p));
        });
        h.delete.setOnClickListener(v -> {
            int p = h.getBindingAdapterPosition();
            if (p != RecyclerView.NO_POSITION) listener.onRowDelete(rows.get(p));
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title, subtitle, meta;
        final ImageButton edit, delete;

        VH(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.ivIcon);
            title = v.findViewById(R.id.tvTitle);
            subtitle = v.findViewById(R.id.tvSubtitle);
            meta = v.findViewById(R.id.tvMeta);
            edit = v.findViewById(R.id.ibEdit);
            delete = v.findViewById(R.id.ibDelete);
        }
    }
}

package com.habibah.tripplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final String[] TIPS = {
            "Catat pengeluaran setiap hari agar anggaran tetap terkendali.",
            "Susun itinerary per jam supaya waktu liburan lebih efisien.",
            "Sisihkan 10% anggaran sebagai dana tak terduga.",
            "Tandai destinasi yang sudah dikunjungi untuk memantau progres perjalanan.",
            "Bandingkan beberapa pilihan akomodasi sebelum memesan."
    };

    private View root;
    private TextView tvGreeting, tvHeroTitle, tvHeroSub, tvHeroCountdown, tvBudgetTotal, tvBudgetUsed, tvTips;
    private ProgressBar progressBudget;
    private View cardHero, cardTips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tvGreeting = root.findViewById(R.id.tvGreeting);
        cardHero = root.findViewById(R.id.cardHero);
        tvHeroTitle = root.findViewById(R.id.tvHeroTitle);
        tvHeroSub = root.findViewById(R.id.tvHeroSub);
        tvHeroCountdown = root.findViewById(R.id.tvHeroCountdown);
        tvBudgetTotal = root.findViewById(R.id.tvBudgetTotal);
        tvBudgetUsed = root.findViewById(R.id.tvBudgetUsed);
        progressBudget = root.findViewById(R.id.progressBudget);
        cardTips = root.findViewById(R.id.cardTips);
        tvTips = root.findViewById(R.id.tvTips);

        root.findViewById(R.id.btnNewTrip).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), TripFormActivity.class)));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    private void setStat(int includeId, int icon, String value, String label) {
        View v = root.findViewById(includeId);
        ((ImageView) v.findViewById(R.id.ivStat)).setImageResource(icon);
        ((TextView) v.findViewById(R.id.tvValue)).setText(value);
        ((TextView) v.findViewById(R.id.tvLabel)).setText(label);
    }

    private void bind() {
        Context c = requireContext();
        PrefsManager prefs = new PrefsManager(c);
        DatabaseHelper db = DatabaseHelper.getInstance(c);
        String today = Util.today();

        tvGreeting.setText("Halo, " + prefs.getUserName() + "!");

        List<Models.Trip> active = db.getTrips(false, today);
        if (!active.isEmpty()) {
            final Models.Trip next = active.get(0);
            tvHeroTitle.setText(next.name);
            tvHeroSub.setText(next.destination + "\n" + Util.range(next.startDate, next.endDate));
            tvHeroCountdown.setText(Util.statusLabel(next, today));
            tvHeroCountdown.setVisibility(View.VISIBLE);
            cardHero.setOnClickListener(v -> {
                Intent i = new Intent(requireContext(), TripDetailActivity.class);
                i.putExtra(TripFormActivity.EXTRA_TRIP_ID, next.id);
                startActivity(i);
            });
        } else {
            tvHeroTitle.setText("Belum ada perjalanan");
            tvHeroSub.setText("Mulai rencanakan perjalanan pertamamu sekarang.");
            tvHeroCountdown.setVisibility(View.GONE);
            cardHero.setOnClickListener(null);
        }

        setStat(R.id.stat1, R.drawable.ic_plane, String.valueOf(db.countActiveTrips(today)), "Perjalanan aktif");
        setStat(R.id.stat2, R.drawable.ic_history, String.valueOf(db.countHistoryTrips(today)), "Perjalanan selesai");
        setStat(R.id.stat3, R.drawable.ic_place, String.valueOf(db.countDestinations()), "Total destinasi");
        double spent = db.totalExpenses();
        double budget = db.totalBudget();
        setStat(R.id.stat4, R.drawable.ic_wallet, Util.formatMoney(c, spent), "Total pengeluaran");

        tvBudgetTotal.setText("Total anggaran: " + Util.formatMoney(c, budget));
        tvBudgetUsed.setText("Terpakai " + Util.formatMoney(c, spent) + "  \u2022  Sisa "
                + Util.formatMoney(c, budget - spent));
        int pct = budget <= 0 ? (spent > 0 ? 100 : 0) : (int) Math.min(100, Math.round(spent / budget * 100));
        progressBudget.setMax(100);
        progressBudget.setProgress(pct);

        cardTips.setVisibility(prefs.isShowTips() ? View.VISIBLE : View.GONE);
        int idx = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % TIPS.length;
        tvTips.setText(TIPS[idx]);
    }
}

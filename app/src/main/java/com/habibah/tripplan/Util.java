package com.habibah.tripplan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    private static SimpleDateFormat iso() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    public static String today() {
        return iso().format(new Date());
    }

    public static Date parse(String s) {
        try {
            return iso().parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String fmtDate(String isoDate) {
        Date d = parse(isoDate);
        if (d == null) return isoDate;
        return new SimpleDateFormat("d MMM yyyy", new Locale("id", "ID")).format(d);
    }

    public static String range(String start, String end) {
        return fmtDate(start) + " - " + fmtDate(end);
    }

    public static long daysBetween(String a, String b) {
        Date x = parse(a);
        Date y = parse(b);
        if (x == null || y == null) return 0;
        return Math.round((y.getTime() - x.getTime()) / 86400000.0);
    }

    public static String addDays(String isoDate, int days) {
        Date d = parse(isoDate);
        if (d == null) return isoDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return iso().format(cal.getTime());
    }

    public static int tripDays(Models.Trip t) {
        return (int) daysBetween(t.startDate, t.endDate) + 1;
    }

    public static String statusLabel(Models.Trip t, String today) {
        if (t.status == 1) return "Selesai";
        if (today.compareTo(t.startDate) < 0) {
            long n = daysBetween(today, t.startDate);
            return n + " hari lagi";
        }
        if (today.compareTo(t.endDate) <= 0) return "Berlangsung";
        return "Berakhir";
    }

    public static String text(TextView v) {
        CharSequence cs = v.getText();
        return cs == null ? "" : cs.toString().trim();
    }

    public static String text(Editable e) {
        return e == null ? "" : e.toString().trim();
    }

    public static double parseAmount(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public static void toast(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    // ---- Mata uang (label tampilan, tanpa konversi kurs) ----
    public static String symbol(String code) {
        switch (code) {
            case "USD": return "$";
            case "EUR": return "\u20AC";
            case "MYR": return "RM";
            case "SGD": return "S$";
            case "JPY": return "\u00A5";
            default: return "Rp";
        }
    }

    public static String formatMoney(Context c, double v) {
        String code = new PrefsManager(c).getCurrency();
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.US);
        if (code.equals("IDR")) {
            s.setGroupingSeparator('.');
            s.setDecimalSeparator(',');
        }
        boolean whole = code.equals("IDR") || code.equals("JPY");
        DecimalFormat df = new DecimalFormat(whole ? "#,##0" : "#,##0.00", s);
        return symbol(code) + " " + df.format(v);
    }

    public static String currencyCode(String entry) {
        return entry.length() >= 3 ? entry.substring(0, 3) : "IDR";
    }

    public static int currencyIndex(Context c, String code) {
        String[] arr = c.getResources().getStringArray(R.array.currencies);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith(code)) return i;
        }
        return 0;
    }

    // ---- Spinner ----
    public static ArrayAdapter<CharSequence> spinnerAdapter(Context c, int arrayRes) {
        ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(c, arrayRes,
                android.R.layout.simple_spinner_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return a;
    }

    public static void selectSpinner(Spinner sp, String value) {
        for (int i = 0; i < sp.getCount(); i++) {
            if (sp.getItemAtPosition(i).toString().equals(value)) {
                sp.setSelection(i);
                return;
            }
        }
    }

    // ---- Picker ----
    public static void pickDate(Context c, final TextView target) {
        Calendar cal = Calendar.getInstance();
        Date d = parse(target.getText().toString());
        if (d != null) cal.setTime(d);
        new DatePickerDialog(c, (view, y, m, day) ->
                target.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, day)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void pickTime(Context c, final TextView target) {
        new TimePickerDialog(c, (view, h, m) ->
                target.setText(String.format(Locale.US, "%02d:%02d", h, m)), 8, 0, true).show();
    }

    // ---- Konfirmasi hapus (mengikuti pengaturan) ----
    public static void confirmDelete(Context c, String message, final Runnable onYes) {
        if (!new PrefsManager(c).isConfirmDelete()) {
            onYes.run();
            return;
        }
        new MaterialAlertDialogBuilder(c)
                .setTitle("Hapus data?")
                .setMessage(message)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", (d, w) -> onYes.run())
                .show();
    }
}

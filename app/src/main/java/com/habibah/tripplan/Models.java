package com.habibah.tripplan;

public class Models {

    public static class Trip {
        public long id;
        public String name = "";
        public String destination = "";
        public String startDate = "";
        public String endDate = "";
        public double budget;
        public String notes = "";
        public int status; // 0 = aktif, 1 = selesai
    }

    public static class Destination {
        public long id;
        public long tripId;
        public String name = "";
        public String location = "";
        public String category = "";
        public String notes = "";
        public boolean visited;
    }

    public static class Itinerary {
        public long id;
        public long tripId;
        public int day;
        public String time = "";
        public String activity = "";
        public String place = "";
    }

    public static class Expense {
        public long id;
        public long tripId;
        public String title = "";
        public String category = "";
        public double amount;
        public String date = "";
    }
}

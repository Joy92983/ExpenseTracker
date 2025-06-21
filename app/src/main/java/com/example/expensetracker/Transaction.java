package com.example.expensetracker;

public class Transaction {
    private float amount;
    private String type, category, date, note;

    public Transaction(float amount, String type, String category, String date, String note) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    public float getAmount() { return amount; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getNote() { return note; }
}

package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "expenses.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, amount REAL, type TEXT, category TEXT, date TEXT, note TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    public void insertTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", t.getAmount());
        values.put("type", t.getType());
        values.put("category", t.getCategory());
        values.put("date", t.getDate());
        values.put("note", t.getNote());
        db.insert("transactions", null, values);
    }

    public void deleteTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "date=? AND amount=? AND category=?", new String[]{t.getDate(), String.valueOf(t.getAmount()), t.getCategory()});
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", null, null);
    }



    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions ORDER BY date DESC", null);
        if (cursor.moveToFirst()) {
            do {
                float amount = cursor.getFloat(1);
                String type = cursor.getString(2);
                String category = cursor.getString(3);
                String date = cursor.getString(4);
                String note = cursor.getString(5);
                list.add(new Transaction(amount, type, category, date, note));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean exportToCSV(Context context) {
        File csvFile = new File(context.getExternalFilesDir(null), "transactions.csv");
        try {
            FileWriter writer = new FileWriter(csvFile);
            writer.append("Amount,Type,Category,Date,Note\n");
            for (Transaction t : getAllTransactions()) {
                writer.append(t.getAmount() + "," + t.getType() + "," + t.getCategory() + "," + t.getDate() + "," + t.getNote() + "\n");
            }
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Transaction> getTransactionsByMonth(String monthYear) {
            List<Transaction> list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE date LIKE ? ORDER BY date DESC", new String[]{monthYear + "%"});

            if (cursor.moveToFirst()) {
                do {
                    float amount = cursor.getFloat(1);
                    String type = cursor.getString(2);
                    String category = cursor.getString(3);
                    String date = cursor.getString(4);
                    String note = cursor.getString(5);
                    list.add(new Transaction(amount, type, category, date, note));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return list;
        }

}

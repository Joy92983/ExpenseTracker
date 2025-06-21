package com.example.expensetracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class SummaryActivity extends AppCompatActivity {

    TextView summaryText;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        summaryText = findViewById(R.id.summaryText);
        dbHelper = new DBHelper(this);

        // ✅ Step 3: Get current month transactions
        String currentMonthYear = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        List<Transaction> monthlyTransactions = dbHelper.getTransactionsByMonth(currentMonthYear);

        float totalIncome = 0;
        float totalExpense = 0;

        for (Transaction t : monthlyTransactions) {
            if (t.getType().equals("Income")) {
                totalIncome += t.getAmount();
            } else {
                totalExpense += t.getAmount();
            }
        }

        float balance = totalIncome - totalExpense;

        summaryText.setText(
                "Month: " + currentMonthYear + "\n\n" +
                        "Total Income: KES " + totalIncome + "\n" +
                        "Total Expense: KES " + totalExpense + "\n" +
                        "Balance: KES " + balance
        );
    }
}

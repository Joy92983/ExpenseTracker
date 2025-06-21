package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.widget.Toast;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import android.widget.Switch;

import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.File;



public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    TransactionAdapter adapter;
    TextView balanceText, incomeText, expenseText;
    PieChart pieChart;
    MaterialButton btnReset; // 🔸 Declare here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        balanceText = findViewById(R.id.textViewBalance);
        incomeText = findViewById(R.id.textViewIncome);
        expenseText = findViewById(R.id.textViewExpense);
        pieChart = findViewById(R.id.pieChart);

        Switch switchTheme = findViewById(R.id.switchTheme);
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean nightMode = preferences.getBoolean("night_mode", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchTheme.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchTheme.setChecked(false);
        }

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("night_mode", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("night_mode", false);
            }
            editor.apply();
            recreate(); // Recreate activity to apply theme
        });

        MaterialButton btnExport = findViewById(R.id.btnExport);

        btnExport.setOnClickListener(v -> {
            boolean success = dbHelper.exportToCSV(MainActivity.this);
            if (success) {
                File csvFile = new File(getExternalFilesDir(null), "transactions.csv");
                Uri fileUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.example.expensetracker.fileprovider",
                        csvFile
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/csv");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share CSV via"));
            } else {
                Toast.makeText(MainActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        });



        RecyclerView recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ Fix: Initialize the class-level adapter, not a local variable
        List<Transaction> transactions = dbHelper.getAllTransactions();

        adapter = new TransactionAdapter(this, transactions);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddTransactionActivity.class)));

        // 🔸 Reset Button Setup
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> {
            dbHelper.deleteAllTransactions();
            adapter.setTransactions(new ArrayList<>());
            updateSummary();
            Toast.makeText(this, "Reset done", Toast.LENGTH_SHORT).show();
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transaction t = adapter.getTransactions().get(position);
                dbHelper.deleteTransaction(t); // You'll implement this
                adapter.getTransactions().remove(position);
                adapter.notifyItemRemoved(position);
                updateSummary(); // Refresh summary after deletion
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);


        updateSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Safe usage after initialization
        adapter.setTransactions(dbHelper.getAllTransactions());
        updateSummary();
    }

    private void updateSummary() {
        float income = 0, expense = 0;
        for (Transaction t : dbHelper.getAllTransactions()) {
            if ("Income".equals(t.getType())) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
            }
        }

        float balance = income - expense;
        balanceText.setText("Balance: KES " + balance);
        incomeText.setText("Income: KES " + income);
        expenseText.setText("Expense: KES " + expense);

        FloatingActionButton fabSummary = findViewById(R.id.fabSummary);
        fabSummary.setOnClickListener(v -> startActivity(new Intent(this, SummaryActivity.class)));


        ArrayList<PieEntry> entries = new ArrayList<>();
        if (income > 0) entries.add(new PieEntry(income, "Income"));
        if (expense > 0) entries.add(new PieEntry(expense, "Expense"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setCenterText("Income vs Expense");
        pieChart.setUsePercentValues(true);
        pieChart.invalidate(); // refresh chart

        String month = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        List<Transaction> monthTransactions = dbHelper.getTransactionsByMonth(month);
        adapter.setTransactions(monthTransactions);

        for (Transaction t : monthTransactions) {
            Log.d("MonthlySummary", "Date: " + t.getDate() + ", Amount: " + t.getAmount());
        }


    }
}

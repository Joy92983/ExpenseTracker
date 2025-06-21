package com.example.expensetracker;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    Spinner typeSpinner;
    EditText amountInput, categoryInput, dateInput, noteInput;
    Button saveButton;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        typeSpinner = findViewById(R.id.spinnerType);
        amountInput = findViewById(R.id.editTextAmount);
        categoryInput = findViewById(R.id.editTextCategory);
        dateInput = findViewById(R.id.editTextDate);
        noteInput = findViewById(R.id.editTextNote);
        saveButton = findViewById(R.id.buttonSave);
        dbHelper = new DBHelper(this);

        // Setup spinner items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Select Type", "Income", "Expense"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> {
            try {
                float amount = Float.parseFloat(amountInput.getText().toString());
                String category = categoryInput.getText().toString();
                String type = typeSpinner.getSelectedItem().toString();
                String date = dateInput.getText().toString();
                String note = noteInput.getText().toString();
                dbHelper.insertTransaction(new Transaction(amount, type, category, date, note));
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
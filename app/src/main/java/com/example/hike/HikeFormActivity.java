package com.example.hike;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class HikeFormActivity extends AppCompatActivity {
    private HikeDao hikeDao;
    private Hike hike;

    // promote views to fields so helper methods can access them
    private EditText name;
    private EditText location;
    private EditText date;
    private EditText parking;
    private Spinner spinnerDifficulty;
    private EditText description;
    private Button btnSave;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_form);

        // Enable Up navigation in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Hike Details");
        }

        hikeDao = new HikeDao(this);

        // ...existing code... assign views to the fields
        name = findViewById(R.id.name);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        parking = findViewById(R.id.parking);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        description = findViewById(R.id.description);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        Button navBack = findViewById(R.id.btnNavBack);
        Button navHome = findViewById(R.id.btnNavHome2);

        // Setup spinner with Low/Medium/High
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Low");
        adapter.add("Medium");
        adapter.add("High");
        spinnerDifficulty.setAdapter(adapter);

        // Date picker: show current date by default
        final Calendar cal = Calendar.getInstance();
        date.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));

        date.setOnClickListener(v -> {
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH);
            int d = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(HikeFormActivity.this, (view, year, month, dayOfMonth) -> {
                // month is 0-based
                String s = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                date.setText(s);
                cal.set(year, month, dayOfMonth);
            }, y, m, d);
            dp.show();
        });

        long id = getIntent().getLongExtra("hikeId", -1);
        if (id != -1) {
            hike = hikeDao.getById(id);
            if (hike != null) {
                name.setText(hike.name);
                location.setText(hike.location);
                date.setText(hike.date);
                parking.setText(hike.parking);
                // set spinner to matching difficulty
                if (hike.difficulty != null) {
                    String diff = hike.difficulty.trim();
                    if (diff.equalsIgnoreCase("low")) spinnerDifficulty.setSelection(0);
                    else if (diff.equalsIgnoreCase("medium")) spinnerDifficulty.setSelection(1);
                    else if (diff.equalsIgnoreCase("high")) spinnerDifficulty.setSelection(2);
                }
                description.setText(hike.description);
                if (getSupportActionBar() != null) getSupportActionBar().setTitle(hike.name);
            }
        } else {
            hike = new Hike();
        }

        // Show confirmation before saving
        btnSave.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Save hike")
            .setMessage("Save changes to this hike?")
            .setPositiveButton("Save", (dlg, which) -> performSave())
            .setNegativeButton("Cancel", null)
            .show());

        // Show confirmation before deleting
        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Delete hike")
            .setMessage("Are you sure you want to delete this hike? This cannot be undone.")
            .setPositiveButton("Delete", (dlg, which) -> performDelete())
            .setNegativeButton("Cancel", null)
            .show());

        navBack.setOnClickListener(v -> finish());
        navHome.setOnClickListener(v -> {
            Intent i = new Intent(HikeFormActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        });
    }

    // perform the actual save (validation + insert/update)
    private void performSave() {
        boolean ok = true;
        if (name.getText().toString().trim().isEmpty()) {
            name.setError("Name is required");
            ok = false;
        }
        if (location.getText().toString().trim().isEmpty()) {
            location.setError("Location is required");
            ok = false;
        }
        if (date.getText().toString().trim().isEmpty()) {
            date.setError("Date is required");
            ok = false;
        }
        if (parking.getText().toString().trim().isEmpty()) {
            parking.setError("Parking is required");
            ok = false;
        }
        String selectedDifficulty = (String) spinnerDifficulty.getSelectedItem();
        if (selectedDifficulty == null || selectedDifficulty.trim().isEmpty()) {
            ok = false;
        }

        if (!ok) return; // don't save

        hike.name = name.getText().toString().trim();
        hike.location = location.getText().toString().trim();
        hike.date = date.getText().toString().trim();
        hike.parking = parking.getText().toString().trim();
        hike.difficulty = selectedDifficulty;
        hike.description = description.getText().toString().trim();

        if (hike.id == 0) {
            hike.id = hikeDao.insert(hike);
        } else {
            hikeDao.update(hike);
        }
        finish();
    }

    // perform the actual deletion
    private void performDelete() {
        if (hike != null && hike.id != 0) {
            hikeDao.delete(hike.id);
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Treat Up like back
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.hike;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ObservationFormActivity extends AppCompatActivity {
    private ObservationDao obsDao;
    private HikeObservation obs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_form);

        // Enable Up navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        obsDao = new ObservationDao(this);

        TextView tvHike = findViewById(R.id.tvHikeName);
        EditText desc = findViewById(R.id.desc);
        EditText time = findViewById(R.id.time);
        Button btnSave = findViewById(R.id.btnSaveObs);
        Button btnDelete = findViewById(R.id.btnDeleteObs);

        Button navBack = findViewById(R.id.btnNavBackObs);
        Button navHome = findViewById(R.id.btnNavHomeObs);

        long obsId = getIntent().getLongExtra("obsId", -1);
        long hikeId = getIntent().getLongExtra("hikeId", -1);
        String hikeName = getIntent().getStringExtra("hikeName");

        if (hikeName != null) {
            tvHike.setText(hikeName);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(hikeName + " - Observation");
        }

        if (obsId != -1) {
            obs = obsDao.getById(obsId);
            if (obs != null) {
                desc.setText(obs.description);
                time.setText(obs.time);
            }
        } else {
            obs = new HikeObservation();
            obs.hikeId = hikeId;
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            obs.time = now;
            time.setText(now);
        }

        btnSave.setOnClickListener(v -> {
            obs.description = desc.getText().toString();
            obs.time = time.getText().toString();
            if (obs.id == 0) {
                obs.id = obsDao.insert(obs);
            } else {
                obsDao.update(obs);
            }
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            if (obs != null && obs.id != 0) {
                obsDao.delete(obs.id);
            }
            finish();
        });

        navBack.setOnClickListener(v -> finish());
        navHome.setOnClickListener(v -> {
            Intent i = new Intent(ObservationFormActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

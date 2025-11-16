package com.example.hike;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HikeDetailActivity extends AppCompatActivity {
    private HikeDao hikeDao;
    private ObservationDao obsDao;
    private Hike hike;
    private List<HikeObservation> observations;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_hike_detail);

            // Use the theme's ActionBar for Up/navigation and title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            hikeDao = new HikeDao(this);
            obsDao = new ObservationDao(this);

            TextView tvName = findViewById(R.id.tvName);
            TextView tvLocation = findViewById(R.id.tvLocation);
            TextView tvDate = findViewById(R.id.tvDate);
            TextView tvDifficulty = findViewById(R.id.tvDifficulty);
            TextView tvDescription = findViewById(R.id.tvDescription);

            ListView listObs = findViewById(R.id.listObs);
            Button btnEditHike = findViewById(R.id.btnEditHike);
            Button btnAddObs = findViewById(R.id.btnAddObs);

            Button navBack = findViewById(R.id.btnNavBackDetail);
            Button navHome = findViewById(R.id.btnNavHomeDetail);

            long id = getIntent().getLongExtra("hikeId", -1);
            if (id != -1) {
                hike = hikeDao.getById(id);
            }

            if (hike != null) {
                if (getSupportActionBar() != null) getSupportActionBar().setTitle(hike.name == null ? "Hike" : hike.name);
                if (tvName != null) tvName.setText(safe(hike.name));
                if (tvLocation != null) tvLocation.setText(safe(hike.location));
                if (tvDate != null) tvDate.setText(safe(hike.date));
                if (tvDifficulty != null) tvDifficulty.setText(safe(hike.difficulty));
                if (tvDescription != null) tvDescription.setText(safe(hike.description));
                observations = obsDao.getByHikeId(hike.id);
            } else {
                if (getSupportActionBar() != null) getSupportActionBar().setTitle("Hike");
                if (tvName != null) tvName.setText("");
                if (tvLocation != null) tvLocation.setText("");
                if (tvDate != null) tvDate.setText("");
                if (tvDifficulty != null) tvDifficulty.setText("");
                if (tvDescription != null) tvDescription.setText("No hike selected");
                observations = new ArrayList<>();
            }

            // Ensure adapter always initialized
            List<String> initial = obsTitles(observations == null ? new ArrayList<>() : observations);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, initial);
            if (listObs != null) listObs.setAdapter(adapter);

            if (listObs != null) {
                listObs.setOnItemClickListener((parent, view, position, id2) -> {
                    if (observations == null || position < 0 || position >= observations.size()) return;
                    HikeObservation o = observations.get(position);
                    if (o == null) return;
                    Intent i = new Intent(HikeDetailActivity.this, ObservationFormActivity.class);
                    i.putExtra("obsId", o.id);
                    startActivity(i);
                });
            }

            if (btnEditHike != null) btnEditHike.setOnClickListener(v -> {
                if (hike != null) {
                    Intent i = new Intent(HikeDetailActivity.this, HikeFormActivity.class);
                    i.putExtra("hikeId", hike.id);
                    startActivity(i);
                } else {
                    Toast.makeText(HikeDetailActivity.this, "No hike selected", Toast.LENGTH_SHORT).show();
                }
            });

            if (btnAddObs != null) btnAddObs.setOnClickListener(v -> {
                if (hike != null) {
                    Intent i = new Intent(HikeDetailActivity.this, ObservationFormActivity.class);
                    i.putExtra("hikeId", hike.id);
                    i.putExtra("hikeName", hike.name);
                    startActivity(i);
                } else {
                    Toast.makeText(HikeDetailActivity.this, "No hike selected", Toast.LENGTH_SHORT).show();
                }
            });

            if (navBack != null) navBack.setOnClickListener(v -> finish());
            if (navHome != null) navHome.setOnClickListener(v -> {
                Intent i = new Intent(HikeDetailActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening hike details: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload hike data in case it was edited
        if (hike != null) {
            try {
                hike = hikeDao.getById(hike.id);

                // Update all the text views with the latest data
                TextView tvName = findViewById(R.id.tvName);
                TextView tvLocation = findViewById(R.id.tvLocation);
                TextView tvDate = findViewById(R.id.tvDate);
                TextView tvDifficulty = findViewById(R.id.tvDifficulty);
                TextView tvDescription = findViewById(R.id.tvDescription);

                if (hike != null) {
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle(hike.name == null ? "Hike" : hike.name);
                    if (tvName != null) tvName.setText(safe(hike.name));
                    if (tvLocation != null) tvLocation.setText(safe(hike.location));
                    if (tvDate != null) tvDate.setText(safe(hike.date));
                    if (tvDifficulty != null) tvDifficulty.setText(safe(hike.difficulty));
                    if (tvDescription != null) tvDescription.setText(safe(hike.description));
                }

                observations = obsDao.getByHikeId(hike.id);
            } catch (Exception e) {
                observations = new ArrayList<>();
            }
        } else {
            observations = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, obsTitles(observations));
            ListView listObs = findViewById(R.id.listObs);
            if (listObs != null) listObs.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(obsTitles(observations));
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> obsTitles(List<HikeObservation> list) {
        List<String> t = new ArrayList<>();
        if (list == null) return t;
        for (HikeObservation o : list) {
            t.add(safe(o.time) + " - " + safe(o.description));
        }
        return t;
    }

    private String safe(String s) {
        return s == null ? "" : s;
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

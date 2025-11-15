package com.example.hike;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private HikeDao hikeDao;
    private HikeAdapter adapter;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Hike Manager");

        hikeDao = new HikeDao(this);
        recycler = findViewById(R.id.recyclerHikes);
        EditText search = findViewById(R.id.search);
        Button btnAdd = findViewById(R.id.btnAddHike);
        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);

        adapter = new HikeAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(hike -> {
            Intent i = new Intent(MainActivity.this, HikeDetailActivity.class);
            i.putExtra("hikeId", hike.id);
            startActivity(i);
        });

        // swipe-to-delete with undo
        ItemTouchHelper.SimpleCallback sc = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                Hike removed = adapter.getItem(pos);
                if (removed == null) return;

                // remove from adapter immediately
                adapter.removeItem(pos);

                // show undo snackbar
                Snackbar s = Snackbar.make(recycler, "Hike deleted", Snackbar.LENGTH_LONG);
                s.setAction("Undo", v -> {
                    // restore in adapter
                    adapter.addItem(pos, removed);
                });
                s.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            // snackbar timed out or was swiped away -> finalize deletion in DB
                            try {
                                hikeDao.delete(removed.id);
                            } catch (Exception ex) {
                                Log.e(TAG, "Error deleting hike id=" + removed.id, ex);
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to delete hike: " + ex.getMessage(), Toast.LENGTH_LONG).show());
                            }
                        }
                    }
                });
                s.show();
            }
        };
        new ItemTouchHelper(sc).attachToRecyclerView(recycler);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HikeFormActivity.class)));

        btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete all hikes")
                .setMessage("Are you sure you want to delete ALL hikes? This cannot be undone.")
                .setPositiveButton("Delete All", (dlg, which) -> {
                    try {
                        int rows = hikeDao.deleteAll();
                        adapter.setItems(hikeDao.getAll());
                        Toast.makeText(MainActivity.this, rows + " hikes deleted", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error deleting all hikes", ex);
                        Toast.makeText(MainActivity.this, "Failed to delete all hikes: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString();
                List<Hike> results;
                if (q.isEmpty()) results = hikeDao.getAll();
                else results = hikeDao.search(q);
                adapter.setItems(results);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setItems(hikeDao.getAll());
    }
}

package com.example.hike;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.ViewHolder> {
    private List<Hike> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Hike hike);
    }

    public void setOnItemClickListener(OnItemClickListener l) { listener = l; }

    public void setItems(List<Hike> list) {
        items = list == null ? new ArrayList<>() : new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public Hike getItem(int pos) { return (pos >= 0 && pos < items.size()) ? items.get(pos) : null; }

    public void removeItem(int pos) {
        if (pos >= 0 && pos < items.size()) {
            items.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void addItem(int pos, Hike h) {
        if (pos < 0) pos = 0;
        if (pos > items.size()) pos = items.size();
        items.add(pos, h);
        notifyItemInserted(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hike_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hike h = items.get(position);
        holder.title.setText(h.toString());
        String subtitle = (h.date == null ? "" : h.date) + (h.difficulty == null ? "" : " • " + h.difficulty);
        holder.subtitle.setText(subtitle);
        // bind image if present
        if (h.imageUri != null && !h.imageUri.trim().isEmpty()) {
            try {
                holder.image.setImageURI(Uri.parse(h.imageUri));
            } catch (Exception e) {
                holder.image.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            holder.image.setImageResource(R.mipmap.ic_launcher);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(h);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.tvItemImage);
            title = itemView.findViewById(R.id.tvItemTitle);
            subtitle = itemView.findViewById(R.id.tvItemSubtitle);
        }
    }
}

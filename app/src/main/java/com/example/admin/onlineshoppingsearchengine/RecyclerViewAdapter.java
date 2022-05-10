package com.example.admin.onlineshoppingsearchengine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageViewHolder> {

    private ArrayList<Item> itemList;
    private Context context;

    RecyclerViewAdapter(Context context, ArrayList<Item> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int i) {
        // Called by the layout manager when it wants new data in an existing row

        if ((itemList == null) || (itemList.size() == 0)) {
            String noResults = "No results found";
            holder.title.setText(noResults);
        } else {
            final Item item = itemList.get(i);

            Picasso.with(context).load(item.getImageUrl())
                    .error(R.drawable.ic_all_inclusive_black_24dp)
                    .placeholder(R.drawable.ic_all_inclusive_black_24dp)
                    .into(holder.imageView);
            holder.title.setText(item.getTitle());
            holder.price.setText(item.getPrice());
            holder.website.setText(item.getWebsite());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(item.getItemUrl()));
//                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ((itemList != null) && (itemList.size() != 0) ? itemList.size() : 1);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;
        TextView price;
        TextView website;
        View lineView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.title = itemView.findViewById(R.id.title1);
            this.price = itemView.findViewById(R.id.price);
            this.website = itemView.findViewById(R.id.website);
            this.lineView = itemView.findViewById(R.id.line_id);
        }

    }

}

package org.dbpedia.browser.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dbpedia.browser.DetailActivity;
import org.dbpedia.browser.R;
import org.dbpedia.browser.adapter.viewholder.ReferToPageViewHolder;

import java.util.ArrayList;

/**
 * A recycler adapter which is used if a page refers to multiple other pages.
 *
 * A RecyclerView.Adapter is used, to connect data with the views of a RecyclerView.
 * See also here: https://developer.android.com/training/material/lists-cards.html
 */

public class ReferToPageRecyclerAdapter extends RecyclerView.Adapter<ReferToPageViewHolder> {
    private ArrayList<String> objects;
    private Context context;

    public ReferToPageRecyclerAdapter(ArrayList<String> objects, Context context) {
        this.objects = objects;
        this.context = context;
    }

    @Override
    public ReferToPageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_refer, parent, false);
        return new ReferToPageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReferToPageViewHolder holder, final int position) {
        String label = objects.get(position).substring(objects.get(position).lastIndexOf('/') + 1).replace('_', ' ');
        holder.labelTextView.setText(label);
        holder.uriTextView.setText(objects.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailActivity.class);
                i.setData(Uri.parse(objects.get(position)));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}

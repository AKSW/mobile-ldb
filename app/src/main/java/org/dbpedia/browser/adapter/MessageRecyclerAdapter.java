package org.dbpedia.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dbpedia.browser.R;
import org.dbpedia.browser.adapter.viewholder.MessageViewHolder;

/**
 * A recycler adapter which is used to display a (error) message instead of data.
 *
 * A RecyclerView.Adapter is used, to connect data with the views of a RecyclerView.
 * See also here: https://developer.android.com/training/material/lists-cards.html
 */

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private String message;
    private int iconRes;
    private Context context;

    public MessageRecyclerAdapter(String message, int iconRes, Context context) {
        this.message = message;
        this.iconRes = iconRes;
        this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.messageText.setText(message);
        holder.iconImageView.setImageDrawable(context.getResources().getDrawable(iconRes));
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}

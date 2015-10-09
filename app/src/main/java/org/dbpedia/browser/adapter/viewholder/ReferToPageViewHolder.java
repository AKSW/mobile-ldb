package org.dbpedia.browser.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.dbpedia.browser.R;

//ViewHolder to show a link which refers to another page
public class ReferToPageViewHolder extends RecyclerView.ViewHolder {
    public TextView labelTextView;
    public TextView uriTextView;
    public CardView card;

    public ReferToPageViewHolder(View itemView) {
        super(itemView);
        labelTextView = (TextView) itemView.findViewById(R.id.refer_page_title);
        uriTextView = (TextView) itemView.findViewById(R.id.refer_page_uri);
        card = (CardView) itemView.findViewById(R.id.card);
    }
}

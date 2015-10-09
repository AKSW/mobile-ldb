package org.dbpedia.browser.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.dbpedia.browser.R;

/**
 * ViewHolder for the abstract text property.
 */
public class AbstractViewHolder extends RecyclerView.ViewHolder {
    public TextView abstractTextView;

    public AbstractViewHolder(View itemView) {
        super(itemView);
        abstractTextView = (TextView) itemView.findViewById(R.id.abstractTextView);
    }
}
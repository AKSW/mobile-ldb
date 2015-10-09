package org.dbpedia.browser.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.dbpedia.browser.R;

/**
 * ViewHolder to show a message next to an icon. Used if there is no content or no network connection
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView messageText;
    public ImageView iconImageView;

    public MessageViewHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.message);
        iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    }
}

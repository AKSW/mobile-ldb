package org.dbpedia.browser.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.dbpedia.browser.R;

/**
 * ViewHolder for the date of birth and the date of death property
 */
public class BirthDeathDateViewHolder extends RecyclerView.ViewHolder {
    public TextView birthDateDescriptionText;
    public TextView deathDateDescriptionText;
    public TextView birthDateText;
    public TextView deathDateText;

    public BirthDeathDateViewHolder(View itemView) {
        super(itemView);
        birthDateDescriptionText = (TextView) itemView.findViewById(R.id.birth_date_description_text);
        birthDateText = (TextView) itemView.findViewById(R.id.birth_date_text);
        deathDateDescriptionText = (TextView) itemView.findViewById(R.id.death_date_description_text);
        deathDateText = (TextView) itemView.findViewById(R.id.death_date_text);
    }
}

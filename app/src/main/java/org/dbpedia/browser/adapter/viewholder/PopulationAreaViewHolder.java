package org.dbpedia.browser.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.dbpedia.browser.R;

/**
 * ViewHolder to show the number of inhabitants and the area size of a city, a state or a country
 */
public class PopulationAreaViewHolder extends RecyclerView.ViewHolder {
    public TextView populationDescriptionText;
    public TextView areaDescriptionText;
    public TextView populationText;
    public TextView areaText;

    public PopulationAreaViewHolder(View itemView) {
        super(itemView);
        populationDescriptionText = (TextView) itemView.findViewById(R.id.population_description_text);
        populationText = (TextView) itemView.findViewById(R.id.population_text);
        areaDescriptionText = (TextView) itemView.findViewById(R.id.area_description_text);
        areaText = (TextView) itemView.findViewById(R.id.area_text);
    }
}

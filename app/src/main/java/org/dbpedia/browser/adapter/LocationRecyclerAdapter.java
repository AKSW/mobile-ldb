package org.dbpedia.browser.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import org.dbpedia.browser.R;
import org.dbpedia.browser.adapter.viewholder.AbstractViewHolder;
import org.dbpedia.browser.adapter.viewholder.EmptyViewHolder;
import org.dbpedia.browser.adapter.viewholder.MapViewHolder;
import org.dbpedia.browser.adapter.viewholder.PopulationAreaViewHolder;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A recycler view which is used for locations and shows a map, a card with population and area and the abstract text
 *
 * A RecyclerView.Adapter is used, to connect data with the views of a RecyclerView.
 * See also here: https://developer.android.com/training/material/lists-cards.html
 */
public class LocationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MAP = 0;
    private static final int TYPE_ABSTRACT = 1;
    private static final int TYPE_EMPTY = -1;
    private static final int TYPE_POPULATION_AREA = 2;
    private String abstractText;
    private LatLng location;
    private Context context;
    private float mapZoom;
    private String population;
    private String area;

    public LocationRecyclerAdapter(@Nullable String abstractText, @Nullable String locationCoordinates,
                                   Context context, float mapZoom, @Nullable String population,
                                   @Nullable String area) {

        this.abstractText = abstractText;
        this.context = context;
        this.mapZoom = mapZoom;
        this.population = population;
        this.area = area;
        if (locationCoordinates != null) {
            double latitude = Double.parseDouble(locationCoordinates.substring(0, locationCoordinates.indexOf(' ')));
            double longitude = Double.parseDouble(locationCoordinates.substring(locationCoordinates.indexOf(' ') + 1));
            location = new LatLng(latitude, longitude);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ABSTRACT && abstractText != null) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abstract, parent, false);
            return new AbstractViewHolder(v);
        } else if (viewType == TYPE_MAP && location != null) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maps, parent, false);
            return new MapViewHolder(v, context, location, mapZoom);
        } else if (viewType == TYPE_POPULATION_AREA && (population != null || area != null)) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_population_area, parent, false);
            return new PopulationAreaViewHolder(v);
        }
        return new EmptyViewHolder(new View(context));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            if (holder instanceof AbstractViewHolder) {
                ((AbstractViewHolder) holder).abstractTextView.setText(abstractText);
            } else if (holder instanceof MapViewHolder) {
                ((MapViewHolder) holder).initMap();
                ((MapViewHolder) holder).locationNameTextView.setText(locationAsString(location));
            } else if (holder instanceof PopulationAreaViewHolder) {
                if (population != null) {
                    ((PopulationAreaViewHolder) holder).populationDescriptionText.setVisibility(View.VISIBLE);
                    ((PopulationAreaViewHolder) holder).populationText.setVisibility(View.VISIBLE);
                    try {
                        NumberFormat numberFormat = new DecimalFormat("#,###");
                        population = numberFormat.format(Integer.parseInt(population)) + " " + context.getResources().getString(R.string.inhabitants);
                    } catch (NumberFormatException e) {
                        population = population + context.getResources().getString(R.string.inhabitants);
                    }
                    ((PopulationAreaViewHolder) holder).populationText.setText(population);
                }
                if (area != null) {
                    ((PopulationAreaViewHolder) holder).areaDescriptionText.setVisibility(View.VISIBLE);
                    ((PopulationAreaViewHolder) holder).areaText.setVisibility(View.VISIBLE);
                    try {
                        NumberFormat numberFormat = new DecimalFormat("#,###");
                        area = numberFormat.format(Double.parseDouble(area) / 1000000.0) + " km²";
                    } catch (NumberFormatException e) {
                        area = area + " m²";
                    }
                    ((PopulationAreaViewHolder) holder).areaText.setText(area);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_MAP;
            case 1:
                return TYPE_POPULATION_AREA;
            case 2:
                return TYPE_ABSTRACT;
        }
        return TYPE_EMPTY;
    }

    private String locationAsString(LatLng location) {
        String s = "";
        NumberFormat nf = new DecimalFormat("0.000");
        String latitude = nf.format(location.latitude);
        String longitude = nf.format(location.longitude);
        if (latitude.startsWith("-")) {
            s += latitude.substring(1) + "°S /";
        } else {
            s += latitude + "°N / ";
        }
        if (longitude.startsWith("-")) {
            s += longitude.substring(1) + "°W";
        } else {
            s += longitude + "°E";
        }
        return s;
    }
}

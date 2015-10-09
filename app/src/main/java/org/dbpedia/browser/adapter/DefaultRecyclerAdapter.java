package org.dbpedia.browser.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import org.dbpedia.browser.DetailActivity;
import org.dbpedia.browser.R;
import org.dbpedia.browser.adapter.viewholder.AbstractViewHolder;
import org.dbpedia.browser.adapter.viewholder.EmptyViewHolder;
import org.dbpedia.browser.adapter.viewholder.MapViewHolder;

import java.io.IOException;
import java.util.List;

/**
 * The default recycler adapter which shows an abstract text and a map if available
 *
 * A RecyclerView.Adapter is used, to connect data with the views of a RecyclerView.
 * See also here: https://developer.android.com/training/material/lists-cards.html
 */
public class DefaultRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MAP = 0;
    private static final int TYPE_ABSTRACT = 1;
    private static final int TYPE_EMPTY = -1;

    private Context context;
    private String locationCity;
    private String abstractText;


    public DefaultRecyclerAdapter(Context context, @Nullable String abstractText, @Nullable String locationCity) {
        this.context = context;
        this.abstractText = abstractText;
        this.locationCity = locationCity;
    }

    /**
     *
     * @param parent the parent view
     * @param viewType the type of the view declared in getViewType
     * @return a ViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Create the ViewHolder depending on viewType. Return an EmptyViewHolder if the required data for another ViewHolder
        //are missing.
        if (viewType == TYPE_ABSTRACT && abstractText != null) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abstract, parent, false);
            return new AbstractViewHolder(v);
        } else if (viewType == TYPE_MAP && locationCity != null) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maps, parent, false);
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addresses = geocoder.getFromLocationName(locationCity, 1);
                LatLng location = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                return new MapViewHolder(v, context, location, 7);
            } catch (IOException | IndexOutOfBoundsException e) {
                return new EmptyViewHolder(new View(context));
            }
        }
        return new EmptyViewHolder(new View(context));
    }


    /**
     *This is called to bind the data to the item view
     * @param holder the ViewHolder
     * @param position the position of the item
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            //Check which type of ViewHolder we have
            if (holder instanceof AbstractViewHolder) {
                //Set abstract text if it is a AbstractViewHolder
                ((AbstractViewHolder) holder).abstractTextView.setText(abstractText);
            } else if (holder instanceof MapViewHolder) {
                //Init map and set location text if it is a MapViewHolder
                ((MapViewHolder) holder).initMap();
                ((MapViewHolder) holder).locationNameTextView.setText(locationCity);

            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        //Set the view type of an item. The order of the views can be changed here.
        switch (position) {
            case 0:
                return TYPE_MAP;
            case 1:
                return TYPE_ABSTRACT;
            default:
                return TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MapViewHolder) {
            ((DetailActivity) context).getSupportFragmentManager().beginTransaction().remove(((MapViewHolder) holder).mapFragment).commit();
        }
    }


}

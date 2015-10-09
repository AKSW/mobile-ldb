package org.dbpedia.browser.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dbpedia.browser.DetailActivity;
import org.dbpedia.browser.R;

/**
 * ViewHolder used to show a map
 */
public class MapViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    public SupportMapFragment mapFragment;
    public TextView locationNameTextView;
    private GoogleMap mMap;
    private Context context;
    private LatLng location;
    private float zoom;

    public MapViewHolder(View itemView, Context context, LatLng location, float zoom) {
        super(itemView);
        this.context = context;
        this.location = location;
        this.zoom = zoom;
        locationNameTextView = (TextView) itemView.findViewById(R.id.locationNameTextView);
    }

    public void initMap() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        ((DetailActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment, "MapFragment").commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions().position(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }
}

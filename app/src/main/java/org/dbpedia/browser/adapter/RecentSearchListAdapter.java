package org.dbpedia.browser.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * A ListView adapter to display the recent searches.
 */

public class RecentSearchListAdapter extends ArrayAdapter<String> {

    private List<String> objects;

    public RecentSearchListAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size() <= 5 ? objects.size() : 5;
    }
}

package org.dbpedia.browser.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dbpedia.browser.R;
import org.dbpedia.browser.adapter.viewholder.AbstractViewHolder;
import org.dbpedia.browser.adapter.viewholder.BirthDeathDateViewHolder;
import org.dbpedia.browser.adapter.viewholder.EmptyViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A recycler adapter which is used to show the date of birth, date of death and abstract text of a person.
 *
 * A RecyclerView.Adapter is used, to connect data with the views of a RecyclerView.
 * See also here: https://developer.android.com/training/material/lists-cards.html
 */
public class PersonRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BIRTH_DEATH_DATE = 0;
    private static final int TYPE_ABSTRACT = 1;
    private static final int TYPE_EMPTY = -1;

    private Context context;
    private String abstractText;
    private String birthDate;
    private String deathDate;


    public PersonRecyclerAdapter(Context context, @Nullable String abstractText, @Nullable String birthDate, @Nullable String deathDate) {
        this.context = context;
        this.abstractText = abstractText;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ABSTRACT && abstractText != null) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abstract, parent, false);
            return new AbstractViewHolder(v);
        } else if (viewType == TYPE_BIRTH_DEATH_DATE && (birthDate != null || deathDate != null)) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_birth_death_date, parent, false);
            return new BirthDeathDateViewHolder(v);
        }
        return new EmptyViewHolder(new View(context));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            if (holder instanceof AbstractViewHolder) {
                ((AbstractViewHolder) holder).abstractTextView.setText(abstractText);
            } else if (holder instanceof BirthDeathDateViewHolder) {
                if (birthDate != null) {
                    ((BirthDeathDateViewHolder) holder).birthDateDescriptionText.setVisibility(View.VISIBLE);
                    ((BirthDeathDateViewHolder) holder).birthDateText.setVisibility(View.VISIBLE);
                    //Transform the given date format yyyy-MM-dd in a local date format
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat newDateFormat = SimpleDateFormat.getDateInstance();
                    try {
                        Date date = dateFormat.parse(birthDate);
                        ((BirthDeathDateViewHolder) holder).birthDateText.setText(newDateFormat.format(date));
                    } catch (ParseException e) {
                        ((BirthDeathDateViewHolder) holder).birthDateText.setText(deathDate);
                    }
                }
                if (deathDate != null) {
                    ((BirthDeathDateViewHolder) holder).deathDateDescriptionText.setVisibility(View.VISIBLE);
                    ((BirthDeathDateViewHolder) holder).deathDateText.setVisibility(View.VISIBLE);
                    //Transform the given date format yyyy-MM-dd in a local date format
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat newDateFormat = SimpleDateFormat.getDateInstance();
                    try {
                        Date date = dateFormat.parse(deathDate);
                        ((BirthDeathDateViewHolder) holder).deathDateText.setText(newDateFormat.format(date));
                    } catch (ParseException e) {
                        ((BirthDeathDateViewHolder) holder).deathDateText.setText(deathDate);
                    }
                }
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_BIRTH_DEATH_DATE;
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


}

package com.example.theguardiannews;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

/*
 * {@link NewsAdapter} is an {@link ArrayAdapter} that can provide the layout for each list
 * based on a data source, which is a list of {@link News} objects.
 * */
public class NewsAdapter extends ArrayAdapter<News> {
    private Context context;

    public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        // creating convertView if it doesn't already exist and setting up viewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        News objectToDisplay = getItem(position);
        // adding details to list view item
        assert objectToDisplay != null;
        if (!objectToDisplay.getAuthor().equals("not known")) {
            viewHolder.authorText.setText(objectToDisplay.getAuthor());
        }
        viewHolder.titleText.setText(objectToDisplay.getTitle());
        viewHolder.dateText.setText(objectToDisplay.getDate());
        viewHolder.sectionText.setText(objectToDisplay.getSection());

        // setting colours
        int colour = getCategoryColour(objectToDisplay.getSection(), viewHolder);
        viewHolder.sectionText.setTextColor(colour);
        viewHolder.authorText.setTextColor(colour);
        return convertView;
    }

    // setting up viewHolder class
    private class ViewHolder {
        final TextView titleText;
        final TextView authorText;
        final TextView sectionText;
        final TextView dateText;

        ViewHolder(View view) {
            this.titleText = view.findViewById(R.id.title_text);
            this.authorText = view.findViewById(R.id.author_text);
            this.dateText = view.findViewById(R.id.date_text);
            this.sectionText = view.findViewById(R.id.section_text);
        }
    }

    // method to get the right colour for each section category and to truncate unknown and long section names
    @SuppressLint("SetTextI18n")
    private int getCategoryColour(String category, ViewHolder viewHolder) {
        int categoryColourId;
        if (context.getString(R.string.news_colour_categories).contains(category)) {
            categoryColourId = R.color.news;
        }  else if (context.getString(R.string.football_colour_categories).contains(category)) {
            categoryColourId = R.color.sports;
        } else if (context.getString(R.string.technology_colour_categories).contains(category)) {
            categoryColourId = R.color.culture;
        }  else {
            categoryColourId = R.color.unclassified;
            int index = category.indexOf(" ", category.indexOf(" ") + 1);

            if (index != -1) {
                category = category.substring(0, index);
                viewHolder.sectionText.setText(category);
            }
        }
        return ContextCompat.getColor(getContext(), categoryColourId);
    }

}

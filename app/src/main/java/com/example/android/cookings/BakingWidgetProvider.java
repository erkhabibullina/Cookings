package com.example.android.cookings;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Set;

import androidx.collection.ArraySet;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        // Get clicked recipe data
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String recipe = sharedPreferences.getString(context.getString(R.string.recipe_key),
                context.getString(R.string.widget_nodata_header));
        Set<String> ingredientSet = sharedPreferences.getStringSet(
                context.getString(R.string.ingredients_key), new ArraySet<String>());

        StringBuilder sb = new StringBuilder();
        for(String i: ingredientSet) {
            sb.append(i).append("\n");
        }

        views.setTextViewText(R.id.appwidget_headline, recipe);
        views.setTextViewText(R.id.appwidget_text,
                sb.toString().isEmpty() ?
                        context.getString(R.string.widget_nodata_filler) : sb.toString());

        // Intent to launch app when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.appwidget_headline, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}


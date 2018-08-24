package com.ramona.petcare.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.text.Html;
import android.widget.RemoteViews;

import com.ramona.petcare.R;
import com.ramona.petcare.model.Pet;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class PetsWidgetProvider extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<Pet> pets) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pets_widget_provider);
        views.setTextViewText(R.id.text_widget_title, context.getString(R.string.my_pets));
        if (pets != null && pets.size() > 0) {

            StringBuilder ingredients = new StringBuilder("<html>");
            ingredients.append("<ol>");
            for (Pet ingredient : pets) {
                ingredients.append("<li>")
                        .append(ingredient.getPetName())
                        .append("</li>");
            }
            ingredients.append("</ol>");
            ingredients.append("</html>");
            views.setTextViewText(R.id.text_widget_pets, Html.fromHtml(ingredients.toString()));
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    public static void updatePetCareAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                              int[] appWidgetIds, List<Pet> pets) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, pets);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PetsWidgetService.startActionUpdatePetsWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


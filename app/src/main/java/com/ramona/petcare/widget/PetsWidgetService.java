package com.ramona.petcare.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.ramona.petcare.database.AppDatabase;
import com.ramona.petcare.model.Pet;

import java.util.List;

public class PetsWidgetService extends IntentService {

    private static final String ACTION_UPATE_PETS_WIDGET = "com.ramona.petcare.widget.action.UPATE_RECIPE_WIDGET";

    public PetsWidgetService() {
        super("PetsWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPATE_PETS_WIDGET.equals(action)) {
                AppDatabase db = AppDatabase.getInstance(this.getApplication());
                List<Pet> pets = db.petDao().loadPetsAsArray();
                if(pets != null) {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PetsWidgetProvider.class));
                    PetsWidgetProvider.updatePetCareAppWidgets(this, appWidgetManager, appWidgetIds, pets);
                }
            }
        }
    }

    public static void startActionUpdatePetsWidget(Context context) {
        Intent intent = new Intent(context, PetsWidgetService.class);
        intent.setAction(ACTION_UPATE_PETS_WIDGET);
        context.startService(intent);
    }
}

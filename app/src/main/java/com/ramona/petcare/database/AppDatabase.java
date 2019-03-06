package com.ramona.petcare.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.ramona.petcare.model.Pet;

@Database(entities = {Pet.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    public abstract PetDao petDao();

    /**
     * Gets the singleton instance of AppDatabase.
     *
     * @param context The context.
     * @return The singleton instance of AppDatabase.
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "pet_care_database")
                    .build();
        }
        return sInstance;
    }

    public static void destroyInstance() {
        sInstance = null;
    }

}

package com.ramona.petcare.model;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.ramona.petcare.database.AppDatabase;

import java.util.List;

public class PetsViewModel extends AndroidViewModel {

    public LiveData<List<Pet>> petsLiveData;
    public LiveData<Pet> petLiveData;

    private AppDatabase mDb;

    public PetsViewModel(@NonNull Application application) {
        super(application);
        createDb();
    }

    public void setupPetsLiveData(){
        petsLiveData = mDb.petDao().loadAllPets();
    }

    public void setupPetLiveData(int uid){
        petLiveData = mDb.petDao().loadPet(uid);
    }

    private void createDb() {
        mDb = AppDatabase.getInstance(this.getApplication());
    }
}

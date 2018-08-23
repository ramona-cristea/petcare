package com.ramona.petcare.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ramona.petcare.model.Pet;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PetDao {

    @Query("SELECT * FROM Pet")
    LiveData<List<Pet>> loadAllPets();


    @Query("SELECT * FROM Pet")
    List<Pet> loadPetsAsArray();

    @Query("SELECT * FROM Pet WHERE uid = :uid")
    LiveData<Pet> loadPet(int uid);

    @Insert(onConflict = REPLACE)
    void insertPet(Pet pet);

    @Update
    void updatePet(Pet pet);

    @Query("DELETE FROM Pet WHERE uid = :uid")
    void delete(int uid);

}

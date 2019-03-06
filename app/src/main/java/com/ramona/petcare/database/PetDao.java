package com.ramona.petcare.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ramona.petcare.model.Pet;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

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

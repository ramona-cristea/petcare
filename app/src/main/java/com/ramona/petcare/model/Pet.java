package com.ramona.petcare.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Pet implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "pet_name")
    private String petName;

    @ColumnInfo(name = "pet_picture_path")
    private String petPicturePath;

    @ColumnInfo(name = "pet_breed")
    private String petBreed;

    @ColumnInfo(name = "pet_age")
    private String petAge;

    @ColumnInfo(name = "pet_temper")
    private String petTemper;

    @ColumnInfo(name = "pet_gender")
    private int petGender;

    @ColumnInfo(name = "neutered_status")
    private boolean isNeutered;

    @ColumnInfo(name = "adoption_status")
    private boolean isAdopted;

    @ColumnInfo(name = "pet_weight")
    private String petWeight;

    @ColumnInfo(name = "pet_height")
    private String petHeight;

    @ColumnInfo(name = "health_notes")
    private String additionalHealthNotes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeString(this.petName);
        dest.writeString(this.petPicturePath);
        dest.writeString(this.petBreed);
        dest.writeString(this.petAge);
        dest.writeString(this.petTemper);
        dest.writeInt(this.petGender);
        dest.writeByte(this.isNeutered ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAdopted ? (byte) 1 : (byte) 0);
        dest.writeString(this.petWeight);
        dest.writeString(this.petHeight);
        dest.writeString(this.additionalHealthNotes);
    }

    public Pet() {
    }

    protected Pet(Parcel in) {
        this.uid = in.readInt();
        this.petName = in.readString();
        this.petPicturePath = in.readString();
        this.petBreed = in.readString();
        this.petAge = in.readString();
        this.petTemper = in.readString();
        this.petGender = in.readInt();
        this.isNeutered = in.readByte() != 0;
        this.isAdopted = in.readByte() != 0;
        this.petWeight = in.readString();
        this.petHeight = in.readString();
        this.additionalHealthNotes = in.readString();
    }

    public static final Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel source) {
            return new Pet(source);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetPicturePath() {
        return petPicturePath;
    }

    public void setPetPicturePath(String petPicturePath) {
        this.petPicturePath = petPicturePath;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }

    public String getPetTemper() {
        return petTemper;
    }

    public void setPetTemper(String petTemper) {
        this.petTemper = petTemper;
    }

    public int getPetGender() {
        return petGender;
    }

    public void setPetGender(int petGender) {
        this.petGender = petGender;
    }

    public boolean isNeutered() {
        return isNeutered;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

    public boolean isAdopted() {
        return isAdopted;
    }

    public void setAdopted(boolean adopted) {
        isAdopted = adopted;
    }

    public String getPetWeight() {
        return petWeight;
    }

    public void setPetWeight(String petWeight) {
        this.petWeight = petWeight;
    }

    public String getPetHeight() {
        return petHeight;
    }

    public void setPetHeight(String petHeight) {
        this.petHeight = petHeight;
    }

    public String getAdditionalHealthNotes() {
        return additionalHealthNotes;
    }

    public void setAdditionalHealthNotes(String additionalHealthNotes) {
        this.additionalHealthNotes = additionalHealthNotes;
    }
}

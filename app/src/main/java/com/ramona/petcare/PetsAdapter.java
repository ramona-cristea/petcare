package com.ramona.petcare;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramona.petcare.model.Pet;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetViewHolder> {
    private List<Pet> mPets;
    private final OnPetClickListener mPetClickListener;

    PetsAdapter(List<Pet> pets, OnPetClickListener listener) {
        mPets = pets;
        mPetClickListener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.pet_item_layout, parent, false);

        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        final Pet activity = mPets.get(position);
        holder.bind(activity);
    }

    @Override
    public int getItemCount() {
        return mPets == null ? 0 : mPets.size();
    }

    public void setData(List<Pet> activities) {
        mPets = activities;
        notifyDataSetChanged();
    }

    public interface OnPetClickListener {
        void onClick(Pet pet);
    }

    public class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textPetName;
        TextView textPetInfo;
        CircleImageView imagePet;
        RelativeLayout itemLayout;

        private Pet mPet;

        PetViewHolder(View itemView) {
            super(itemView);

            textPetName = itemView.findViewById(R.id.pet_name_text);
            textPetInfo = itemView.findViewById(R.id.pet_name_info);
            imagePet = itemView.findViewById(R.id.pet_image);
            itemLayout = itemView.findViewById(R.id.pet_item_layout);
            itemLayout.setOnClickListener(this);
        }

        void bind(@NonNull Pet pet) {
            mPet = pet;

            itemView.setTag(mPet.getUid());
            textPetName.setText(mPet.getPetName());
            String petInfo;

            if(mPet.getPetAge().matches("^[0-9]*$")){
                petInfo = mPet.getPetAge() + " " + textPetName.getResources().getString(R.string.years);
            }
            else {
                petInfo = mPet.getPetAge();
            }

            petInfo = petInfo + " " + mPet.getPetBreed();

            textPetInfo.setText(petInfo);

            if(!TextUtils.isEmpty(mPet.getPetPicturePath())) {
                File file = new File(mPet.getPetPicturePath());
                Picasso.get().load(file).into(imagePet);
            }
        }

        @Override
        public void onClick(View view) {
            mPetClickListener.onClick(mPet);
        }
    }
}

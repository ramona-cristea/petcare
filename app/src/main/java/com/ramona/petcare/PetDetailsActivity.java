package com.ramona.petcare;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramona.petcare.model.Gender;
import com.ramona.petcare.model.Pet;
import com.ramona.petcare.model.PetsViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PetDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    boolean appBarExpanded = false;
    Pet mPet;

    TextView mTextViewBreed;
    TextView mTextViewAge;
    TextView mTextViewTemper;
    TextView mTextViewGender;
    TextView mTextViewStatus;
    TextView mTextViewWeight;
    TextView mTextViewHeight;
    TextView mTextViewHealthNotes;
    TextView mTextViewAdoptionStatus;
    AppCompatButton mButtonSearchVet;
    ImageView mImageViewPetPicture;
    CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        int petUid = getIntent().getIntExtra("pet_uid", 0);
        PetsViewModel viewModel = ViewModelProviders.of(this).get(PetsViewModel.class);
        viewModel.setupPetLiveData(petUid);

        Toolbar toolbar = findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mImageViewPetPicture = findViewById(R.id.header);
        mTextViewBreed = findViewById(R.id.breed_value);
        mTextViewAge = findViewById(R.id.age_value);
        mTextViewTemper = findViewById(R.id.temper_value);
        mTextViewGender = findViewById(R.id.gender_value);
        mTextViewStatus = findViewById(R.id.status_value);
        mTextViewWeight = findViewById(R.id.weight_value);
        mTextViewHeight = findViewById(R.id.height_value);
        mTextViewHealthNotes = findViewById(R.id.health_notes_value);
        mButtonSearchVet = findViewById(R.id.button_search_vet);
        mTextViewAdoptionStatus = findViewById(R.id.text_adoption_status);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (Math.abs(verticalOffset) > 500) {
                appBarExpanded = false;
                invalidateOptionsMenu();
            } else {
                appBarExpanded = true;
                invalidateOptionsMenu();
            }
        });

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fabEditButton = findViewById(R.id.fab_edit_button);
        fabEditButton.setOnClickListener(this);
        mButtonSearchVet.setOnClickListener(this);

        viewModel.petLiveData.observe(this, petDetails -> showPetDetails(petDetails));
    }

    private void showPetDetails(Pet petDetails) {
        mPet = petDetails;
        if(mPet == null) {
            return;
        }
        if(!TextUtils.isEmpty(mPet.getPetPicturePath())) {
            File file = new File(mPet.getPetPicturePath());
            Picasso.get().load(file).into(mImageViewPetPicture);
        }

        mCollapsingToolbar.setTitle(mPet.getPetName());
        mTextViewBreed.setText(mPet.getPetBreed());

        if(mPet.getPetAge().matches("^[0-9]*$")){
            mTextViewAge.setText(mTextViewAge.getResources().getString(R.string.pet_age_info, mPet.getPetAge()));
        }
        else {
            mTextViewAge.setText(mPet.getPetAge());
        }

        if(!TextUtils.isEmpty(mPet.getPetTemper())) {
            mTextViewTemper.setText(mPet.getPetTemper());
        }

        mTextViewStatus.setText(mTextViewGender.getResources().getString(mPet.isNeutered() ?
                R.string.pet_is_neutered : R.string.pet_is_not_neutered));

        mTextViewGender.setText(mTextViewGender.getResources().getString(mPet.getPetGender() == Gender.MALE ?
                R.string.pet_is_male : R.string.pet_is_female));

        if(!TextUtils.isEmpty(mPet.getPetWeight())) {
            mTextViewWeight.setText(mPet.getPetWeight());
        }

        if(!TextUtils.isEmpty(mPet.getPetHeight())) {
            mTextViewHeight.setText(mPet.getPetHeight());
        }

        if(!TextUtils.isEmpty(mPet.getAdditionalHealthNotes())) {
            mTextViewHealthNotes.setText(mPet.getAdditionalHealthNotes());
        }
        mTextViewAdoptionStatus.setVisibility(mPet.isAdopted() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_edit:
                launchEditScreenForPet();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemDelete = menu.findItem(R.id.action_edit);
        itemDelete.setVisible(!appBarExpanded);

        return true;
    }

    private void launchEditScreenForPet() {
        Intent intentEditPet = new Intent(this, AddPetActivity.class);
        intentEditPet.putExtra("pet_details", mPet);
        startActivity(intentEditPet, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.fab_edit_button) {
            launchEditScreenForPet();

        } else if (id == R.id.button_search_vet) {
            Intent searchVetIntent = new Intent(this, VetsActivity.class);
            startActivity(searchVetIntent);
        }
    }
}

package com.ramona.petcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ramona.petcare.database.AppDatabase;
import com.ramona.petcare.model.Gender;
import com.ramona.petcare.model.Pet;
import com.ramona.petcare.widget.PetsWidgetService;
import com.squareup.picasso.Picasso;
import com.sw926.imagefileselector.ErrorResult;
import com.sw926.imagefileselector.ImageFileSelector;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPetActivity extends AppCompatActivity implements View.OnClickListener{

    private RadioGroup mPetGenderRadioGroup;
    private EditText mPetNameEdit;
    private ImageButton mAddPetImageButton;
    private EditText mPetBreedEdit;
    private EditText mPetAgeEdit;
    private EditText mPetTemperEdit;
    private CheckBox mIsNeuteredCheckbox;
    private CheckBox mIsAdoptedCheckbox;
    private EditText mPetWeightEdit;
    private EditText mPetHeightEdit;
    private EditText mPetHealthNotesEdit;
    private CircleImageView mPetImageView;

    private ImageFileSelector mImageFileSelector;

    int mPetGender = Gender.MALE;
    Pet mPet;

    private static final int REQUEST_PERMISSION_STORAGE = 101;
    private static final int REQUEST_PERMISSION_CAMERA = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        setupWindowAnimations();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if(getIntent() != null && getIntent().hasExtra("pet_details")) {
            mPet = getIntent().getParcelableExtra("pet_details");
        } else {
            mPet = new Pet();
        }

        mPetNameEdit = findViewById(R.id.edit_pet_name);
        mPetBreedEdit = findViewById(R.id.edit_pet_breed);
        mAddPetImageButton = findViewById(R.id.image_button_add_pet_picture);
        mPetAgeEdit = findViewById(R.id.edit_pet_age);
        mPetTemperEdit = findViewById(R.id.edit_pet_temper);
        mIsNeuteredCheckbox = findViewById(R.id.checkbox_neutered);
        mIsAdoptedCheckbox = findViewById(R.id.checkbox_adopted);
        mPetWeightEdit = findViewById(R.id.edit_pet_weight);
        mPetHeightEdit = findViewById(R.id.edit_pet_height);
        mPetHealthNotesEdit = findViewById(R.id.edit_pet_health_notes);
        mPetImageView = findViewById(R.id.pet_image);
        mAddPetImageButton.setOnClickListener(this);
        mPetImageView.setOnClickListener(this);

        mPetGenderRadioGroup = findViewById(R.id.radio_group_gender);
        mPetGenderRadioGroup.setOnCheckedChangeListener(
                (radioGroup, id) -> {
                    switch (id) {
                        case R.id.radio_male:
                            mPetGender = Gender.MALE;
                            break;
                        case R.id.radio_female:
                            mPetGender = Gender.FEMALE;
                            break;
                    }
                });

        initializeImageSelector();
        if(mPet.getUid() > 0) {
            displayPetDetails();
            getSupportActionBar().setTitle(R.string.edit_pet);
        }
    }

    private void setupWindowAnimations() {
        Fade fadeInTransition = new Fade(Fade.IN);
        fadeInTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        getWindow().setEnterTransition(fadeInTransition);

        Fade fadeOutTransition = new Fade(Fade.OUT);
        fadeOutTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        getWindow().setExitTransition(fadeOutTransition);
    }

    private void displayPetDetails() {
        mPetNameEdit.setText(mPet.getPetName());
        mPetBreedEdit.setText(mPet.getPetBreed());
        mPetAgeEdit.setText(mPet.getPetAge());
        mPetTemperEdit.setText(mPet.getPetTemper());
        mIsNeuteredCheckbox.setChecked(mPet.isNeutered());
        mIsAdoptedCheckbox.setChecked(mPet.isAdopted());
        mPetWeightEdit.setText(mPet.getPetWeight());
        mPetHeightEdit.setText(mPet.getPetHeight());
        mPetHealthNotesEdit.setText(mPet.getAdditionalHealthNotes());
        if(mPet.getPetGender() == Gender.MALE) {
            mPetGenderRadioGroup.check(R.id.radio_male);
        } else {
            mPetGenderRadioGroup.check(R.id.radio_female);
        }
        loadImage();

    }

    private void initializeImageSelector() {
        mImageFileSelector = new ImageFileSelector(this);
        mImageFileSelector.setOutPutPath(Environment.getExternalStorageDirectory().getPath());
        mImageFileSelector.setCallback(new ImageFileSelector.Callback() {
            @Override
            public void onError(ErrorResult errorResult) {
                switch (errorResult) {
                    case permissionDenied:
                        Toast.makeText(AddPetActivity.this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                        break;
                    case canceled:
                        Toast.makeText(AddPetActivity.this, getString(R.string.canceled), Toast.LENGTH_LONG).show();
                        break;
                    case error:
                        Toast.makeText(AddPetActivity.this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onSuccess(String filePath) {
                mPet.setPetPicturePath(filePath);
                loadImage();
            }
        });
    }

    private void loadImage(){
        if(!TextUtils.isEmpty(mPet.getPetPicturePath())) {
            File file = new File(mPet.getPetPicturePath());
            Picasso.get().load(file).into(mPetImageView);
            mPetImageView.setVisibility(View.VISIBLE);
            mAddPetImageButton.setVisibility(View.INVISIBLE);
        } else {
            mPetImageView.setVisibility(View.INVISIBLE);
            mAddPetImageButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageFileSelector.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("pet_picture_path", mPet.getPetPicturePath());
        super.onSaveInstanceState(outState);
        mImageFileSelector.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPet.setPetPicturePath(savedInstanceState.getString("pet_picture_path"));
        mImageFileSelector.onRestoreInstanceState(savedInstanceState);
        loadImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPhotoFromGallery();
                } else {
                    Toast.makeText(this, getString(R.string.storage_permission_expanation), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case REQUEST_PERMISSION_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, getString(R.string.camera_permission_expanation), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        mImageFileSelector.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_save:
                validatePetInfoAndSave();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validatePetInfoAndSave() {

        if(TextUtils.isEmpty(mPetNameEdit.getText().toString()) ||
                TextUtils.isEmpty(mPetBreedEdit.getText().toString()) ||
                TextUtils.isEmpty(mPetAgeEdit.getText().toString())) {

            Toast.makeText(this, getString(R.string.error_mandatory_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(mPet.getPetPicturePath())) {
            Toast.makeText(this, getString(R.string.error_picture_missing), Toast.LENGTH_SHORT).show();
            return;
        }

        mPet.setPetName(mPetNameEdit.getText().toString());
        mPet.setPetBreed(mPetBreedEdit.getText().toString());
        mPet.setPetAge(mPetAgeEdit.getText().toString());
        mPet.setPetTemper(mPetTemperEdit.getText().toString());
        mPet.setPetGender(mPetGender);
        mPet.setNeutered(mIsNeuteredCheckbox.isChecked());
        mPet.setAdopted(mIsAdoptedCheckbox.isChecked());
        mPet.setPetWeight(mPetWeightEdit.getText().toString());
        mPet.setPetHeight(mPetHeightEdit.getText().toString());
        mPet.setAdditionalHealthNotes(mPetHealthNotesEdit.getText().toString());

        new SavePetAsyncTask(this).execute(mPet);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.image_button_add_pet_picture || id == R.id.pet_image) {
            selectImage();
        }
    }

    private static class SavePetAsyncTask extends AsyncTask<Pet, Void, Void> {
        private final WeakReference<AddPetActivity> mActivityRef;
        SavePetAsyncTask(AddPetActivity activity){
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Pet... pet) {

            AppDatabase db = AppDatabase.getInstance(mActivityRef.get());
            if(pet[0].getUid() > 0){
                db.petDao().updatePet(pet[0]);
            }
            else {
                db.petDao().insertPet(pet[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mActivityRef.get() != null){
                PetsWidgetService.startActionUpdatePetsWidget(mActivityRef.get());
                mActivityRef.get().finish();
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPetActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                takePhoto();
            } else if (items[item].equals("Choose from Gallery")) {
                selectPhotoFromGallery();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void takePhoto() {
        final List<String> permissionsList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_PERMISSION_CAMERA);

        } else {
            mImageFileSelector.takePhoto(AddPetActivity.this, 1);
        }
    }

    private void selectPhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE);

        } else {
            mImageFileSelector.selectImage(AddPetActivity.this, 2);
        }
    }
}

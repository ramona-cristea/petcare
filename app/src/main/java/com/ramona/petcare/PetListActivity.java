package com.ramona.petcare;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.transition.Slide;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;

import com.ramona.petcare.database.AppDatabase;
import com.ramona.petcare.model.Pet;
import com.ramona.petcare.model.PetsViewModel;
import com.ramona.petcare.widget.PetsWidgetService;

import java.lang.ref.WeakReference;
import java.util.List;

public class PetListActivity extends AppCompatActivity implements PetsAdapter.OnPetClickListener{

    private PetsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Parcelable mSavedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);
        setupWindowAnimations();

        PetsViewModel viewModel = ViewModelProviders.of(this).get(PetsViewModel.class);
        viewModel.setupPetsLiveData();

        FloatingActionButton fabAddPet = findViewById(R.id.fab_add_pet);
        fabAddPet.setOnClickListener(view -> {
            Intent addPetIntent = new Intent(PetListActivity.this, AddPetActivity.class);
            startActivity(addPetIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_pets);
        mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PetsAdapter(null, this);
        recyclerView.setAdapter(mAdapter);

        // Create an item touch helper to handle swiping items off the list
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                int id = (int) viewHolder.itemView.getTag();
                //remove from DB
                new DeletePetAsyncTask(PetListActivity.this).execute(id);
            }
        }).attachToRecyclerView(recyclerView);


        viewModel.petsLiveData.observe(this, petsList -> showPetsInUi(petsList));
    }

    private void setupWindowAnimations() {
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.START);
        slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        getWindow().setExitTransition(slideTransition);
        getWindow().setReenterTransition(slideTransition);
    }

    private void showPetsInUi(List<Pet> pets) {
        mAdapter.setData(pets);
        mAdapter.notifyDataSetChanged();
        if(mSavedRecyclerLayoutState != null){
            mLayoutManager.onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    @Override
    public void onClick(Pet pet) {
        Intent petDetailsIntent = new Intent(this, PetDetailsActivity.class);
        petDetailsIntent.putExtra("pet_uid", pet.getUid());
        startActivity(petDetailsIntent);
    }

    private static class DeletePetAsyncTask extends AsyncTask<Integer, Void, Void> {
        private final WeakReference<PetListActivity> mActivityRef;
        DeletePetAsyncTask(PetListActivity activity){
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Integer... ids) {

            int id = ids[0];
            AppDatabase db = AppDatabase.getInstance(mActivityRef.get());
            if(id != -1) {
                db.petDao().delete(ids[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mActivityRef.get() != null){
                PetsWidgetService.startActionUpdatePetsWidget(mActivityRef.get());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT,
                mLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
    }
}

/*
 * Copyright 2018 (c) Andy Li, Colin Choi, James Sun, Jeremy Ng, Micheal Nguyen, Wyatt Praharenka
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.cmput301w18t05.taskzilla.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301w18t05.taskzilla.CustomOnItemClick;
import com.cmput301w18t05.taskzilla.Photo;
import com.cmput301w18t05.taskzilla.RecyclerViewAdapter;
import com.cmput301w18t05.taskzilla.User;
import com.cmput301w18t05.taskzilla.controller.NewTaskController;
import com.cmput301w18t05.taskzilla.R;
import com.cmput301w18t05.taskzilla.currentUser;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Activity for creating a new task
 */
public class NewTaskActivity extends AppCompatActivity implements OnMapReadyCallback {

    private NewTaskController newTaskController;
    private User cUser = currentUser.getInstance();
    private LatLng taskLocation;
    private LocationManager locationManager;


    private ImageButton currentLocationButton;
    private PlaceAutocompleteFragment autocompleteFragment;
    private double lon;
    private double lat;

    private GoogleMap mMap;
    private ImageButton addPhotoButton;
    private ArrayList<Photo> photos;
    private RecyclerView recyclerPhotosView;
    private RecyclerView.Adapter recyclerPhotosViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout linearLayout;
    private Integer PICK_IMAGE = 5;
    private int maxSize;


    /**
     * Activity uses the activity_new_task.xml layout
     * New tasks are created through NewTaskController
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Add a Task");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dragdropMap);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        newTaskController =  new NewTaskController(this, getApplicationContext());
        currentLocationButton = findViewById(R.id.currentLocationButton);
        Button cancel =  findViewById(R.id.CancelButton);
        Button addTask = findViewById(R.id.addTaskButton);
        final EditText taskName = findViewById(R.id.TaskName);
        final EditText taskDescription = findViewById(R.id.Description);
        addPhotoButton = findViewById(R.id.AddPhotoButton);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("yolo", "Place: " + place.getName());
                autocompleteFragment.setHint(place.getName());
                taskLocation=place.getLatLng();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                taskLocation = null;
                Log.i("err", "An error occurred: " + status);
            }
        });

        photos = new ArrayList<Photo>();
        linearLayout = (LinearLayout) findViewById(R.id.Photos);
        recyclerPhotosView = (RecyclerView) findViewById(R.id.listOfPhotos);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerPhotosView.setLayoutManager(layoutManager);
        recyclerPhotosViewAdapter = new RecyclerViewAdapter(this, photos, new CustomOnItemClick() {
            @Override
            public void onColumnClicked(final int position) {
                // taken from https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
                // 2018-03-16
                AlertDialog.Builder alert = new AlertDialog.Builder(NewTaskActivity.this);
                alert.setTitle("Delete Photo");
                alert.setMessage("Are you sure you want to delete this photo?");

                //DELETE CODE
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photos.remove(position);
                        dialogInterface.dismiss();
                        recyclerPhotosViewAdapter.notifyDataSetChanged();
                    }
                });

                //DELETE CANCEL CODE
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();

            }
        });
        recyclerPhotosView.setAdapter(recyclerPhotosViewAdapter);


        autocompleteFragment.setHint("Task Location");
        getLocation();
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(lat-0.25, lon-0.25),
                new LatLng(lat+0.25, lon+0.25)));
        /* cancel button */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTaskController.cancelTask();
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentLocation();
            }
        });
        /* add task button */
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTaskController.addTask(taskName.getText().toString(), cUser, taskDescription.getText().toString(),taskLocation,photos);
            }
        });
        addPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AddPhotoButtonClicked();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("drag","true");
                startActivity(intent);
            }
        });


    }

    public void AddPhotoButtonClicked(){
        if(photos.size()==10){
            Toast.makeText(NewTaskActivity.this,"Photo limited reached",Toast.LENGTH_LONG).show();
        }
        else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, PICK_IMAGE);
        }
    }
    void getLocation() {
        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null){
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        }
    }

    public void setCurrentLocation(){
        getLocation();
        taskLocation = new LatLng(lat,lon);
        autocompleteFragment.setHint("Current Location");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                getLocation();
                break;
        }
    }



    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        // taken from https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview
        // 2018-04-03
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                maxSize = 65536;
                Log.i("ACTUAL SIZE", String.valueOf(selectedImage.getByteCount()));
                Integer width = 1200;
                Integer height = 1200;
                Bitmap resizedImage;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,50,stream);
                Log.i("size",String.valueOf(stream.size()));
                while(stream.size()>maxSize){
                    width = width - 200;
                    height = height - 200;
                    stream = new ByteArrayOutputStream();
                    resizedImage = Bitmap.createScaledBitmap(selectedImage, width, height, false);
                    resizedImage.compress(Bitmap.CompressFormat.JPEG,50,stream);
                    Log.i("size",String.valueOf(stream.size()));
                }

                byte byteImage[];
                byteImage = stream.toByteArray();
                String image = Base64.encodeToString(byteImage, Base64.DEFAULT);
                photos.add(new Photo(image));
                Log.i("hi",String.valueOf(photos.size()));
                recyclerPhotosViewAdapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NewTaskActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(NewTaskActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }

}


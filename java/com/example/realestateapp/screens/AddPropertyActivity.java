package com.example.realestateapp.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.realestateapp.R;
import com.example.realestateapp.utils.PermissionHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_CAMERA = 2;

    private EditText locationEditText, descriptionEditText, shortDescriptionEditText,
            ownerNameEditText, contactNoEditText, priceEditText, bedroomsEditText,
            bathroomsEditText, areaEditText, yearEditText;
    private AutoCompleteTextView typeEditText, categoryEditText;
    private ImageView imageViewUploaded;
    private Button buttonUploadImage, buttonSubmit;

    private ImageButton back_button;

    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseFirestore db;

    private static final String CLOUDINARY_CLOUD_NAME = "dvyiii6qq";
    private static final String CLOUDINARY_UPLOAD_PRESET = "dreamh";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is logged in - required for adding properties
        if (!com.example.realestateapp.utils.AuthHelper.isUserLoggedIn()) {
            com.example.realestateapp.utils.AuthHelper.requireLogin(this, "You need to login to add a property for sale or rent");
            finish();
            return;
        }
        
        setContentView(R.layout.add_property_listing);

        locationEditText = findViewById(R.id.property_location);
        typeEditText = findViewById(R.id.property_type);
        descriptionEditText = findViewById(R.id.property_description);
        shortDescriptionEditText = findViewById(R.id.property_shortdescription);
        ownerNameEditText = findViewById(R.id.property_ownername);
        contactNoEditText = findViewById(R.id.property_contactno);
        priceEditText = findViewById(R.id.property_price);
        categoryEditText = findViewById(R.id.property_category);
        bedroomsEditText = findViewById(R.id.property_bedrooms);
        bathroomsEditText = findViewById(R.id.property_bathrooms);
        areaEditText = findViewById(R.id.property_area);
        yearEditText = findViewById(R.id.property_year);
        imageViewUploaded = findViewById(R.id.imageViewUploaded);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        back_button = findViewById(R.id.back_button);

        // Setup dropdowns for Type and Category
        setupTypeDropdown();
        setupCategoryDropdown();

        // Check if property type was passed from intent (for Sell button)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("property_type")) {
            String prefillType = intent.getStringExtra("property_type");
            if (prefillType != null && !prefillType.isEmpty()) {
                typeEditText.setText(prefillType, false);
            }
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        // Set onClickListener for backButton
        back_button.setOnClickListener(v -> {
            // Navigate back to the previous activity
            finish();
        });



        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request permissions if needed (though ACTION_GET_CONTENT works without them on Android 10+)
                // But we request for better compatibility
                if (PermissionHelper.checkAndRequestImagePermissions(AddPropertyActivity.this)) {
                    showImagePickerOptions();
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate form
                if (!validateForm()) {
                    return;
                }

                // Retrieve data from EditText fields
                String location = locationEditText.getText().toString().trim();
                String type = typeEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String shortDescription = shortDescriptionEditText.getText().toString().trim();
                String ownerName = ownerNameEditText.getText().toString().trim();
                String contactNo = contactNoEditText.getText().toString().trim();
                String price = priceEditText.getText().toString().trim();
                String category = categoryEditText.getText().toString().trim();
                String bedrooms = bedroomsEditText.getText().toString().trim();
                String bathrooms = bathroomsEditText.getText().toString().trim();
                String area = areaEditText.getText().toString().trim();
                String year = yearEditText.getText().toString().trim();

                // Create a Map to store the property data
                Map<String, Object> propertyData = new HashMap<>();
                propertyData.put("location", location);
                propertyData.put("type", type);
                propertyData.put("description", description);
                propertyData.put("shortdescription", shortDescription);
                propertyData.put("ownername", ownerName);
                propertyData.put("contactno", contactNo);
                propertyData.put("price", price);
                propertyData.put("category", category);
                if (!bedrooms.isEmpty()) propertyData.put("bedrooms", bedrooms);
                if (!bathrooms.isEmpty()) propertyData.put("bathrooms", bathrooms);
                if (!area.isEmpty()) propertyData.put("area", area);
                if (!year.isEmpty()) propertyData.put("year", year);

                /* Add data to Firestore
                db.collection("Properties")
                        .add(propertyData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Data added successfully
                                Toast.makeText(AddPropertyActivity.this, "Property added successfully", Toast.LENGTH_SHORT).show();
                                // Clear the form after successful submission
                                clearForm();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to add data
                                Toast.makeText(AddPropertyActivity.this, "Failed to add property", Toast.LENGTH_SHORT).show();
                            }
                        });*/

                // Upload image to Cloudinary and get image URL
                if (imageUri != null) {
                    Toast.makeText(AddPropertyActivity.this, "Uploading image...", Toast.LENGTH_SHORT).show();
                    new Thread(() -> {
                        try {
                            Log.d("AddProperty", "Starting Cloudinary upload to: " + CLOUDINARY_CLOUD_NAME);
                            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                            byte[] data = new byte[8192];
                            int nRead;
                            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, nRead);
                            }
                            buffer.flush();
                            byte[] bytes = buffer.toByteArray();
                            Log.d("AddProperty", "Image size: " + bytes.length + " bytes");

                            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                            okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(bytes, okhttp3.MediaType.parse("image/*"));
                            okhttp3.MultipartBody requestBody = new okhttp3.MultipartBody.Builder()
                                    .setType(okhttp3.MultipartBody.FORM)
                                    .addFormDataPart("file", "upload.jpg", fileBody)
                                    .addFormDataPart("upload_preset", CLOUDINARY_UPLOAD_PRESET)
                                    .build();

                            String url = "https://api.cloudinary.com/v1_1/" + CLOUDINARY_CLOUD_NAME + "/image/upload";
                            okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();

                            okhttp3.Response response = client.newCall(request).execute();
                            Log.d("AddProperty", "Cloudinary response code: " + response.code());
                            if (response.isSuccessful()) {
                                String body = response.body().string();
                                Log.d("AddProperty", "Cloudinary response: " + body);
                                org.json.JSONObject json = new org.json.JSONObject(body);
                                String uploadedUrl = json.optString("secure_url", json.optString("url"));
                                Log.d("AddProperty", "Uploaded URL: " + uploadedUrl);
                                runOnUiThread(() -> {
                                    propertyData.put("imageuri", uploadedUrl);
                                    Log.d("AddProperty", "Adding property to Firestore");
                                    db.collection("Properties").add(propertyData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("AddProperty", "Property added successfully");
                                                    Toast.makeText(AddPropertyActivity.this, "Property added successfully", Toast.LENGTH_SHORT).show();
                                                    clearForm();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("AddProperty", "Failed to add property to Firestore", e);
                                                    Toast.makeText(AddPropertyActivity.this, "Failed to add property: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                });
                            } else {
                                String errorBody = response.body() != null ? response.body().string() : "No error body";
                                Log.e("AddProperty", "Cloudinary upload failed: " + response.code() + " - " + errorBody);
                                runOnUiThread(() -> Toast.makeText(AddPropertyActivity.this, "Failed to upload image: " + response.code(), Toast.LENGTH_LONG).show());
                            }
                        } catch (Exception e) {
                            Log.e("AddProperty", "Exception during upload", e);
                            runOnUiThread(() -> Toast.makeText(AddPropertyActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                } else {
                    Toast.makeText(AddPropertyActivity.this, "Please upload an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showImagePickerOptions() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                // Camera
                if (PermissionHelper.hasCameraPermission(this)) {
                    openCamera();
                } else {
                    PermissionHelper.checkAndRequestImagePermissions(this);
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Gallery
                openGallery();
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_IMAGE_CAMERA);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                showImagePickerOptions();
            } else {
                Toast.makeText(this, "Permissions are required to select images", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI from gallery
            imageUri = data.getData();
            // Display the selected image in the ImageView
            imageViewUploaded.setImageURI(imageUri);
        } else if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK && data != null) {
            // Get the image from camera
            android.graphics.Bitmap photo = (android.graphics.Bitmap) data.getExtras().get("data");
            if (photo != null) {
                // Save bitmap to a temporary file and get URI
                try {
                    java.io.File tempFile = new java.io.File(getCacheDir(), "temp_camera_image.jpg");
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
                    photo.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();
                    imageUri = Uri.fromFile(tempFile);
                    imageViewUploaded.setImageURI(imageUri);
                } catch (Exception e) {
                    Toast.makeText(this, "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setupTypeDropdown() {
        String[] types = {"Sell", "Rent"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);
        typeEditText.setAdapter(adapter);
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Home", "Villa", "Flat", "Building", "Bungalow", "Farmhouse"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryEditText.setAdapter(adapter);
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (locationEditText.getText().toString().trim().isEmpty()) {
            locationEditText.setError("Location is required");
            isValid = false;
        }

        if (typeEditText.getText().toString().trim().isEmpty()) {
            typeEditText.setError("Property type is required");
            isValid = false;
        }

        if (shortDescriptionEditText.getText().toString().trim().isEmpty()) {
            shortDescriptionEditText.setError("Property title is required");
            isValid = false;
        }

        if (descriptionEditText.getText().toString().trim().isEmpty()) {
            descriptionEditText.setError("Description is required");
            isValid = false;
        }

        if (ownerNameEditText.getText().toString().trim().isEmpty()) {
            ownerNameEditText.setError("Owner name is required");
            isValid = false;
        }

        if (contactNoEditText.getText().toString().trim().isEmpty()) {
            contactNoEditText.setError("Contact number is required");
            isValid = false;
        } else if (contactNoEditText.getText().toString().trim().length() < 10) {
            contactNoEditText.setError("Please enter a valid contact number");
            isValid = false;
        }

        if (priceEditText.getText().toString().trim().isEmpty()) {
            priceEditText.setError("Price is required");
            isValid = false;
        }

        if (categoryEditText.getText().toString().trim().isEmpty()) {
            categoryEditText.setError("Category is required");
            isValid = false;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a property image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void clearForm() {
        locationEditText.setText("");
        typeEditText.setText("");
        descriptionEditText.setText("");
        shortDescriptionEditText.setText("");
        ownerNameEditText.setText("");
        contactNoEditText.setText("");
        priceEditText.setText("");
        categoryEditText.setText("");
        bedroomsEditText.setText("");
        bathroomsEditText.setText("");
        areaEditText.setText("");
        yearEditText.setText("");
        imageUri = null;
        imageViewUploaded.setImageDrawable(null);
    }
}

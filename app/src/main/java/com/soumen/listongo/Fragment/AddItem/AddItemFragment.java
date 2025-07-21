package com.soumen.listongo.Fragment.AddItem;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddItemFragment extends Fragment {
    public AddItemFragment() {
    }

    TextInputEditText edtTitle, edtDescription, edtPrice, nickNameEditText;
    MaterialAutoCompleteTextView dropCategory;
    ImageView productImageView;
    MaterialButton uploadButton, submitButton;
    TextInputLayout dropdown_menu;
    ProgressBar addProgress;
    private final int gallary_code = 1000;
    private Uri imageUri;
    String item,email;
    Long userId;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        edtTitle = view.findViewById(R.id.titleEditText);
        edtDescription = view.findViewById(R.id.descriptionEditText);
        edtPrice = view.findViewById(R.id.priceEditText);
        dropCategory = view.findViewById(R.id.categoryDropdown);
        productImageView = view.findViewById(R.id.productImageView);
        uploadButton = view.findViewById(R.id.uploadButton);
        dropdown_menu = view.findViewById(R.id.dropdown_menu);
        submitButton = view.findViewById(R.id.submitButton);
        addProgress = view.findViewById(R.id.addProgress);
        nickNameEditText = view.findViewById(R.id.nickNameEditText);

        userId = getArguments().getLong("UserId");
        int credit = getArguments().getInt("credit");
        email=getArguments().getString("email");

        uploadButton.setOnClickListener(v -> {
            Intent image = new Intent(Intent.ACTION_PICK);
            image.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(image, gallary_code);

        });

        submitButton.setOnClickListener(v -> {
            if (credit > 25) {
                if (edtTitle.getText().toString() == null || edtTitle.getText().toString().isEmpty() ||
                        edtDescription.getText().toString() == null || edtDescription.getText().toString().isEmpty() ||
                        edtPrice.getText().toString() == null || edtPrice.getText().toString().isEmpty() ||
                        dropCategory.getText().toString() == null || dropCategory.getText().toString().isEmpty() ||
                        productImageView == null) {
                    Toast.makeText(getContext(), "Something is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                String title = edtTitle.getText().toString().trim().toLowerCase();
                isPresent(title);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Insufficient Credits")
                        .setMessage("You don’t have enough credit points to perform this action.\nPlease buy credits to continue.")
                        .setPositiveButton("OK", null)
                        .show();

            }
        });

        String[] categories = {"Soft Drinks", "Medicine", "Sweets & Chips", "Fashion", "Fresh Vegetable", "Non Veg", "Spices", "Husk Store", "Fresh Fruits", "Dry Fruits", "Flowers & Leaves", "Body Care", "Exotics", "Coriander & Others", "Dairy, Brade & Eggs", "Electronics", "Atta, Rice & Dal", "Bakery & Brade", "Puja Store"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, categories);
        dropCategory.setAdapter(adapter);

        dropdown_menu.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard(requireActivity());
            }
            return false;
        });

        dropCategory.setOnItemClickListener((adapterView, view1, position, id) -> {
            item = adapterView.getItemAtPosition(position).toString();
        });


        return view;
    }

    public void hideKeyboard(FragmentActivity fragmentActivity) {
        View view = fragmentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == gallary_code && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                productImageView.setImageURI(selectedImageUri);

                // Save the image Uri for later use in addProduct()
                imageUri = selectedImageUri;  // declare imageUri as a class-level variable
            }
        }
    }

    private void isPresent(String title) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> isPresent = apiService.isPresent(title);
        isPresent.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("AddResponse", String.valueOf(response.code()));
                if (response.code() == 200) {
                    addProduct();
                    cost();
                } else {
                    Toast.makeText(getContext(), "This product already exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }


    public void addProduct() {
        addProgress.setVisibility(VISIBLE);
        if (imageUri == null) {
            return;
        }

        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);

            // 1. Collect product data
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString();
            double price = Double.parseDouble(edtPrice.getText().toString());
            String nickName = nickNameEditText.getText().toString().trim();

            // 2. Create product object
            AddProductModel product = new AddProductModel();
            product.setTitle(title);
            product.setDescription(description);
            product.setPrice(price);
            product.setNickName(nickName);
            product.setCategory(item);

            // 3. Convert product to JSON
            Gson gson = new Gson();
            String json = gson.toJson(product);
            RequestBody productBody = RequestBody.create(json, MediaType.parse("application/json"));

            // 4. Prepare image part from Uri
            ContentResolver contentResolver = getContext().getContentResolver();

            // Get MIME type (e.g., "image/png", "image/jpeg")
            String mimeType = contentResolver.getType(imageUri);

            // Get file name from Uri
            String fileName = getFileNameFromUri(imageUri);

            // Read image bytes
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);

            // Create request body and part
            RequestBody imageBody = RequestBody.create(imageBytes, MediaType.parse(mimeType));
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileName, imageBody);

            // 6. Send the request
            Call<ResponseBody> call = apiService.addProduct(productBody, imagePart, userId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                        edtTitle.setText("");
                        edtDescription.setText("");
                        edtPrice.setText("");
                        dropCategory.setText("");
                        productImageView.setImageResource(R.drawable.product);
                        nickNameEditText.setText("");
                        addProgress.setVisibility(GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("Add Successfully")
                                .setMessage("Your Product was added successfully.\nIt will be listed after approval by the admin.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss(); // Dismiss the dialog properly
                                    }
                                });

                        AlertDialog dialog = builder.create(); // Use AlertDialog type
                        dialog.show();
                    } else {
                        String errorMsg = "";
                        try {
                            errorMsg = response.errorBody().string(); // Convert to readable text
                        } catch (IOException e) {
                            errorMsg = "Error reading error body: " + e.getMessage();
                        }
                        Log.e("Upload", "Server Error: " + errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload", "Failure: " + t.getMessage().toString());
                }
            });

        } catch (Exception e) {
            Log.e("Upload", "Exception: " + e.getMessage().toString());
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public void cost(){
        ApiService apiService=ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> costCredit=apiService.costCredit(email,25);
        costCredit.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String title = "Product Added";
                String message = "Your product has been added successfully. 25 credits have been deducted from your wallet.\n" +
                        "If the admin rejects the product, your credits will be refunded.\n" +
                        "Review process typically takes up to 5 days.";
                notification(getContext(),title,message);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }

    public void notification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel_id";
        String channelName = "Default Channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for basic notifications");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.liston)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Notification ID should be unique for each notification
        int notificationId = new Random().nextInt(10000);
        notificationManager.notify(notificationId, builder.build());
    }


}
package com.soumen.listongo.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.OptionActivity;
import com.soumen.listongo.R;
import com.soumen.listongo.ForAdmin.ReqForMakeAdmin.ReqForAdminActivity;
import com.soumen.listongo.SettingActivity.SettingsActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    public ProfileFragment() {
    }
    private ActivityResultLauncher<Intent> launcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView name = view.findViewById(R.id.text_username);
        TextView userid = view.findViewById(R.id.text_user_id);
        TextView email = view.findViewById(R.id.text_email);
        MaterialButton logoutButton = view.findViewById(R.id.button_logout);
        MaterialButton reqAdmin = view.findViewById(R.id.button_req_for_admin);

        Long id = getArguments().getLong("UserId");
        String userName = getArguments().getString("UserName");
        String userEmail = getArguments().getString("email");
        boolean isAdmin = getArguments().getBoolean("isAdmin");

        if (isAdmin) {
            reqAdmin.setVisibility(GONE);
        }

        reqAdmin.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Option")
                    .setMessage("Chose one option.\nEmail link is not working now.")
                    .setPositiveButton("Here", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getContext(), ReqForAdminActivity.class);
                            intent.putExtra("email", userEmail);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Send link", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ApiService service = ApiClient.getInstance().create(ApiService.class);
                            Call<ResponseBody> sendLink = service.sendLink(userEmail);
                            sendLink.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Toast.makeText(getContext(), "Email Send Successful.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                                }
                            });
                        }
                    }).show();

        });

        email.setText(userEmail);
        name.setText(userName);
        userid.setText(id.toString());

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean shouldShowDialog = result.getData().getBooleanExtra("SHOW_DIALOG", false);
                        showBottomSheetDialog(shouldShowDialog);
                    }
                }
        );
        return view;
    }
    private void showBottomSheetDialog(boolean isSuccess) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.payment_bottom_dialog, null);

        // These should be called after inflating the view
        MaterialTextView textSuccess = view.findViewById(R.id.payStatus);
        LottieAnimationView payLotti = view.findViewById(R.id.payAnim);

        // Set values based on success/failure
        if (isSuccess) {
            textSuccess.setText("Payment Successful");
            payLotti.setAnimation(R.raw.pay_done);
        } else {
            textSuccess.setText("Payment Failed");
            payLotti.setAnimation(R.raw.payment_failed);
        }

        payLotti.playAnimation(); // Optional: Play the animation
        dialog.setContentView(view);
        dialog.show();
    }

}
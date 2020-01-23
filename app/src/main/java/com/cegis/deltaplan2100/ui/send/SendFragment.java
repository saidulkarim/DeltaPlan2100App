package com.cegis.deltaplan2100.ui.send;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.API;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.models.ModelSendFeedback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SendFragment extends Fragment {
    private SendViewModel sendViewModel;
    private Typeface tf;
    private TextView textViewEmail;
    private EditText txtUserName, txtUserPhone, txtUserEmail, txtUserMessage;
    private Button btnSubmit;
    private String result;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendViewModel = ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        tf = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/titillium_semi_bold.ttf");

        textViewEmail = root.findViewById(R.id.textViewEmail);
        txtUserName = root.findViewById(R.id.txtUserName);
        txtUserPhone = root.findViewById(R.id.txtUserPhone);
        txtUserEmail = root.findViewById(R.id.txtUserEmail);
        txtUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Is_Valid_Email(txtUserEmail);
            }

            public void Is_Valid_Email(EditText edt) {
                if (edt.getText().toString() == null) {
                    textViewEmail.setError("Invalid Email Address");
                } else if (isEmailValid(edt.getText().toString()) == false) {
                    textViewEmail.setError("Invalid Email Address");
                } else {
                    //
                }
            }

            boolean isEmailValid(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });

        txtUserMessage = root.findViewById(R.id.txtUserMessage);
        btnSubmit = root.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this::onClick);

        return root;
    }

    public void onClick(View v) {
        String uName = txtUserName.getText().toString().trim();
        String uPhone = txtUserPhone.getText().toString().trim();
        String uEmail = txtUserEmail.getText().toString().trim();
        String uMessage = txtUserMessage.getText().toString().trim();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient okHttpClient = API.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        API api = retrofit.create(API.class);

        //ModelSendFeedback modelSendFeedback = new ModelSendFeedback(uName, uPhone, uEmail, uMessage);
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Please wait...", true);

        Call<String> call = api.sendFeedback(uName, uPhone, uEmail, uMessage);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dialog.dismiss();

                if (response.isSuccessful()) {
                    try {
                        result = response.body();
                        Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        result = new String();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();
                    result = new String();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();
                result = new String();
            }
        });
    }
}
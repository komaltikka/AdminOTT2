package com.example.adminott.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.adminott.R;
import com.example.adminott.activities.MainActivity;
import com.example.adminott.model.User;
import com.example.adminott.model.Useradmin;
import com.example.adminott.services.MyInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    MyInterface myInterface;
    TextView login_button;
    EditText emailInput_op,passwordInput_op;
    TextView registerTV_op,forgotPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login,container,false);
        emailInput_op=view.findViewById(R.id.emailInput);
        passwordInput_op=view.findViewById(R.id.passwordInput);
        login_button=view.findViewById(R.id.loginBtn);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();

            }
        });
        registerTV_op=view.findViewById(R.id.registerTV);
        registerTV_op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.register();

            }
        });
        forgotPassword=view.findViewById(R.id.forgotpassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ForgotPasswordFragment())
                        .commit();
            }
        });
        return view;
    }

    private void loginUser() {
        String email=emailInput_op.getText().toString().trim();
        String password=passwordInput_op.getText().toString().trim();
        if (TextUtils.isEmpty(email))
        {
            MainActivity.appPreference.showToast("Enter your emailId");
        }else if (TextUtils.isEmpty(password))
        {
            MainActivity.appPreference.showToast("Enter your password");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            MainActivity.appPreference.showToast("Email id is invalid");
        }else if (password.length()<6)
        {
            MainActivity.appPreference.showToast("Password too short!");
        }else
            {
                Call<User> userCall = MainActivity.serviceApi.doLogin(email,password);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call,@NonNull Response<User> response) {

                        if(response.body().getResponse().equals("data"))
                        {
                          //  myInterface.login(response.body().getName(),response.body().getEmail(),response.body().getCreatedAt());
                         //   System.out.println(" "+response.body().getName()+response.body().getEmail()+response.body().getCreatedAt());
                            Toast.makeText(getActivity(), "Login Successfull!!", Toast.LENGTH_SHORT).show();
                            emailInput_op.setText("");
                            passwordInput_op.setText("");
                            myInterface.homepage();
                        }

                        else  if(response.body().getResponse().equals("verify")){
                            Toast.makeText(getActivity(), "Verify OTP", Toast.LENGTH_SHORT).show();
                            emailInput_op.setText("");
                            passwordInput_op.setText("");
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setTitle("Check the email for verification");
                            LayoutInflater layoutInflater=LayoutInflater.from(getContext());
                            View view=layoutInflater.inflate(R.layout.alertdialog,null);
                            builder.setView(view);
                            final EditText otpinput=view.findViewById(R.id.userotp);


                            builder.setPositiveButton("Submit",new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,int id) {

                                    String otp=otpinput.getText().toString().trim();

                                    Call<User> userCall = MainActivity.serviceApi.doVerify(otp);
                                    userCall.enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            if(response.body().getResponse().matches("sucess"))
                                            {
                                                Toast.makeText(getContext(), "Successfully Verify OTP", Toast.LENGTH_SHORT).show();

                                                getFragmentManager()
                                                        .beginTransaction()
                                                        .replace(R.id.fragment_container, new LoginFragment())

                                                        .commit();
                                            }
                                            else
                                            {
                                                Toast.makeText(getContext(), "Invalid OTP ", Toast.LENGTH_SHORT).show();
                                                verifyotp();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {
                                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                            System.out.println("MyError"+t.getMessage());
                                        }

                                    });

                                }


                            });


                            builder.setNegativeButton("Re-try",new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,int id) {
                                    verifyotp();

                                }


                            });
                            builder.show();


                        }
                        else if(response.body().getResponse().equals("login_failed")){
                            Toast.makeText(getActivity(), "Login Failed!!", Toast.LENGTH_SHORT).show();
                            emailInput_op.setText("");
                            passwordInput_op.setText("");

                        }


                    }

                    private void verifyotp() {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setCancelable(false);
                        builder.setTitle("Check the email for verification");
                        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
                        View view=layoutInflater.inflate(R.layout.alertdialog,null);
                        builder.setView(view);
                        final EditText otpinput=view.findViewById(R.id.userotp);


                        builder.setPositiveButton("Submit",new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,int id) {

                                String otp=otpinput.getText().toString().trim();

                                Call<User> userCall = MainActivity.serviceApi.doVerify(otp);
                                userCall.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        if(response.body().getResponse().matches("sucess"))
                                        {
                                            Toast.makeText(getContext(), "Successfully Verify OTP", Toast.LENGTH_SHORT).show();

                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.fragment_container, new LoginFragment())

                                                    .commit();
                                        }
                                        else
                                        {
                                            Toast.makeText(getContext(), "Invalid OTP ", Toast.LENGTH_SHORT).show();
                                            verifyotp();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                        System.out.println("MyError"+t.getMessage());
                                    }

                                });



                            }


                        });


                        builder.setNegativeButton("Re-try",new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,int id) {
                                verifyotp();

                            }


                        });
                        builder.show();

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

                    }
                });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity= (Activity) context;
        myInterface= (MyInterface) activity;
    }
}
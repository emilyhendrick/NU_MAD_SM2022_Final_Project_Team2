package com.example.nu_mad_sm2022_final_project_team2.login_flow;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private ImageView loginBackButton;

    private FirebaseAuth mAuth;
    private ILoginFragmentAction mListener;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ILoginFragmentAction) {
            this.mListener = (ILoginFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        BottomNavigationView navView = view.findViewById(R.id.nav_view);
        navView.setVisibility(View.GONE);

        emailInput = view.findViewById(R.id.loginInputEmail);
        passwordInput = view.findViewById(R.id.loginInputPassword);
        loginButton = view.findViewById(R.id.loginButton);
        loginBackButton = view.findViewById(R.id.loginBackButton);

        loginBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loginBackButtonClicked();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                boolean isValid = validateInputs(email, password);

                if (isValid) {
                    login(email, password);
                }
            }
        });

        return view;
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;
        if (email.equals("")) {
            emailInput.setError("Must input email to login!");
            isValid = false;
        }

        if (password.equals("")) {
            passwordInput.setError("Must input password to login!");
            isValid = false;
        }

        return isValid;
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Login Failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            mListener.loginDone(mUser);
                        }
                    }
                });
    }

    public interface ILoginFragmentAction {
        void loginBackButtonClicked();
        void loginDone(FirebaseUser firebaseUser);
    }
}
package com.example.nu_mad_sm2022_final_project_team2.login_flow;

import android.content.Context;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class RegisterFragment extends Fragment {

    private static final String IMAGE_URI = "imageUri";

    private EditText firstNameInput, lastNameInput, pronounsInput, birthdayInput, emailInput, passwordInput, retypedPasswordInput;
    private Button uploadAvatarButton, registerButton;
    private ImageView avatar, registerBackButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IRegisterFragmentAction mListener;

    private static Uri avatarUri;

    public RegisterFragment() {}

    public static RegisterFragment newInstance(Uri imageUri) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_URI, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            avatarUri = getArguments().getParcelable(IMAGE_URI);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IRegisterFragmentAction) {
            this.mListener = (IRegisterFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.setVisibility(View.GONE);

        firstNameInput = view.findViewById(R.id.registerFirstNameInput);
        lastNameInput = view.findViewById(R.id.registerLastNameInput);
        pronounsInput = view.findViewById(R.id.registerPronounsInput);
        birthdayInput = view.findViewById(R.id.registerBirthdayInput);
        emailInput = view.findViewById(R.id.registerInputEmail);
        passwordInput = view.findViewById(R.id.registerInputPassword);
        retypedPasswordInput = view.findViewById(R.id.registerInputRetypedPassword);
        registerButton = view.findViewById(R.id.registerButton);
        uploadAvatarButton = view.findViewById(R.id.registerUploadAvatarButton);
        avatar = view.findViewById(R.id.registerAvatar);
        registerBackButton = view.findViewById(R.id.registerBackButton);

        if (avatarUri != null) {
            Glide.with(view)
                    .load(avatarUri)
                    .centerCrop()
                    .into(avatar);
        }

        uploadAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.uploadAvatarClicked();
            }
        });

        registerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.registerBackButtonClicked();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String pronouns = pronounsInput.getText().toString();
                String birthday = birthdayInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String retypedPassword = retypedPasswordInput.getText().toString();

                boolean inputsValid = validateInputs(firstName, lastName, pronouns, birthday, email, password, retypedPassword);

                if (inputsValid) {
                    createUserInFirebase(getContext(), firstName, lastName, pronouns, birthday, email, password);
                }
            }
        });


        return view;
    }

    private boolean validateInputs(String firstName, String lastName, String pronouns, String birthday, String email, String password, String retypedPassword) {
        boolean isValid = true;
        if (firstName.equals("")) {
            firstNameInput.setError("Must enter first name!");
            isValid = false;
        }

        if (lastName.equals("")) {
            lastNameInput.setError("Must enter last name!");
            isValid = false;
        }

        if (pronouns.equals("")) {
            pronounsInput.setError("Must enter pronouns!");
            isValid = false;
        }

        if (birthday.equals("")) {
            lastNameInput.setError("Must enter birthday! (MM/DD/YYYY)");
            isValid = false;
        }

        if (email.equals("")) {
            emailInput.setError("Must enter email!");
            isValid = false;
        }

        if (password.equals("")) {
            passwordInput.setError("Must enter password!");
            isValid = false;
        }

        if (!retypedPassword.equals(password)) {
            retypedPasswordInput.setError("Passwords must match!");
            isValid = false;
        }

        return isValid;
    }

    private void createUserInFirebase(Context context, String firstName, String lastName, String pronouns, String birthday, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(context, "User added to Firebase", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add user to Firebase. Try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = task.getResult().getUser();
                            addUserToDataBase(context, firstName, lastName, pronouns, birthday, email);
                            mListener.registerDone(mUser, avatarUri);
                        }
                    }
                });
    }

    private void addUserToDataBase(Context context, String firstName, String lastName, String pronouns, String birthday, String email) {
        User user = new User(firstName, lastName, pronouns, birthday, email, new ArrayList<>(), new ArrayList<>());

        db.collection("users")
                .document(mUser.getEmail())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"User added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add user! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface IRegisterFragmentAction {
        void uploadAvatarClicked();
        void registerBackButtonClicked();
        void registerDone(FirebaseUser mUser, Uri avatarUri);
    }
}
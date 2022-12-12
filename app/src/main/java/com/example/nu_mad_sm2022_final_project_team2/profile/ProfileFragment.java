package com.example.nu_mad_sm2022_final_project_team2.profile;

import android.content.Context;
import android.net.TelephonyNetworkSpecifier;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileNotFoundException;

public class ProfileFragment extends Fragment {

    private ImageView avatar, profileBackButton;
    private TextView name, pronouns, email, birthday;
    private Button editProfileButton, logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IProfileFragmentAction mListener;

    private static String userPassword;

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IProfileFragmentAction) {
            this.mListener = (IProfileFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profileName);
        pronouns = view.findViewById(R.id.profilePronouns);
        email = view.findViewById(R.id.profileEmail);
        birthday = view.findViewById(R.id.profileBirthday);
        avatar = view.findViewById(R.id.profileImage);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        profileBackButton = view.findViewById(R.id.profileLeftArrow);
        logoutButton = view.findViewById(R.id.logoutButton);

        loadProfile(view);

        profileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.profileBackButtonClicked();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editProfileButtonPressed();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logoutClicked();
            }
        });


        return view;
    }

    private void loadProfile(View view) {
        mUser.reload();
        db.collection("users").document(mUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Toast.makeText(getContext(), "Profile loaded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);

                            name.setText(user.getFirstName() + " " + user.getLastName());
                            pronouns.setText(user.getPronouns());
                            email.setText(user.getEmail());
                            birthday.setText(user.getBirthday());

                            if (user.getAvatarUri() != null) {
                                Uri avatarUri = Uri.parse(user.getAvatarUri());
                                Glide.with(view)
                                        .load(avatarUri)
                                        .centerCrop()
                                        .into(avatar);
                            } else {
                                avatar.setImageResource(R.drawable.select_avatar);
                            }
                        }
                    }
                });
    }

    public interface IProfileFragmentAction {
        void profileBackButtonClicked();
        void editProfileButtonPressed();
        void logoutClicked();
    }
}
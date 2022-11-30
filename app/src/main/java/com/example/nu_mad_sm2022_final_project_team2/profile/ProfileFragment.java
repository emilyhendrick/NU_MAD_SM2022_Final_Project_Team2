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
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileFragment extends Fragment {

    private ImageView avatar, profileBackButton;
    private TextView name, pronouns, email, birthday;
    private Button editProfileButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IProfileFragmentAction mListener;

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

        final User[] user = new User[1];

        db.collection("users").document(mUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user[0] = documentSnapshot.toObject(User.class);

                        name.setText(user[0].getFirstName() + " " + user[0].getLastName());
                        pronouns.setText(user[0].getPronouns());
                        email.setText(user[0].getEmail());
                        birthday.setText(user[0].getBirthday());

                        if (user[0].getAvatarUri() != null) {
                            Uri avatarUri = Uri.parse(user[0].getAvatarUri());
                            Glide.with(view)
                                    .load(avatarUri)
                                    .centerCrop()
                                    .into(avatar);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        return view;
    }

    public interface IProfileFragmentAction {
        void profileBackButtonClicked();
        void editProfileButtonPressed();
    }
}
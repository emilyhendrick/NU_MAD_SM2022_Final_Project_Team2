package com.example.nu_mad_sm2022_final_project_team2.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.example.nu_mad_sm2022_final_project_team2.alarm.Alarm;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.Event;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.TaskPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditProfileFragment extends Fragment {

    private ImageView avatar, editProfileBackArrow;
    private TextView firstNameInput, lastNameInput, pronounsInput, emailInput, birthdayInput, passwordInput, retypedPasswordInput;
    private Button editAvatarButton, saveChangesButton, editProfileBackButton;

    private ArrayList<Alarm> alarms = new ArrayList<>();
    private ArrayList<TaskPI> tasks = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();
    private String avatarUri = null;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IEditProfileFragmentAction mListener;

    public EditProfileFragment() {}

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
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
        if (context instanceof IEditProfileFragmentAction) {
            this.mListener = (IEditProfileFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        firstNameInput = view.findViewById(R.id.editProfileFirstNameInput);
        lastNameInput = view.findViewById(R.id.editProfileLastNameInput);
        pronounsInput = view.findViewById(R.id.editProfilePronounsInput);
        birthdayInput = view.findViewById(R.id.editProfileBirthdayInput);
        emailInput = view.findViewById(R.id.editProfileEmailInput);
        passwordInput = view.findViewById(R.id.editProfilePasswordInput);
        retypedPasswordInput = view.findViewById(R.id.editProfileRetypedPasswordInput);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        editAvatarButton = view.findViewById(R.id.editAvatarButton);
        avatar = view.findViewById(R.id.editAvatar);
        editProfileBackArrow = view.findViewById(R.id.editTaskLeftArrow);

        loadProfile(view);

        editProfileBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editProfileBackArrowClicked();
            }
        });

        editAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editAvatarButtonClicked();
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newFirstName = firstNameInput.getText().toString();
                String newLastName = lastNameInput.getText().toString();
                String newPronouns = pronounsInput.getText().toString();
                String newEmail = emailInput.getText().toString();
                String newBirthday = birthdayInput.getText().toString();

                User newUser = new User(newFirstName, newLastName, newPronouns, newBirthday, newEmail, alarms, tasks, events);
                updateFirebaseUser(getContext(), newUser);

                String newPassword = passwordInput.getText().toString();
                String newRetypedPassword = retypedPasswordInput.getText().toString();

                if (newPassword.length() > 0 && newPassword.equals(newRetypedPassword)) {
                    mUser.updatePassword(newPassword);
                    mUser.reload();
                }

                mListener.editProfileDone(mUser);
            }
        });


        return view;
    }

    private void loadProfile(View view) {
        db.collection("users").document(mUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);

                        alarms = user.getAlarms();
                        tasks = user.getTasks();
                        events = user.getEvents();

                        firstNameInput.setText(user.getFirstName());
                        lastNameInput.setText(user.getLastName());
                        pronounsInput.setText(user.getPronouns());
                        emailInput.setText(user.getEmail());
                        birthdayInput.setText(user.getBirthday());

                        if (user.getAvatarUri() != null) {
                            avatarUri = user.getAvatarUri();
                            Uri avatarUri = Uri.parse(user.getAvatarUri());
                            Glide.with(view)
                                    .load(avatarUri)
                                    .centerCrop()
                                    .into(avatar);
                        }
                    }
                })
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
                });
    }

    private void updateFirebaseUser(Context context, User updatedUser) {
        db.collection("users")
                .document(mUser.getEmail())
                .set(updatedUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"User updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to update user! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });

        mUser.reload();
    }

    public interface IEditProfileFragmentAction {
        void editProfileBackArrowClicked();
        void editAvatarButtonClicked();
        void editProfileDone(FirebaseUser mUser);
    }
}
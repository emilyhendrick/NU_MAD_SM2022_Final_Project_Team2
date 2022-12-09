package com.example.nu_mad_sm2022_final_project_team2.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditProfileFragment extends Fragment {

    private ImageView avatar, editProfileBackArrow;
    private TextView firstNameInput, lastNameInput, pronounsInput, emailInput, birthdayInput, passwordInput, retypedPasswordInput;
    private Button editAvatarButton, saveChangesButton, editProfileBackButton;

    private ArrayList<Alarm> alarms = new ArrayList<>();
    private String avatarUri = null;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IEditProfileFragmentAction mListener;

    private static String userPassword;

    public EditProfileFragment() {}

    public static EditProfileFragment newInstance(String password) {
        userPassword = password;
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
        editProfileBackArrow = view.findViewById(R.id.editProfileLeftArrow);

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
                String newPassword = passwordInput.getText().toString();
                String newRetypedPassword = retypedPasswordInput.getText().toString();

                if (!newPassword.equals(newRetypedPassword)) {
                    retypedPasswordInput.setError("Passwords must match");
                } else {
                    User newUser = new User(newFirstName, newLastName, newPronouns, newBirthday, newEmail, avatarUri, alarms);
                    updateUser(getContext(), newUser, newPassword);
                }
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
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUser(Context context, User updatedUser, String newPassword) {
        db.collection("users")
                .document(mUser.getEmail())
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateFirebaseUser(context, updatedUser, newPassword);
                    }
                });
    }

    private void updateFirebaseUser(Context context, User updatedUser, String newPassword) {
        db.collection("users")
                .document(updatedUser.getEmail())
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
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateAuthenticatedUserEmail(context, updatedUser, newPassword);
                    }
                });
    }

    private void updateAuthenticatedUserEmail(Context context, User updatedUser, String newPassword) {
        if (!mUser.getEmail().equals(updatedUser.getEmail())) {
            // Prompt the user to re-provide their sign-in credentials
            AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), userPassword);
            mUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mUser = mAuth.getCurrentUser();
                            mUser.updateEmail(updatedUser.getEmail())
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed to update authenticated user email! Try again!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Updated authenticated user email!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mUser.reload();
                                            if (newPassword.length() > 0 && !newPassword.equals(userPassword)) {
                                                updateAuthenticatedUserPassword(context, newPassword);
                                            } else {
                                                mListener.editProfileDone(mUser, userPassword);
                                            }
                                        }
                                    });
                        }
                    });
        } else if (newPassword.length() > 0 && !newPassword.equals(userPassword)) {
            updateAuthenticatedUserPassword(context, newPassword);
        } else {
            mUser.reload();
            mListener.editProfileDone(mUser, userPassword);
        }
    }

    private void updateAuthenticatedUserPassword(Context context, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), userPassword);
        mUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mUser = mAuth.getCurrentUser();
                        mUser.updatePassword(newPassword)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to update authenticated user password! Try again!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Updated authenticated user password!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mUser.reload();
                                        mListener.editProfileDone(mUser, newPassword);
                                    }
                                });
                    }
                });
    }

    public interface IEditProfileFragmentAction {
        void editProfileBackArrowClicked();
        void editAvatarButtonClicked();
        void editProfileDone(FirebaseUser mUser, String password);
    }
}
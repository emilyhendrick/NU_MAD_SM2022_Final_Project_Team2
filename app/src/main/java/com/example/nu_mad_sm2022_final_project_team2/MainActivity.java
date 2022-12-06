package com.example.nu_mad_sm2022_final_project_team2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.alarm.AddAlarmFragment;
import com.example.nu_mad_sm2022_final_project_team2.alarm.Alarm;
import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmFragment;
import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmsAdaptor;
import com.example.nu_mad_sm2022_final_project_team2.alarm.EditAlarmFragment;
import com.example.nu_mad_sm2022_final_project_team2.camera.CameraFragment;
import com.example.nu_mad_sm2022_final_project_team2.camera.DisplayFragment;
import com.example.nu_mad_sm2022_final_project_team2.databinding.ActivityMainAppBinding;
import com.example.nu_mad_sm2022_final_project_team2.databinding.ActivityMainBinding;
import com.example.nu_mad_sm2022_final_project_team2.login_flow.LoginFragment;
import com.example.nu_mad_sm2022_final_project_team2.login_flow.RegisterFragment;
import com.example.nu_mad_sm2022_final_project_team2.login_flow.WelcomeFragment;
import com.example.nu_mad_sm2022_final_project_team2.profile.EditProfileFragment;
import com.example.nu_mad_sm2022_final_project_team2.profile.ProfileFragment;
import com.example.nu_mad_sm2022_final_project_team2.ui.home.HomeFragment;
import com.example.nu_mad_sm2022_final_project_team2.ui.tasks.TasksFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.IWelcomeFragmentAction, RegisterFragment.IRegisterFragmentAction,
        LoginFragment.ILoginFragmentAction, ProfileFragment.IProfileFragmentAction, CameraFragment.IPhotoTaken, DisplayFragment.RetakePhoto,
        EditProfileFragment.IEditProfileFragmentAction, AlarmFragment.IAlarmFragmentAction, AlarmsAdaptor.IAlarmsListRecyclerAction,
        EditAlarmFragment.IEditAlarmFragmentAction, AddAlarmFragment.IAddAlarmFragmentAction, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_CODE = 0x100;
    public static final String CHANNEL_ID = "ALARM_CHANNEL";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;

    boolean isSetProfilePhotoFromRegister = false;
    boolean isSetProfilePhotoFromEditProfile = false;

    private BottomNavigationView bottomNavigationView;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("PenciledIn");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navCo = navHostFragment.getNavController();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_tasks, R.id.navigation_alarm, R.id.navigation_profile)
                .build();
       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navCo, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navCo);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Boolean cameraAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        Boolean readAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writeAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean alarmAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED;

        if (cameraAllowed && readAllowed && writeAllowed && alarmAllowed) {
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            populateScreen();

        } else {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SCHEDULE_EXACT_ALARM
            }, PERMISSIONS_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        createNotificationChannel();
        populateScreen();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void populateScreen() {
        if (currentUser != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.nav_host_fragment_activity_main, new HomeFragment(), "homeFragment")
//                    .addToBackStack(null)
//                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new RegisterFragment(), "homeFragment")
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new WelcomeFragment(), "welcomeFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void loginBackButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new WelcomeFragment(), "welcomeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void loginDone(FirebaseUser firebaseUser) {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setVisibility(View.VISIBLE);

        currentUser = firebaseUser;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new HomeFragment(), "homeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void uploadAvatarClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, CameraFragment.newInstance(),"cameraFragment")
                .addToBackStack(null)
                .commit();
        isSetProfilePhotoFromRegister = true;
        isSetProfilePhotoFromEditProfile = false;
    }

    @Override
    public void registerBackButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new WelcomeFragment(), "welcomeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void registerDone(FirebaseUser mUser, Uri avatarUri) {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setVisibility(View.VISIBLE);

        currentUser = mUser;
        if (avatarUri != null) {
            String path = "images/profilePhotos/" + currentUser.getEmail() + "/";
            updateProfilePhotoInFirebase(avatarUri, path);
            updateUserProfilePhoto(avatarUri);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new HomeFragment(), "homeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPhotoTaken(Uri imageUri) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, DisplayFragment.newInstance(imageUri),"displayFragment")
                .commit();
    }

    @Override
    public void onOpenGalleryPressed() {
        if (currentUser != null) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/" + currentUser.getEmail() + "*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            galleryLauncher.launch(intent);
        } else {
            Toast.makeText(MainActivity.this, "Cannot open gallery for unregistered user. Please take a photo instead.", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        Intent data = result.getData();
                        Uri selectedImageUri = data.getData();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment_activity_main, DisplayFragment.newInstance(selectedImageUri),"displayFragment")
                                .commit();
                    }
                }
            }
    );

    @Override
    public void onRetakePressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, CameraFragment.newInstance(), "cameraFragment")
                .commit();
    }

    @Override
    public void onUploadButtonPressed(Uri imageUri) {
        if (isSetProfilePhotoFromEditProfile) {
            BottomNavigationView navView = findViewById(R.id.nav_view);
            navView.setVisibility(View.VISIBLE);
            String path = "images/profilePhotos/" + currentUser.getEmail() + "/";
            updateProfilePhotoInFirebase(imageUri, path);
            updateUserProfilePhoto(imageUri);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, EditProfileFragment.newInstance(), "editProfileFragment")
                    .addToBackStack(null)
                    .commit();
        } else if (isSetProfilePhotoFromRegister) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, RegisterFragment.newInstance(imageUri), "registerFragment")
                    .addToBackStack(null)
                    .commit();
        }

        isSetProfilePhotoFromRegister = false;
        isSetProfilePhotoFromEditProfile = false;
    }

    private void updateProfilePhotoInFirebase(Uri imageUri, String path) {
        StorageReference storageReference = storage.getReference().child(path + imageUri.getLastPathSegment());
        UploadTask uploadImage = storageReference.putFile(imageUri);
        uploadImage.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Upload Failed! Try again!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "Upload successful! Check Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserProfilePhoto(Uri imageUri) {
        db.collection("users")
                .document(currentUser.getEmail())
                .update("avatarUri", imageUri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to update profile photo! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void profileBackButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new HomeFragment(), "homeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void editProfileButtonPressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, EditProfileFragment.newInstance(),"editProfileFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void logoutClicked() {
        mAuth.signOut();
        currentUser = null;
        populateScreen();
    }

    @Override
    public void editProfileBackArrowClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new ProfileFragment(), "profileFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void editAvatarButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, CameraFragment.newInstance(),"cameraFragment")
                .commit();
        isSetProfilePhotoFromRegister = false;
        isSetProfilePhotoFromEditProfile = true;
    }

    @Override
    public void editProfileDone(FirebaseUser mUser) {
        mUser.reload();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new ProfileFragment(), "profileFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void alarmBackArrowClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new HomeFragment(), "homeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void addAlarmClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, AddAlarmFragment.newInstance(),"addAlarmFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void alarmSwitched(Alarm alarm) {
        if (alarm.isOn()) {
            alarm.schedule(getApplicationContext());
        } else {
            alarm.cancelAlarm(getApplicationContext());
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, AlarmFragment.newInstance(), "alarmFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void editAlarmClicked(Alarm alarm, int position) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, EditAlarmFragment.newInstance(alarm, position), "editAlarmFragment")
                .commit();
    }

    @Override
    public void addAlarmBackArrowClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, AlarmFragment.newInstance(),"AlarmFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void addAlarmDone() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, AlarmFragment.newInstance(),"AlarmFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void editAlarmBackArrowClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, AlarmFragment.newInstance(),"AlarmFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void editAlarmDone() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, AlarmFragment.newInstance(),"AlarmFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void registerButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, RegisterFragment.newInstance(null), "registerFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void loginButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, LoginFragment.newInstance(), "loginFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new HomeFragment()).addToBackStack(null).commit();
                return true;

            case R.id.navigation_tasks:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new TasksFragment()).addToBackStack(null).commit();
                return true;

            case R.id.navigation_alarm:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, AlarmFragment.newInstance()).addToBackStack(null).commit();
                return true;

            case R.id.navigation_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, ProfileFragment.newInstance()).addToBackStack(null).commit();
                return true;
        }
        return false;
    }
}
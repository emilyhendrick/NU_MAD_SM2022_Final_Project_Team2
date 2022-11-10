package com.example.nu_mad_sm2022_final_project_team2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nu_mad_sm2022_final_project_team2.login_flow.WelcomeFragment;
import com.example.nu_mad_sm2022_final_project_team2.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("PenciledIn");

        /*
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new WelcomeFragment())
                .addToBackStack(null)
                .commit();

         */

        /*
        // for testing purposes
        ActivityResultLauncher<Intent> getAvatar =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == RESULT_OK) {
                                }
                            }
                        });

        Intent intentToSetAvatar = new Intent(MainActivity.this, MainAppActivity.class);
        getAvatar.launch(intentToSetAvatar);

         */
    }
}
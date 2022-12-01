package com.example.nu_mad_sm2022_final_project_team2.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.nu_mad_sm2022_final_project_team2.MainActivity;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.ui.home.HomeFragment;

public class WakeUpTaskActivity extends AppCompatActivity implements WakeUpTaskFragment.IWakeUpTaskFragmentAction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up_task);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.wakeUpTaskFragmentContainer, new WakeUpTaskFragment(), "wakeUpTaskFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void wakeUpBackButtonClicked() {
        Intent intent = new Intent(WakeUpTaskActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void wakeUpTaskLinkClicked(String linkStr) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkStr));
        startActivity(urlIntent);
    }
}
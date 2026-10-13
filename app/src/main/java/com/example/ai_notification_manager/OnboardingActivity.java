package com.example.ai_notification_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        TextView btnSkip = findViewById(R.id.btnSkip);
        MaterialButton btnGetStarted = findViewById(R.id.btnGetStarted);

        btnSkip.setOnClickListener(v -> proceedToPermission());
        btnGetStarted.setOnClickListener(v -> proceedToPermission());
    }

    private void proceedToPermission() {
        Intent intent = new Intent(OnboardingActivity.this, PermissionSetupActivity.class);
        startActivity(intent);
        finish();
    }
}

package dev.dkqz.fundkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import models.Account;

public class WelcomeActivity extends AppCompatActivity {

    public static String accountKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            createSignInIntent();
        else
            onGoToMainActivity();
    }

    @Override
    protected void onResume() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            createSignInIntent();
        super.onResume();
    }

    private void onGoToMainActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        accountKey = sharedPreferences.getString("accountKey", null);

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Account.accounts.orderByChild("key").equalTo(accountKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Account sp_account = ds.getValue(Account.class);
                            if (sp_account != null && sp_account.getOwnerUid().equals(userUid)) {
                                startActivity(intent);
                                return;
                            }
                        }

                        Account account = new Account(userUid, "Счёт", 0);
                        DatabaseReference push = Account.accounts.push();
                        accountKey = push.getKey();
                        sharedPreferences.edit().putString("accountKey", accountKey).apply();
                        account.setKey(accountKey);
                        push.setValue(account);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(WelcomeActivity.this, "Error loading your \"bank\" account", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(@NonNull FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (response == null) return;

        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;
            onGoToMainActivity();

        } else {
            if (response.getError() != null) {
                Toast.makeText(
                        this,
                        "Sign in failed " + response.getError().getErrorCode()
                                + ". " + response.getError().getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
package dev.dkqz.fundkeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
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
import java.util.concurrent.Executor;

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

        String userUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Account.accounts.orderByChild("ownerUid").equalTo(userUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account firstAccount = null;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account sp_account = ds.getValue(Account.class);

                    if (sp_account != null) {
                        if (firstAccount == null)
                            firstAccount = sp_account;

                        if (sp_account.getKey().equals(accountKey)) {
                            biometricPrompt(intent);
                            return;
                        }
                    }
                }

                if (firstAccount == null) {
                    Account account = new Account(userUid, getResources().getString(R.string.bank_account), 0);
                    DatabaseReference push = Account.accounts.push();
                    accountKey = push.getKey();
                    sharedPreferences.edit().putString("accountKey", accountKey).apply();
                    account.setKey(accountKey);
                    push.setValue(account);
                } else {
                    accountKey = firstAccount.getKey();
                    sharedPreferences.edit().putString("accountKey", accountKey).apply();
                }

                biometricPrompt(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void biometricPrompt(Intent intent) {
        Executor executor = ContextCompat.getMainExecutor(WelcomeActivity.this);
        final BiometricPrompt biometricPrompt = new BiometricPrompt(WelcomeActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_CANCELED || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    finish();
                    return;
                }
                startActivity(intent);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(intent);
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                finish();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle(getResources().getString(R.string.login))
                .setDescription(getResources().getString(R.string.use_fingerprint)).setNegativeButtonText(getResources().getString(R.string.cancel)).build();
        biometricPrompt.authenticate(promptInfo);
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
                        this, getResources().getString(R.string.error_sign_in, response.getError().getErrorCode(), response.getError().getMessage()),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
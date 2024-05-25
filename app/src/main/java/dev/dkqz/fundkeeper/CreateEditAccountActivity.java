package dev.dkqz.fundkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import models.Account;

public class CreateEditAccountActivity extends AppCompatActivity {
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_edit_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        etName = findViewById(R.id.etName);
        if (intent.getStringExtra("accountName") != null)
            etName.setText(intent.getStringExtra("accountName"));

        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        if (!intent.getBooleanExtra("isNew", true))
            btnCreateAccount.setText("Edit");

        // On click

        findViewById(R.id.btnCreateAccount).setOnClickListener(v -> {
            if (intent.getBooleanExtra("isNew", true)) {
                Account account = new Account();
                account.setName(etName.getText().toString());
                account.setOwnerUid(FirebaseAuth.getInstance().getUid());

                DatabaseReference push = Account.accounts.push();
                String accountKey = push.getKey();
                account.setKey(accountKey);
                push.setValue(account);
            } else if (intent.getStringExtra("accountKey") != null) {
                Account.accounts.child(intent.getStringExtra("accountKey") + "/name").setValue(etName.getText().toString());
            }

            finish();
        });
    }
}
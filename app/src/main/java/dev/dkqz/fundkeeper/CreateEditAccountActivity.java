package dev.dkqz.fundkeeper;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import models.Account;
import models.Transaction;

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

        etName = findViewById(R.id.etName);

        // On click

        findViewById(R.id.btnCreateAccount).setOnClickListener(v -> {
            Account account = new Account();
            account.setName(etName.getText().toString());
            account.setOwnerUid(FirebaseAuth.getInstance().getUid());

            DatabaseReference push = Account.accounts.push();
            String accountKey = push.getKey();
            account.setKey(accountKey);
            push.setValue(account);

            finish();
        });
    }
}
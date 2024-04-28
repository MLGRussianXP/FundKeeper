package dev.dkqz.fundkeeper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import models.Transaction;


public class HomeFragment extends Fragment {
    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Transaction transaction = new Transaction(
                WelcomeActivity.accountKey,
                "test",
                Transaction.TransactionType.INCOME,
                "test",
                1000,
                0,
                new ArrayList<>(Arrays.asList(Transaction.Category.CLOTHES, Transaction.Category.ENTERTAINMENT))
        );

        DatabaseReference push = Transaction.transactions.push();
        transaction.setKey(push.getKey());
        push.setValue(transaction);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
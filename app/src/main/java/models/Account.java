package models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class Account implements Serializable {
    private String key = "";
    private String ownerUid = "";

    private String name = "";
    private long balance = 0;

    public static final FirebaseDatabase db = FirebaseDatabase.getInstance();
    public static final DatabaseReference accounts = db.getReference().child("Accounts");

    public Account(String ownerUid, String name, long balance) {
        this.ownerUid = ownerUid;
        this.name = name;
        this.balance = balance;
    }

    public Account() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}

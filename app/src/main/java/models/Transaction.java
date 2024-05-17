package models;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;


public class Transaction implements Serializable {
    public enum TransactionType {
        INCOME,
        EXPENSE
    }

    public enum Category {
        FOOD,
        HOUSE,
        TRANSPORT,
        HEALTH,
        ENTERTAINMENT,
        CLOTHES,
        EDUCATION,
        CONNECTIVITY,
        TRAVEL,
        AUTOMOBILE,
        INVESTMENT,
        OTHER
    }

    private String key = "";
    private String accountKey = "";

    private String title = "";
    private TransactionType type = TransactionType.INCOME;
    private String description = "";
    private long amount = 0;
    private long date = 0;
    private ArrayList<Category> categories = new ArrayList<>();

    public static FirebaseDatabase db = FirebaseDatabase.getInstance();
    public static DatabaseReference transactions = db.getReference().child("Transactions");

    public Transaction(String ownerUid, String title, TransactionType type, String description, long amount, long date, ArrayList<Category> categories) {
        this.accountKey = ownerUid;
        this.title = title;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.categories = categories;
    }

    public Transaction() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public String toString() {
        return "Transaction{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", categories=" + categories +
                '}';
    }
}

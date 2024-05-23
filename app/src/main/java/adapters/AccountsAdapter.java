package adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import dev.dkqz.fundkeeper.CreateEditAccountActivity;
import dev.dkqz.fundkeeper.MainActivity;
import dev.dkqz.fundkeeper.R;
import dev.dkqz.fundkeeper.WelcomeActivity;
import models.Account;
import models.Transaction;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    List<Account> accounts;

    public AccountsAdapter(Context context, List<Account> accounts) {
        this.accounts = accounts;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onEditClick(Account item);
        void onDeleteClick(Account item);
    }

    @NonNull
    @Override
    public AccountsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.accounts_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsAdapter.ViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.nameView.setText(account.getName());

        // On click

        holder.bind(account, new OnItemClickListener() {
            @Override
            public void onEditClick(Account item) {
                Intent intent = new Intent(holder.itemView.getContext(), CreateEditAccountActivity.class);
                intent.putExtra("accountKey", item.getKey());
                intent.putExtra("accountName", item.getName());
                intent.putExtra("isNew", false);
                startActivity(holder.itemView.getContext(), intent, null);
            }

            @Override
            public void onDeleteClick(Account item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete " + account.getName() + "?")
                        .setMessage("All transactions will be deleted. Are you sure you want to delete " + account.getName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Transaction.transactions.orderByChild("accountKey").equalTo(account.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren())
                                        ds.getRef().removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(holder.itemView.getContext(), "Error deleting transactions", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Account.accounts.child(account.getKey()).removeValue();

                        }).setNegativeButton("No", null);
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final ImageButton editButton, deleteButton;

        ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.name);
            editButton = view.findViewById(R.id.btnEdit);
            deleteButton = view.findViewById(R.id.btnDelete);
        }

        public void bind(final Account item, final OnItemClickListener listener) {
            editButton.setOnClickListener(v -> listener.onEditClick(item));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
        }
    }
}

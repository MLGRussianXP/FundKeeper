package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.dkqz.fundkeeper.R;
import models.Account;

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
                Toast.makeText(holder.itemView.getContext(), account.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(Account item) {
                Toast.makeText(holder.itemView.getContext(), "Delete " + account.getName() + "?", Toast.LENGTH_SHORT).show();
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

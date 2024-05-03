package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.dkqz.fundkeeper.R;
import models.Transaction;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Transaction> transactions;

    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        this.transactions = transactions;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(Transaction item);
        boolean onItemLongClick(Transaction item);
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.transactions_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.nameView.setText(transaction.getTitle());
        holder.amountView.setText(String.valueOf(transaction.getAmount()));
        holder.timeView.setText(String.valueOf(transaction.getDate()));

        holder.bind(transaction, new OnItemClickListener() {
            @Override
            public void onItemClick(Transaction item) {
                Toast.makeText(holder.itemView.getContext(), transaction.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(Transaction item) {
                Toast.makeText(holder.itemView.getContext(), "Delete " + transaction.getTitle() + "?", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, amountView, timeView;

        ViewHolder(View view){
            super(view);
            nameView = view.findViewById(R.id.name);
            amountView = view.findViewById(R.id.amount);
            timeView = view.findViewById(R.id.time);
        }

        public void bind(final Transaction item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
            itemView.setOnLongClickListener(v -> listener.onItemLongClick(item));
        }
    }
}

package adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import dev.dkqz.fundkeeper.R;
import dev.dkqz.fundkeeper.TransactionActivity;
import models.Transaction;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    List<Transaction> transactions;

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

        // Date

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(transaction.getDate());

        if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR))
            holder.timeView.setText("Today, " + DateFormat.format("HH:mm", calendar));
        else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1 && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR))
            holder.timeView.setText("Yesterday, " + DateFormat.format("HH:mm", calendar));
        else
            holder.timeView.setText(DateFormat.format("HH:mm, dd.MM.yyyy", calendar));

        // Income or Expense

        if (transaction.getType() == Transaction.TransactionType.EXPENSE)
            holder.typeView.setImageResource(R.drawable.ic_expense);
        else
            holder.typeView.setImageResource(R.drawable.ic_income);

        // On click

        holder.bind(transaction, new OnItemClickListener() {
            @Override
            public void onItemClick(Transaction item) {
                Intent intent = new Intent(holder.itemView.getContext(), TransactionActivity.class);
                intent.putExtra("transactionKey", item.getKey());
                holder.itemView.getContext().startActivity(intent);
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
        final ImageView typeView;

        ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.name);
            amountView = view.findViewById(R.id.amount);
            timeView = view.findViewById(R.id.time);
            typeView = view.findViewById(R.id.ivType);
        }

        public void bind(final Transaction item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
            itemView.setOnLongClickListener(v -> listener.onItemLongClick(item));
        }
    }

    public boolean shouldDrawDivider(int position) {
        if (position == getItemCount() - 1)
            return false;

        Transaction transaction1 = transactions.get(position);
        Transaction transaction2 = transactions.get(position + 1);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(transaction1.getDate());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(transaction2.getDate());

        return calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR) || calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR);
    }
}

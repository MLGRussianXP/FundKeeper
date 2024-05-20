package adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;
import java.util.stream.Collectors;

import models.Transaction;

public class HistoryTransactionsAdapter extends TransactionsAdapter {
    public enum Period {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    public interface EmptyStateListener {
        void onEmptyStateChanged(boolean isEmpty);
    }

    private Period period;
    private final List<Transaction> originalTransactions;
    private final EmptyStateListener emptyStateListener;

    public HistoryTransactionsAdapter(Context context, List<Transaction> transactions, Period period, EmptyStateListener emptyStateListener) {
        super(context, transactions);
        this.period = period;
        this.originalTransactions = transactions;
        this.emptyStateListener = emptyStateListener;

        filterTransactionsAndNotify();
    }

    public void setPeriod(Period period) {
        if (this.period == period)
            return;

        this.period = period;
        filterTransactionsAndNotify();
    }

    private void filterTransactions() {
        long currentTimeMillis = System.currentTimeMillis();
        long cutoffTime = 0;

        switch (period) {
            case DAY:
                cutoffTime = currentTimeMillis - 24L * 60 * 60 * 1000;
                break;
            case WEEK:
                cutoffTime = currentTimeMillis - 7L * 24 * 60 * 60 * 1000;
                break;
            case MONTH:
                cutoffTime = currentTimeMillis - 30L * 24 * 60 * 60 * 1000;
                break;
            case YEAR:
                cutoffTime = currentTimeMillis - 365L * 24 * 60 * 60 * 1000;
                break;
        }

        long finalCutoffTime = cutoffTime;
        transactions = originalTransactions.stream()
                .filter(t -> t.getDate() >= finalCutoffTime)
                .collect(Collectors.toList());

        emptyStateListener.onEmptyStateChanged(transactions.isEmpty());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterTransactionsAndNotify() {
        filterTransactions();
        notifyDataSetChanged();
    }
}

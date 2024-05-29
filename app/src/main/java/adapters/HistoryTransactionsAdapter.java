package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dev.dkqz.fundkeeper.R;
import models.Transaction;

public class HistoryTransactionsAdapter extends TransactionsAdapter {
    public enum Period {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    public interface HistoryListener {
        void onEmptyStateChanged(boolean isEmpty);
    }

    private final List<String> listDay, listWeek, listMonth, listYear;

    private Period period;
    private final List<Transaction> originalTransactions;
    private final LineChart chart;
    private final HistoryListener historyListener;

    public HistoryTransactionsAdapter(Context context, List<Transaction> transactions, Period period, LineChart chart, HistoryListener historyListener) {
        super(context, transactions);
        this.period = period;
        this.originalTransactions = transactions;
        this.chart = chart;
        this.historyListener = historyListener;

        Description description = new Description();
        description.setText(context.getResources().getString(R.string.transactions));
        chart.setDescription(description);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);

        listDay = Arrays.asList("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        listWeek = Arrays.asList(context.getResources().getString(R.string.mon), context.getResources().getString(R.string.tue), context.getResources().getString(R.string.wed), context.getResources().getString(R.string.thu), context.getResources().getString(R.string.fri), context.getResources().getString(R.string.sat), context.getResources().getString(R.string.sun));
        listMonth = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31");
        listYear = Arrays.asList(context.getResources().getString(R.string.jan), context.getResources().getString(R.string.feb), context.getResources().getString(R.string.mar), context.getResources().getString(R.string.apr), context.getResources().getString(R.string.may), context.getResources().getString(R.string.jun), context.getResources().getString(R.string.jul), context.getResources().getString(R.string.aug), context.getResources().getString(R.string.sep), context.getResources().getString(R.string.oct), context.getResources().getString(R.string.nov), context.getResources().getString(R.string.dec));

        filterTransactionsAndNotify();
    }

    public void setPeriod(Period period) {
        if (this.period == period)
            return;

        this.period = period;
        filterTransactionsAndNotify();
    }

    private void filterTransactions() {
        Calendar calendar = Calendar.getInstance();
        long cutoffTime = 0;

        switch (period) {
            case DAY:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                cutoffTime = calendar.getTimeInMillis();
                break;
            case WEEK:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                cutoffTime = calendar.getTimeInMillis();
                break;
            case MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                cutoffTime = calendar.getTimeInMillis();
                break;
            case YEAR:
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                cutoffTime = calendar.getTimeInMillis();
                break;
        }

        long finalCutoffTime = cutoffTime;
        transactions = originalTransactions.stream()
                .filter(t -> t.getDate() >= finalCutoffTime)
                .collect(Collectors.toList());
    }

    private void updateChart() {
        chart.clear();

        List<String> xValues = Collections.emptyList();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        switch (period) {
            case DAY:
                xValues = listDay;
                xAxis.setAxisMinimum(0f);
                xAxis.setAxisMaximum(24f);
                break;
            case WEEK:
                xValues = listWeek;
                xAxis.setAxisMinimum(0f);
                xAxis.setAxisMaximum(7f);
                break;
            case MONTH:
                xValues = listMonth;
                xAxis.setAxisMinimum(0f);
                xAxis.setAxisMaximum(31f);
                break;
            case YEAR:
                xValues = listYear;
                xAxis.setAxisMinimum(0f);
                xAxis.setAxisMaximum(12f);
                break;
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setGranularity(1);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(transactions.stream().max(Comparator.comparingLong(Transaction::getAmount)).get().getAmount());
        yAxis.setLabelCount(5);

        List<Entry> entries = new ArrayList<>();

        for (Transaction transaction : transactions.stream().sorted(Comparator.comparingInt(transactions::indexOf).reversed()).collect(Collectors.toList())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(transaction.getDate());

            float xValue = 0;
            switch (period) {
                case DAY:
                    xValue = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60f;
                    break;
                case WEEK:
                    xValue = (calendar.get(Calendar.DAY_OF_WEEK) - 1) + calendar.get(Calendar.HOUR_OF_DAY) / 24f  + calendar.get(Calendar.MINUTE) / 24f / 60f;
                    break;
                case MONTH:
                    xValue = (calendar.get(Calendar.DAY_OF_MONTH) - 1) + calendar.get(Calendar.HOUR_OF_DAY) / 24f + calendar.get(Calendar.MINUTE) / 24f / 60f;
                    break;
                case YEAR:
                    xValue = calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) / 31f + calendar.get(Calendar.HOUR_OF_DAY) / 31f / 24f + calendar.get(Calendar.MINUTE) / 31f / 24f / 60f;
                    break;
            }
            entries.add(new Entry(xValue, transaction.getAmount()));
            System.out.println(xValue);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Transactions");
        dataSet.setLineWidth(2);
        dataSet.setColor(Color.GREEN);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterTransactionsAndNotify() {
        filterTransactions();
        notifyDataSetChanged();
        historyListener.onEmptyStateChanged(transactions.isEmpty());
        if (!transactions.isEmpty())
            updateChart();
    }
}

package com.android.personbest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerSystem;

import java.util.ArrayList;
import java.util.List;

public class FriendProgress extends AppCompatActivity {
    private static final String TAG = "Monthly Chart";
    private static final int NUM_DAYS_M = 28;
    private static ExecMode.EMode test_mode;

    private SavedDataManager savedDataManager;
    private ArrayList<String> xAxisLabel;
    private ArrayList<Pair<String, Integer>> entries;
    private String user;
    private ITimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_progress);

        Toolbar toolbar = findViewById(R.id.toolbar);

        // we testing?
        test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            savedDataManager = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            savedDataManager = new SavedDataManagerSharedPreference(this);
        }
        else {
            // set saved data manager
            savedDataManager = new SavedDataManagerFirestore(this);
        }

        // New Timer
        timer = new TimerSystem();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create legend entries, this never changes after creation
        createLegendEntries();

        // Query and create chart
        user = getIntent().getStringExtra("userId");

        queryData(user);
    }

    public void queryData(String user) {

        if(test_mode == ExecMode.EMode.DEFAULT) {
            savedDataManager.getFriendMonthlyStat(user, updateTime(), ss -> {
                List<IStatistics> stats = ss;
                ChartBuilder builder = new ChartBuilder(this);
                builder.setData(stats, NUM_DAYS_M)
                        .setXAxisLabel(xAxisLabel)
                        .setLegend(entries)
                        .useOptimalConfig()
                        .show();

            });
        }
        else {
            List<IStatistics> stats = savedDataManager.getLastWeekSteps(updateTime(), null);
            ChartBuilder builder = new ChartBuilder(this);
            builder.setData(stats, NUM_DAYS_M)
                    .setXAxisLabel(xAxisLabel)
                    .setLegend(entries)
                    .useOptimalConfig()
                    .show();
        }
    }

    private void createAxisLabels(String today) {
        // Create Axis
        xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        for(int i = 0; i < NUM_DAYS_M; ++i) {
            StringBuilder dateSB = new StringBuilder(String.valueOf(timer.getDayStampDayBefore(today, NUM_DAYS_M - i)));
            dateSB.insert(6, '/');
            xAxisLabel.add(dateSB.substring(4));
        }
    }

    private void createLegendEntries() {
        // Create legend entries
        entries = new ArrayList<>();
        entries.add(new Pair<>("Incidental Walk", Color.rgb(0, 92, 175)));
        entries.add(new Pair<>("Intentional Walk", Color.rgb(123, 144, 210)));
    }

    private String updateTime() {
        String today = timer.getTodayString();
        createAxisLabels(today);
        return today;
    }

    public void setManager(SavedDataManager manager) {
        savedDataManager = manager;
    }

    public void setTimer(ITimer tm) {
        timer = tm;
    }
}
/*
 * Created by Axel Drozdzynski
 */

package com.android.personbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetGoalActivity extends AppCompatActivity {

    Button cancelButton;
    Button acceptButton;
    Button setButton;
    EditText editText;

    TextView goalRecommandation;
    Long stepGoal = 555L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        initViews();
    }

    private void initViews() {
        cancelButton = findViewById(R.id.setup_goal_cancel_button);
        acceptButton = findViewById(R.id.set_goal_accept);
        setButton = findViewById(R.id.set_goal_set);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetGoalActivity.this.cancelPressed();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressed();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptPressed();
            }
        });
        editText = findViewById(R.id.goal_edit_text);

        goalRecommandation = findViewById(R.id.recommanded_goal);
        goalRecommandation.setText(stepGoal + "");

    }

    private void acceptPressed() {
        save(stepGoal);
    }

    private void setPressed() {
        final String steps = editText.getText().toString();
        save(Long.parseLong(steps));
    }

    private void cancelPressed() {
        finish();
    }

    public void save(Long stepNumber) {
        SharedPreferences sharedPref = getSharedPreferences("goal", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        editor.putLong("stepNumber", stepNumber);
        editor.apply();
    }

}

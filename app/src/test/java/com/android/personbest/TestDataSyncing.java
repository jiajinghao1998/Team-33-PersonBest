package com.android.personbest;

import android.app.Activity;
import android.content.Intent;

import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestDataSyncing {
    private Activity mainActivity;

    private static final String TEST_SERVICE = "TEST_SERVICE";
    private SavedDataManagerSharedPreference sd;
    @Before
    public void setUp() throws Exception {
        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new StepCounterGoogleFit(stepCountActivity);
            }
        });
        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        try {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        } catch (Exception e) {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        }
        sd = new SavedDataManagerSharedPreference(mainActivity);
        sd.setUserHeight(72, null, null);
        sd.setCurrentGoal(5000, null, null);
    }

    @Test
    public void testDataSyncing() {
        //assertEquals(5000,sd.getCurrentGoal(null));
        //assertEquals(72,sd.getUserHeight(null));
        GoogleSignIn.getLastSignedInAccount(mainActivity);
        //assertEquals(sd.getEmail(),G.getEmail());
    }
}

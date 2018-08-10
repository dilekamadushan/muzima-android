/*
 * Copyright (c) 2014 - 2018. The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 *  this code in a for-profit venture,please contact the copyright holder.
 */
package com.muzima.view.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.muzima.MuzimaApplication;
import com.muzima.R;
import com.muzima.adapters.ListAdapter;
import com.muzima.adapters.encounters.EncountersByPatientAdapter;
import com.muzima.adapters.patients.PatientAdapterHelper;
import com.muzima.api.model.Cohort;
import com.muzima.api.model.Encounter;
import com.muzima.api.model.MuzimaGeneratedReport;
import com.muzima.api.model.Patient;
import com.muzima.api.model.User;
import com.muzima.controller.CohortController;
import com.muzima.controller.MuzimaGeneratedReportController;
import com.muzima.controller.PatientController;
import com.muzima.tasks.MuzimaGeneratedReportDownloadTask;
import com.muzima.utils.Constants;
import com.muzima.utils.Fonts;
import com.muzima.view.BroadcastListenerActivity;
import com.muzima.view.encounters.EncounterSummaryActivity;
import com.muzima.view.notifications.SyncNotificationsIntent;
import com.muzima.view.patients.PatientSummaryActivity;

import java.util.ArrayList;
import java.util.List;

import static com.muzima.utils.DateUtils.getFormattedDate;
import static com.muzima.view.patients.PatientSummaryActivity.PATIENT;

public class PatientReportButtonActivity extends BroadcastListenerActivity {
    private Patient patient;
    private EncountersByPatientAdapter encountersByPatientAdapter;
    private View noDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_test_button);
        patient = (Patient) getIntent().getSerializableExtra(PATIENT);
        try {
            setupPatientMetadata();
            
        } catch (PatientController.PatientLoadException e) {
            Toast.makeText(this, getString(R.string.error_patient_fetch), Toast.LENGTH_SHORT).show();
            finish();
        }
        Toast.makeText(getApplicationContext(), "00000000000000", Toast.LENGTH_LONG).show();
    }

    private void setupPatientMetadata() throws PatientController.PatientLoadException {

        TextView patientName = (TextView) findViewById(R.id.patientName);

        patientName.setText(PatientAdapterHelper.getPatientFormattedName(patient));

        ImageView genderIcon = (ImageView) findViewById(R.id.genderImg);
        int genderDrawable = patient.getGender().equalsIgnoreCase("M") ? R.drawable.ic_male : R.drawable.ic_female;
        genderIcon.setImageDrawable(getResources().getDrawable(genderDrawable));

        TextView dob = (TextView) findViewById(R.id.dob);
        dob.setText("DOB: " + getFormattedDate(patient.getBirthdate()));

        TextView patientIdentifier = (TextView) findViewById(R.id.patientIdentifier);
        patientIdentifier.setText(patient.getIdentifier());
    }
    

    private void setupNoDataView() {

        noDataView = findViewById(R.id.no_data_layout);
        TextView noDataMsgTextView = (TextView) findViewById(R.id.no_data_msg);
        noDataMsgTextView.setText(getResources().getText(R.string.info_encounter_unavailable));
        noDataMsgTextView.setTypeface(Fonts.roboto_bold_condensed(this));
    }

    private void setupStillLoadingView() {

        noDataView = findViewById(R.id.no_data_layout);
        TextView noDataMsgTextView = (TextView) findViewById(R.id.no_data_msg);
        noDataMsgTextView.setText("Encounters are still loading");
        noDataMsgTextView.setTypeface(Fonts.roboto_bold_condensed(this));
    }
    
    
    public void downloadReport(View v) {
    
        new SyncMuzimaGeneratedReportIntent(this, patient.getUuid()).start();
    }

    
    public void downloadReports(View v){
        /*Toast.makeText(getApplicationContext(), "11111111111111111111", Toast.LENGTH_LONG).show();
        
        
        MuzimaGeneratedReportController muzimaGeneratedReportController = ((MuzimaApplication) getApplicationContext()).getMuzimaGeneratedReportController();
        
        List<MuzimaGeneratedReport> muzimaGeneratedReports = null;
        try {
            muzimaGeneratedReports = muzimaGeneratedReportController.getAllMuzimaGeneratedReportsByPatientUuid(patient.getUuid());
        }
        catch (MuzimaGeneratedReportController.MuzimaGeneratedReportException e) {
            e.printStackTrace();
        }
        if(muzimaGeneratedReports.size()==0) {
            try {
                muzimaGeneratedReports = muzimaGeneratedReportController.downloadLastPriorityMuzimaGeneratedReportByPatientUuid(patient.getUuid());
                muzimaGeneratedReportController.saveAllMuzimaGeneratedReports(muzimaGeneratedReports);
            }
            catch (MuzimaGeneratedReportController.MuzimaGeneratedReportSaveException e) {
                e.printStackTrace();
            }
            catch (MuzimaGeneratedReportController.MuzimaGeneratedReportDownloadException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), "Size is : "+muzimaGeneratedReports.size(), Toast.LENGTH_LONG).show();
        
        
        Intent intent = new Intent(getApplicationContext(), PatientReportWebActivity.class);
        
        intent.putExtra("url", "http://www.cricinfo.com");
        
        Toast.makeText(getApplicationContext(), "77777777777777777", Toast.LENGTH_LONG).show();
        startActivity(intent);*/
        new MuzimaGeneratedReportDownloadTask(getApplicationContext()).execute(patient);
    }
    @Override
    protected void onReceive(Context context, Intent intent){
      super.onReceive(context, intent);
        
        int syncType = intent.getIntExtra(Constants.DataSyncServiceConstants.SYNC_TYPE, -1);
        
        if (syncType == Constants.DataSyncServiceConstants.SYNC_MUZIMA_GENERATED_REPORTS) {
            Intent i = new Intent(getApplicationContext(), PatientReportWebActivity.class);
            intent.putExtra(PATIENT, patient);
            
            startActivity(i);
    
            i.putExtra("url", "http://www.cricinfo.com");
        }
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        

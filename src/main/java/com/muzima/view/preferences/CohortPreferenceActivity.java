package com.muzima.view.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.view.MenuItem;
import com.muzima.R;
import com.muzima.adapters.cohort.CohortPrefixPrefAdapter;
import com.muzima.adapters.cohort.PreferenceClickListener;
import com.muzima.adapters.concept.AutoCompleteCohortPrefixAdapter;
import com.muzima.api.model.Cohort;
import com.muzima.view.BaseActivity;
import com.muzima.view.HelpActivity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static com.muzima.utils.Constants.COHORT_PREFIX_PREF;
import static com.muzima.utils.Constants.COHORT_PREFIX_PREF_KEY;

public class CohortPreferenceActivity extends BaseActivity implements PreferenceClickListener {
    protected CohortPrefixPrefAdapter prefAdapter;
    private AutoCompleteCohortPrefixAdapter autoCompleteCohortPrefixAdapter;
    private AutoCompleteTextView cohortPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        ListView cohortPrefList = (ListView) findViewById(R.id.cohort_pref_list);
        prefAdapter = new CohortPrefixPrefAdapter(this, R.layout.item_preference);
        prefAdapter.setPreferenceClickListener(this);
        cohortPrefList.setEmptyView(findViewById(R.id.no_data_msg));
        cohortPrefList.setAdapter(prefAdapter);


        cohortPrefix = (AutoCompleteTextView)findViewById(R.id.prefix_add_prefix);
        autoCompleteCohortPrefixAdapter = new AutoCompleteCohortPrefixAdapter(getApplicationContext(), R.layout.item_concept_autocomplete);
        cohortPrefix.setAdapter(autoCompleteCohortPrefixAdapter);

        cohortPrefix.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                Cohort selectedCohort = (Cohort) parent.getItemAtPosition(position);
                cohortPrefix.setText(selectedCohort.getName());
            }
        });
    }

    protected int getContentView() {
        return R.layout.activity_cohort_pref;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra(HelpActivity.HELP_TYPE, HelpActivity.COHORT_PREFIX_HELP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefAdapter.reloadData();
    }

    public void addPrefix(View view) {
        String newPrefix = cohortPrefix.getText().toString();
        SharedPreferences cohortSharedPref = getSharedPreferences(COHORT_PREFIX_PREF, MODE_PRIVATE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Set<String> originalPrefixesSet = cohortSharedPref.getStringSet(COHORT_PREFIX_PREF_KEY, new HashSet<String>());
            Set<String> copiedPrefixesSet = new TreeSet<String>(new CaseInsensitiveComparator());
            copiedPrefixesSet.addAll(originalPrefixesSet);

            if (validPrefix(copiedPrefixesSet, newPrefix)) {
                copiedPrefixesSet.add(newPrefix);
                SharedPreferences.Editor editor = cohortSharedPref.edit();
                editor.putStringSet(COHORT_PREFIX_PREF_KEY, copiedPrefixesSet);
                editor.commit();
            } else {
                Toast.makeText(this, "Prefix already exists", Toast.LENGTH_SHORT).show();
            }
        } else {
            Set<String> copiedPrefixesSet = new TreeSet<String>(new CaseInsensitiveComparator());
            int index = 1;
            String cohortPrefix = cohortSharedPref.getString(COHORT_PREFIX_PREF_KEY+index, null);
            while (cohortPrefix != null){
                copiedPrefixesSet.add(cohortPrefix);
                index++;
                cohortPrefix = cohortSharedPref.getString(COHORT_PREFIX_PREF_KEY+index, null);
            }

            if (validPrefix(copiedPrefixesSet, newPrefix)) {
                copiedPrefixesSet.add(newPrefix);
                SharedPreferences.Editor editor = cohortSharedPref.edit();

                index = 1;
                for(String aCohortPrefix: copiedPrefixesSet) {
                    editor.putString(COHORT_PREFIX_PREF_KEY + index, aCohortPrefix);
                    index ++;
                }
                editor.commit();
            } else {
                Toast.makeText(this, "Prefix already exists", Toast.LENGTH_SHORT).show();
            }
        }

        prefAdapter.reloadData();
        cohortPrefix.setText("");
    }

    @Override
    public void onDeletePreferenceClick(String pref) {
        SharedPreferences cohortSharedPref = getSharedPreferences(COHORT_PREFIX_PREF, MODE_PRIVATE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Set<String> prefixes = new HashSet<String>(cohortSharedPref.getStringSet(COHORT_PREFIX_PREF_KEY, new HashSet<String>()));
            prefixes.remove(pref);

            SharedPreferences.Editor editor = cohortSharedPref.edit();
            editor.putStringSet(COHORT_PREFIX_PREF_KEY, prefixes);
            editor.commit();
        } else {
//            TODO for FROYO
        }
        prefAdapter.reloadData();
    }

    @Override
    public void onChangePreferenceClick(String pref) {
        //save prefix
        //notify list
    }

    private boolean validPrefix(Set<String> prefixes, String newPrefix) {
        return !prefixes.contains(newPrefix);
    }

    private static class CaseInsensitiveComparator implements Comparator<String>{

        @Override
        public int compare(String lhs, String rhs) {
            return lhs.toLowerCase().compareTo(rhs.toLowerCase());
        }
    }

}
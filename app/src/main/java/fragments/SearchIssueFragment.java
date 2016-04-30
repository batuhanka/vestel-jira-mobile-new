package fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class SearchIssueFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		final View rootView 		= inflater.inflate(R.layout.fragment_searched, container, false);
		final Context context		= rootView.getContext();

		new UserOptionTask(rootView, context).execute();
		new ProjectOptionTask(rootView, context).execute();
		((NavigationActivity) getActivity()).setActionBarTitle("Search For Issues");

		final AutoCompleteTextView startDateACView = (AutoCompleteTextView) rootView.findViewById(R.id.startDatePickerText);
		startDateACView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					DialogFragment datePickerFragment = new DatePickerFragment() {
						@SuppressLint("NewApi")
						@Override
						public void onDateSet(DatePicker view, int year, int month, int day) {
							Calendar c = Calendar.getInstance();
							c.set(year, month, day);
							@SuppressLint("SimpleDateFormat")
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
							startDateACView.setText(simpleDateFormat.format(c.getTime()), false);
							rootView.requestFocus();
						}
					};
					datePickerFragment.show(getActivity().getFragmentManager(), "datePicker");
				}
			}
		});

		final AutoCompleteTextView endDateACView = (AutoCompleteTextView) rootView.findViewById(R.id.endDatePickerText);
		endDateACView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					DialogFragment datePickerFragment = new DatePickerFragment() {
						@SuppressLint("NewApi")
						@Override
						public void onDateSet(DatePicker view, int year, int month, int day) {
							Calendar c = Calendar.getInstance();
							c.set(year, month, day);
							@SuppressLint("SimpleDateFormat")
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
							endDateACView.setText(simpleDateFormat.format(c.getTime()), false);
							rootView.requestFocus();
						}
					};
					datePickerFragment.show(getActivity().getFragmentManager(), "datePicker");
				}
			}
		});

		ImageView dateClearView = (ImageView) rootView.findViewById(R.id.dateClearIcon);
		dateClearView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startDateACView.setText("");
				endDateACView.setText("");
			}
		});

		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.search_issue));
		fab.setVisibility(View.VISIBLE);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				collectSearchOptions(rootView);
				Snackbar.make(view, "Switching to search for issue screen...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
			}
		});

		return rootView;
	}

	public void collectSearchOptions(View rootView){

		String SEARCH_JQL		= "http://10.108.95.25/jira/rest/api/2/search?jql=";
		String AND				= "%20AND%20";
		String EQUAL			= "%20%3D%20";
		String CONTAINS			= "%20~%20";

		AutoCompleteTextView projectView 	= (AutoCompleteTextView) rootView.findViewById(R.id.projectAutoComplete);
		AutoCompleteTextView assigneeView 	= (AutoCompleteTextView) rootView.findViewById(R.id.assigneeAutoComplete);
		AutoCompleteTextView reporterView	= (AutoCompleteTextView) rootView.findViewById(R.id.reporterAutoComplete);
		AutoCompleteTextView issueTypeView 	= (AutoCompleteTextView) rootView.findViewById(R.id.issueTypeAutoComplete);
		AutoCompleteTextView priorityView 	= (AutoCompleteTextView) rootView.findViewById(R.id.priorityAutoComplete);
		AutoCompleteTextView descriptionView= (AutoCompleteTextView) rootView.findViewById(R.id.descriptionText);
		AutoCompleteTextView summaryView 	= (AutoCompleteTextView) rootView.findViewById(R.id.summaryText);
		AutoCompleteTextView startDateView 	= (AutoCompleteTextView) rootView.findViewById(R.id.startDatePickerText);
		AutoCompleteTextView endDateView 	= (AutoCompleteTextView) rootView.findViewById(R.id.endDatePickerText);

		try{
			String project		= projectView.getText().toString();
			String assignee		= assigneeView.getText().toString();
			String reporter		= reporterView.getText().toString();
			String issueType	= issueTypeView.getText().toString();
			String priority		= priorityView.getText().toString();
			String description	= descriptionView.getText().toString();
			String summary		= summaryView.getText().toString();
			String startDate	= startDateView.getText().toString();
			String endDate		= endDateView.getText().toString();

			if(!project.isEmpty())
				SEARCH_JQL += "project"+EQUAL+project+AND;

			if(!assignee.isEmpty())
				SEARCH_JQL += "assignee"+EQUAL+assignee+AND;

			if(!reporter.isEmpty())
				SEARCH_JQL += "reporter"+EQUAL+reporter+AND;

			if(!issueType.isEmpty())
				SEARCH_JQL += "issuetype"+EQUAL+issueType+AND;

			if(!priority.isEmpty())
				SEARCH_JQL += "priority"+EQUAL+priority+AND;

			if(!description.isEmpty())
				SEARCH_JQL += "description"+CONTAINS+description+AND;

			if(!summary.isEmpty())
				SEARCH_JQL += "summary"+CONTAINS+summary+AND;

			String temp 	= SEARCH_JQL;
			int pos 		= temp.lastIndexOf(AND);
			String result	= temp.substring(0, pos);


			//TODO: resolve buggy information
			FragmentManager fragmentManager = getFragmentManager();
			Fragment searchResultsFragment  = new SearchResultsFragment();
			Bundle bundle = new Bundle();
			bundle.putString("SEARCH_URL", result);
			bundle.putString("FILTER_NAME", "Search Results");
			searchResultsFragment.setArguments(bundle);
			fragmentManager.beginTransaction().replace(R.id.contentNav, searchResultsFragment).addToBackStack("SearchResultsFragment").commit();
			fragmentManager.executePendingTransactions();


		}catch (Exception ex){	Log.e("BATU", "Search Option Empty");	}



	}

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			//blah
		}
	}

	// AsyncTask
	class ProjectOptionTask extends AsyncTask<Void, String, ArrayList<String>> {

		private View mRootView;
		private Context mContext;
		ProjectOptionTask(View rootView, Context context){
			mRootView	= rootView;
			mContext	= context;
		}
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			return  provider.getProjects();
		}

		@Override
		protected void onPostExecute(ArrayList<String> resultList) {
			super.onPostExecute(resultList);
			ArrayAdapter projectAdapter = new ArrayAdapter<>(mContext,
					android.R.layout.simple_dropdown_item_1line,
					resultList);

			AutoCompleteTextView projectACView = (AutoCompleteTextView) mRootView.findViewById(R.id.projectAutoComplete);
			projectACView.setAdapter(projectAdapter);
			projectAdapter.notifyDataSetChanged();
		}
	}


	// AsyncTask
	class UserOptionTask extends AsyncTask<Void, String, ArrayList<String>> {

		private View mRootView;
		private Context mContext;
		UserOptionTask(View rootView, Context context){
			mRootView	= rootView;
			mContext	= context;
		}
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			return  provider.getUsers();
		}

		@Override
		protected void onPostExecute(ArrayList<String> resultList) {
			super.onPostExecute(resultList);
			ArrayAdapter userAdapter = new ArrayAdapter<>(mContext,
					android.R.layout.simple_dropdown_item_1line,
					resultList);

			AutoCompleteTextView assigneeACView = (AutoCompleteTextView) mRootView.findViewById(R.id.assigneeAutoComplete);
			AutoCompleteTextView reporterACView = (AutoCompleteTextView) mRootView.findViewById(R.id.reporterAutoComplete);

			assigneeACView.setAdapter(userAdapter);
			reporterACView.setAdapter(userAdapter);
			userAdapter.notifyDataSetChanged();
		}
	}



}

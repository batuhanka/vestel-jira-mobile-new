package fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import adapter.MyArrayAdapter;
import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;

@SuppressWarnings("deprecation")
public class SearchIssueFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		final View rootView 		= inflater.inflate(R.layout.fragment_searched, container, false);
		final Context context		= rootView.getContext();

		((NavigationActivity) getActivity()).setActionBarTitle("Search For Issues");

		MyArrayAdapter projectAdapter = new MyArrayAdapter<>(context,
				android.R.layout.simple_dropdown_item_1line,
				getResources().getStringArray(R.array.project_names));

		AutoCompleteTextView projectACView = (AutoCompleteTextView) rootView.findViewById(R.id.projectAutoComplete);
		projectACView.setAdapter(projectAdapter);
		projectAdapter.notifyDataSetChanged();

		MyArrayAdapter userAdapter = new MyArrayAdapter<>(context,
				android.R.layout.simple_dropdown_item_1line,
				getResources().getStringArray(R.array.user_names));

		AutoCompleteTextView assigneeACView = (AutoCompleteTextView) rootView.findViewById(R.id.assigneeAutoComplete);
		AutoCompleteTextView reporterACView = (AutoCompleteTextView) rootView.findViewById(R.id.reporterAutoComplete);

		assigneeACView.setAdapter(userAdapter);
		reporterACView.setAdapter(userAdapter);
		userAdapter.notifyDataSetChanged();

		final AutoCompleteTextView priorityACView = (AutoCompleteTextView) rootView.findViewById(R.id.priorityAutoComplete);
		priorityACView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setItems(R.array.priority_values, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String[] priorities = getResources().getStringArray(R.array.priority_values);
							priorityACView.setText(priorities[which]);
							rootView.requestFocus();
						}
					});
					builder.create();
					builder.show();
				}
			}
		});


		final AutoCompleteTextView statusACView = (AutoCompleteTextView) rootView.findViewById(R.id.statusAutoComplete);
		statusACView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setItems(R.array.status_values, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String[] statuses = getResources().getStringArray(R.array.status_values);
							statusACView.setText(statuses[which]);
							rootView.requestFocus();
						}
					});
					builder.create();
					builder.show();
				}
			}
		});

		final AutoCompleteTextView issueTypeACView = (AutoCompleteTextView) rootView.findViewById(R.id.issueTypeAutoComplete);
		issueTypeACView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setItems(R.array.issue_type_values, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String[] issueTypes = getResources().getStringArray(R.array.issue_type_values);
							issueTypeACView.setText(issueTypes[which]);
							rootView.requestFocus();
						}
					});
					builder.create();
					builder.show();
				}
			}
		});


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
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
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
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
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

		FloatingActionMenu issueActionMenu = NavigationActivity.issueActionMenu;
		issueActionMenu.setVisibility(View.INVISIBLE);

		FloatingActionMenu sortMenu = NavigationActivity.sortMenu;
		sortMenu.setVisibility(View.INVISIBLE);

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
		String AFTER			= "%20>%3D%20";
		String BEFORE			= "%20<%3D%20";

		AutoCompleteTextView projectView 	= (AutoCompleteTextView) rootView.findViewById(R.id.projectAutoComplete);
		AutoCompleteTextView assigneeView 	= (AutoCompleteTextView) rootView.findViewById(R.id.assigneeAutoComplete);
		AutoCompleteTextView reporterView	= (AutoCompleteTextView) rootView.findViewById(R.id.reporterAutoComplete);
		AutoCompleteTextView issueTypeView 	= (AutoCompleteTextView) rootView.findViewById(R.id.issueTypeAutoComplete);
		AutoCompleteTextView priorityView 	= (AutoCompleteTextView) rootView.findViewById(R.id.priorityAutoComplete);
		AutoCompleteTextView statusView 	= (AutoCompleteTextView) rootView.findViewById(R.id.statusAutoComplete);
		AutoCompleteTextView descriptionView= (AutoCompleteTextView) rootView.findViewById(R.id.descriptionText);
		AutoCompleteTextView summaryView 	= (AutoCompleteTextView) rootView.findViewById(R.id.summaryText);
		AutoCompleteTextView startDateView 	= (AutoCompleteTextView) rootView.findViewById(R.id.startDatePickerText);
		AutoCompleteTextView endDateView 	= (AutoCompleteTextView) rootView.findViewById(R.id.endDatePickerText);

		try {
			String project 		= projectView.getText().toString();
			String assignee 	= assigneeView.getText().toString();
			String reporter 	= reporterView.getText().toString();
			String issueType 	= issueTypeView.getText().toString();
			String priority 	= priorityView.getText().toString();
			String status 		= statusView.getText().toString();
			String description 	= new String(descriptionView.getText().toString().getBytes("ISO-8859-1"), "UTF-8");
			String summary 		= new String(summaryView.getText().toString().getBytes("ISO-8859-1"), "UTF-8");
			String startDate 	= startDateView.getText().toString();
			String endDate 		= endDateView.getText().toString();

			if (!project.isEmpty()) {
				String projectKey = project.substring(project.indexOf("(") + 1, project.indexOf(")"));
				SEARCH_JQL += "project" + EQUAL + projectKey + AND;
			}
			if (!assignee.isEmpty()) {
				String username = assignee.substring(assignee.indexOf("(") + 1, assignee.indexOf(")"));
				SEARCH_JQL += "assignee" + EQUAL + username + AND;
			}

			if (!reporter.isEmpty()) {
				String username = reporter.substring(reporter.indexOf("(") + 1, reporter.indexOf(")"));
				SEARCH_JQL += "reporter" + EQUAL + username + AND;
			}
			if (!issueType.isEmpty()) {
				issueType = issueType.replace(" ","%20");
				SEARCH_JQL += "issuetype" + EQUAL + "'"+issueType+"'" + AND;
			}
			if (!priority.isEmpty()){
				SEARCH_JQL += "priority" + EQUAL + priority + AND;
			}
			if (!status.isEmpty()){
				status = status.replace(" ","%20");
				SEARCH_JQL += "status" + EQUAL + "'"+status+"'" + AND;
			}
			if(!description.isEmpty()) {
				SEARCH_JQL += "description" + CONTAINS + description + AND;
			}
			if(!summary.isEmpty()) {
				SEARCH_JQL += "summary" + CONTAINS + summary + AND;
			}
			if(!startDate.isEmpty()){
				startDate = startDate.replace("/","%2F");
				SEARCH_JQL += "created" + AFTER + "formatDate('"+startDate+"')"+ AND;
			}

			if(!endDate.isEmpty()){
				endDate = endDate.replace("/","%2F");
				SEARCH_JQL += "created" + BEFORE + "formatDate('"+endDate+"')"+ AND;
			}

			String temp 	= SEARCH_JQL;
			int pos 		= temp.lastIndexOf(AND);
			String result	= temp.substring(0, pos);

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
}

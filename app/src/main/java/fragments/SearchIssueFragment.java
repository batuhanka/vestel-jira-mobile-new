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
				Snackbar.make(view, "Switching to search for issue screen...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
			}
		});

		return rootView;
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

package fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;


import java.util.ArrayList;

import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

public class SearchIssueFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();
	ArrayList<String> assigneeItems	= new ArrayList<>();

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View rootView 				= inflater.inflate(R.layout.fragment_searched, container, false);
		final Context context		= rootView.getContext();
		new UserOptionTask(rootView, context).execute();
		new ProjectOptionTask(rootView, context).execute();

		return rootView;
	}

}

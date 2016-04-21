package fragments;

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

import java.util.ArrayList;

import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class SearchIssueFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();

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
		((NavigationActivity) getActivity()).setActionBarTitle("Search For Issues");

		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.search_issue));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Switching to search for issue screen...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
			}
		});

		return rootView;
	}

}

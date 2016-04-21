package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.LazyAdapter;
import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class ActivityStreamFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();
	private LayoutInflater mInflator;
	private boolean refreshCheck = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		this.mInflator = inflater;
		View rootView = inflater.inflate(R.layout.fragment_activities, container, false);
		final Context context = rootView.getContext();
		new ActivityStreamTask(rootView, context).execute();
		((NavigationActivity) getActivity()).setActionBarTitle("Recent Activities in JIRA");

		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				refreshCheck = true;
				Snackbar.make(view, "Refreshing recent activities in JIRA...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
				new ActivityStreamTask(view.getRootView(), context).execute();
				refreshCheck = false;
			}
		});

		return rootView;
	}


	// AsyncTask
	class ActivityStreamTask extends AsyncTask<Void, String, ArrayList<HashMap<String,String>>> {

		private View mRootView;
		private ProgressDialog loadingDialog;
		private Context mContext;
		ActivityStreamTask(View rootView, Context context){
			mRootView	= rootView;
			mContext	= context;
			loadingDialog = new ProgressDialog(mContext);
			loadingDialog.setMessage("Loading... Please Wait...");
		}
		@Override
		protected ArrayList<HashMap<String,String>> doInBackground(Void... params) {
			return  provider.getActivityStreams();
		}

		@Override
		protected void onPreExecute() {
			if(refreshCheck)
				loadingDialog.hide();
			else
				loadingDialog.show();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String,String>> resultList) {
			super.onPostExecute(resultList);
			LazyAdapter adapter 	= new LazyAdapter(mInflator, resultList);
			ListView activityList	= (ListView) mRootView.findViewById(R.id.activityStreamsList);
			activityList.setAdapter(adapter);

			//TODO: implement on click events for each item
			activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					TextView issueKeyView = (TextView) view.findViewById(R.id.issueKey);
					Toast.makeText(getActivity().getBaseContext(), issueKeyView.getText(), Toast.LENGTH_SHORT).show();
				}
			});
			adapter.notifyDataSetChanged();
			loadingDialog.hide();
			loadingDialog.dismiss();
		}
	}
}
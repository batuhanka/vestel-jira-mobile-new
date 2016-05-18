package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.LazyAdapter;
import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class ActivityStreamFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();
	private LayoutInflater mInflater;
	private boolean refreshCheck = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		this.mInflater = inflater;
		final View rootView = inflater.inflate(R.layout.fragment_activities, container, false);
		//final Context context = rootView.getContext();
		new ActivityStreamTask(rootView, rootView.getContext()).execute();
		((NavigationActivity) getActivity()).setActionBarTitle("Recent Activities in JIRA");

		FloatingActionMenu sortMenu = NavigationActivity.sortMenu;
		sortMenu.setVisibility(View.INVISIBLE);

		FloatingActionMenu issueActionMenu = NavigationActivity.issueActionMenu;
		issueActionMenu.setVisibility(View.INVISIBLE);

		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
		fab.setVisibility(View.VISIBLE);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				refreshCheck = true;
				Snackbar.make(view, "Refreshing recent activities in JIRA...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
				new ActivityStreamTask(view.getRootView(), rootView.getContext()).execute();
				refreshCheck = false;
			}
		});

		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
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
			loadingDialog.setMessage("Loading Activities... Please Wait...");
		}
		@Override
		protected ArrayList<HashMap<String,String>> doInBackground(Void... params) {
			return provider.getActivityStreams();
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
			LazyAdapter adapter 	= new LazyAdapter(mInflater, resultList);
			ListView activityList	= (ListView) mRootView.findViewById(R.id.activityStreamsList);
			activityList.setAdapter(adapter);

			activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					TextView issueKeyView   = (TextView) view.findViewById(R.id.issueKey);
					String issueKey         = issueKeyView.getText().toString();
					if(issueKey.matches("Activity Stream")){
						Toast.makeText(mRootView.getContext(), "Just an activity stream event", Toast.LENGTH_SHORT).show();
					}else {
						FragmentManager fragmentManager = getFragmentManager();
						Fragment viewIssueFragment = new ViewIssueFragment();
						Bundle bundle = new Bundle();
						bundle.putString("ISSUE_KEY", issueKey);
						viewIssueFragment.setArguments(bundle);
						fragmentManager.beginTransaction().replace(R.id.contentNav, viewIssueFragment).addToBackStack("ViewFragment").commit();
						fragmentManager.executePendingTransactions();
					}
				}
			});
			adapter.notifyDataSetChanged();
			loadingDialog.hide();
			loadingDialog.dismiss();
		}
	}
}
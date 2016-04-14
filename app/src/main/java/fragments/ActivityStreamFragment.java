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
import android.widget.ListView;

import java.util.ArrayList;

import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class ActivityStreamFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();

	// AsyncTask
	class ActivityStreamTask extends AsyncTask<Void, String, ArrayList<String>> {

		private View mRootView;
		private Context mContext;
		ActivityStreamTask(View rootView, Context context){
			mRootView	= rootView;
			mContext	= context;
		}
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			return  provider.xmlParser();
		}

		@Override
		protected void onPostExecute(ArrayList<String> resultList) {
			super.onPostExecute(resultList);
			ArrayAdapter activityAdapter = new ArrayAdapter<>(mContext,
					android.R.layout.simple_dropdown_item_1line,
					resultList);

			ListView activityStreamView = (ListView) mRootView.findViewById(R.id.activityStreamsList);

			activityStreamView.setAdapter(activityAdapter);
			activityAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View rootView 				= inflater.inflate(R.layout.content_navigation, container, false);
		final Context context		= rootView.getContext();
		new ActivityStreamTask(rootView, context).execute();

		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Refreshing recent activities in JIRA...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
			}
		});

		return rootView;
	}

}

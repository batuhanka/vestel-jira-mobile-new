package fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.IssueModel;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

public class AssignedToMeFragment extends Fragment {

	private List<IssueModel> results;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		RestConnectionProvider provider = new RestConnectionProvider();

		View rootView 							= inflater.inflate(R.layout.fragment_assigned, container, false);
		ExpandableListView elv 					= (ExpandableListView) rootView.findViewById(R.id.assigneelist);
		HashMap<String, List<String>> results   = provider.getAssignedIssues();
		List<String> headers                    = new ArrayList<String>();
		for(String str : results.keySet()){     headers.add(str);   }
		elv.setAdapter(new SavedTabsListAdapter(getActivity().getApplicationContext(), headers, results ));
		return rootView;
	}


	public class SavedTabsListAdapter extends BaseExpandableListAdapter {


		private Context _context;
		private List<String> _listDataHeader; // header titles
		// child data in format of header title, child title
		private HashMap<String, List<String>> _listDataChild;

		public SavedTabsListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
			this._context = context;
			this._listDataHeader = listDataHeader;
			this._listDataChild = listChildData;
		}

		@Override
		public Object getChild(int groupPosition, int childPosititon) {
			return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

			final String childText = (String) getChild(groupPosition, childPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_item, null);
			}

			TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
			txtListChild.setText(childText);

			txtListChild.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView textView = (TextView) v;
					Toast.makeText(getActivity().getApplicationContext(), "Item : " + textView.getText().toString(), Toast.LENGTH_SHORT).show();

				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return this._listDataHeader.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return this._listDataHeader.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			String headerTitle = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_group, null);
			}

			TextView lblListHeader  = (TextView)    convertView.findViewById(R.id.lblListHeader);
			ImageView headerImage   = (ImageView)   convertView.findViewById(R.id.headerImage);

			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(headerTitle);

			if (headerTitle.toLowerCase().matches("showstopper"))
				headerImage.setImageResource(R.drawable.showstopper);
			if(headerTitle.toLowerCase().matches("high"))
				headerImage.setImageResource(R.drawable.high);
			if(headerTitle.toLowerCase().matches("medium"))
				headerImage.setImageResource(R.drawable.medium);
			if(headerTitle.toLowerCase().matches("low"))
				headerImage.setImageResource(R.drawable.low);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}


	}
}

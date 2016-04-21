package fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ImageLoader;
import adapter.IssueModel;
import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class ReportedToMeFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RestConnectionProvider provider             = new RestConnectionProvider();
        View rootView                               = inflater.inflate(R.layout.fragment_reported, container, false);
        ExpandableListView elv                      = (ExpandableListView) rootView.findViewById(R.id.mylist);
        HashMap<String, List<IssueModel>> results   = provider.getReportedIssues();
        List<String> headers                        = new ArrayList<>();
        for(String str : results.keySet()){     headers.add(str);   }
        elv.setAdapter(new SavedTabsListAdapter(getActivity().getApplicationContext(), headers, results ));
        ((NavigationActivity) getActivity()).setActionBarTitle("Reported Issues By Me");

        FloatingActionButton fab = NavigationActivity.fab;
        fab.setImageDrawable(getResources().getDrawable(R.drawable.create));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Switching to create issue screen...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        return rootView;
    }




    public class SavedTabsListAdapter extends BaseExpandableListAdapter {


        private Context mContext;
        private List<String> mListDataHeader; // header titles
        private HashMap<String, List<IssueModel>> mListDataChild; // child data in format of header title, child title

        public SavedTabsListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<IssueModel>> listChildData) {
            this.mContext = context;
            this.mListDataHeader = listDataHeader;
            this.mListDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final IssueModel childItem = (IssueModel) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }

            final TextView keyChild = (TextView) convertView.findViewById(R.id.issueKey);
            keyChild.setText(childItem.getIssueKey());

            TextView typeChild = (TextView) convertView.findViewById(R.id.issueType);
            typeChild.setText(childItem.getIssueType());

            TextView statusChild = (TextView) convertView.findViewById(R.id.issueStatus);
            statusChild.setText(childItem.getIssueStatus());

            TextView summaryChild = (TextView) convertView.findViewById(R.id.issueSummary);
            summaryChild.setText(childItem.getIssueSummary());

            ImageView typeLogoChild = (ImageView) convertView.findViewById(R.id.issueTypeLogo);
            ImageLoader imageLoader = new ImageLoader(mContext);
            Bitmap issueTypeLogo    = imageLoader.getBitmap(childItem.getTypeIconURL());
            typeLogoChild.setImageBitmap(issueTypeLogo);

            RelativeLayout issueItemLayout = (RelativeLayout) convertView.findViewById(R.id.issueItem);
            issueItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(), "Item : " + keyChild.getText().toString(), Toast.LENGTH_SHORT).show();

                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.mListDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.mListDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
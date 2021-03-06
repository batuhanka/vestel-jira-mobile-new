package fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

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

    RestConnectionProvider provider = new RestConnectionProvider();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_reported, container, false);
        new ReportedIssuesTask(rootView, rootView.getContext()).execute("PRIORITY");
        ((NavigationActivity) getActivity()).setActionBarTitle("Reported Issues By Me");

        FloatingActionMenu issueActionMenu = NavigationActivity.issueActionMenu;
        issueActionMenu.setVisibility(View.INVISIBLE);

        final FloatingActionMenu sortMenu = NavigationActivity.sortMenu;
        sortMenu.setVisibility(View.VISIBLE);

        sortMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                int drawableId;
                if (opened) {
                    drawableId = R.drawable.plus;
                } else {
                    drawableId = R.drawable.sort;
                }
                Drawable drawable = getResources().getDrawable(drawableId);
                sortMenu.getMenuIconView().setImageDrawable(drawable);
            }
        });

        FloatingActionButton prioritySortButton = (FloatingActionButton) sortMenu.findViewById(R.id.prioritySort);
        prioritySortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportedIssuesTask(rootView, rootView.getContext()).execute("PRIORITY");
                sortMenu.close(true);
            }
        });

        FloatingActionButton dateSortButton = (FloatingActionButton) sortMenu.findViewById(R.id.dateSort);
        dateSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportedIssuesTask(rootView, rootView.getContext()).execute("DATE");
                sortMenu.close(true);
            }
        });

        FloatingActionButton statusSortButton = (FloatingActionButton) sortMenu.findViewById(R.id.statusSort);
        statusSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportedIssuesTask(rootView, rootView.getContext()).execute("STATUS");
                sortMenu.close(true);
            }
        });

        FloatingActionButton fab = NavigationActivity.fab;
        fab.setImageDrawable(getResources().getDrawable(R.drawable.create));
        fab.setVisibility(View.INVISIBLE);

        return rootView;
    }


    // AsyncTask
    class ReportedIssuesTask extends AsyncTask<String, String, HashMap<String, List<IssueModel>>> {

        private View mRootView;
        private ProgressDialog loadingDialog;
        private Context mContext;
        ReportedIssuesTask(View rootView, Context context){
            mRootView	= rootView;
            mContext	= context;
            loadingDialog = new ProgressDialog(mContext);
            loadingDialog.setMessage("Loading Issues... Please Wait...");
        }
        @Override
        protected HashMap<String, List<IssueModel>> doInBackground(String... params) {
            String sortType = params[0];
            return provider.getReportedIssues(sortType);
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }


        @Override
        protected void onPostExecute(HashMap<String, List<IssueModel>> resultList) {
            super.onPostExecute(resultList);
            ExpandableListView elv = (ExpandableListView) mRootView.findViewById(R.id.reportedList);
            List<String> headers = new ArrayList<>();
            for (String str : resultList.keySet()) {
                headers.add(str);
            }
            elv.setAdapter(new IssuesAdapter(getActivity().getApplicationContext(), headers, resultList));
            loadingDialog.hide();
            loadingDialog.dismiss();
        }
    }



    public class IssuesAdapter extends BaseExpandableListAdapter {


        private Context mContext;
        private List<String> mListDataHeader; // header titles
        private HashMap<String, List<IssueModel>> mListDataChild; // child data in format of header title, child title

        public IssuesAdapter(Context context, List<String> listDataHeader, HashMap<String, List<IssueModel>> listChildData) {
            this.mContext = context;
            this.mListDataHeader = listDataHeader;
            this.mListDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @SuppressLint("NewApi")
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
            String issueStatus      = childItem.getIssueStatus();
            switch (issueStatus){
                case "SUBMITTED":
                case "OPEN":
                case "REOPENED":
                    statusChild.setBackground(getResources().getDrawable(R.drawable.status_blue));
                    break;

                case "RESOLVED":
                case "CLOSED":
                case "CANCELED":
                case "APPROVED":
                case "DONE":
                    statusChild.setBackground(getResources().getDrawable(R.drawable.status_green));
                    break;

                default:
                    statusChild.setBackground(getResources().getDrawable(R.drawable.status_yellow));
                    break;
            }
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
                    TextView issueKeyView   = (TextView) v.findViewById(R.id.issueKey);
                    String issueKey         = issueKeyView.getText().toString();
                    FragmentManager fragmentManager = getFragmentManager();
                    Fragment viewIssueFragment = new ViewIssueFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ISSUE_KEY", issueKey);
                    viewIssueFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.contentNav, viewIssueFragment).addToBackStack("ViewFragment").commit();
                    fragmentManager.executePendingTransactions();
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
package fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import adapter.CommentAdapter;
import adapter.ImageLoader;
import adapter.ViewIssueModel;
import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class ViewIssueFragment extends Fragment {

    RestConnectionProvider provider = new RestConnectionProvider();
    String mIssueKey;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mIssueKey = bundle.getString("ISSUE_KEY");
        }
        ViewIssueModel issueItem = provider.getSingleIssueDetails(mIssueKey);

        final View rootView = inflater.inflate(R.layout.fragment_view_issue, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle("Issue Details");
        ImageLoader imageLoader = new ImageLoader(rootView.getContext());


        ImageView projectLogoView = (ImageView) rootView.findViewById(R.id.projectLogo);
        try {
            Bitmap projectBitmap = imageLoader.getBitmap(issueItem.getProjectIconURL());
            projectLogoView.setImageBitmap(provider.getResizedBitmap(projectBitmap, 120, 120));
        } catch (Exception ex) {
            projectLogoView.setImageDrawable(getResources().getDrawable(R.drawable.projects));
        }

        TextView pnameAndIssueKeyView = (TextView) rootView.findViewById(R.id.pname_and_issuekey);
        String combinedText = issueItem.getProjectName() + " / " + issueItem.getIssueKey();
        pnameAndIssueKeyView.setText(combinedText);

        TextView issueSummaryView = (TextView) rootView.findViewById(R.id.issueSummary);
        issueSummaryView.setText(issueItem.getIssueSummary());

        TextView issueTypeView = (TextView) rootView.findViewById(R.id.issueTypeValue);
        issueTypeView.setText(issueItem.getIssueType());

        ImageView issueTypeLogo = (ImageView) rootView.findViewById(R.id.issueTypeImage);
        try {
            Bitmap issueTypeBitmap = imageLoader.getBitmap(issueItem.getTypeIconURL());
            issueTypeLogo.setImageBitmap(issueTypeBitmap);
        } catch (Exception ex) {
            issueTypeLogo.setImageDrawable(getResources().getDrawable(R.drawable.recent));
        }

        TextView issueStatusView = (TextView) rootView.findViewById(R.id.issueStatusValue);
        String issueStatus = issueItem.getIssueStatus();
        switch (issueStatus.toUpperCase()) {
            case "SUBMITTED":
            case "OPEN":
            case "REOPENED":
                issueStatusView.setBackground(getResources().getDrawable(R.drawable.status_blue));
                break;

            case "RESOLVED":
            case "CLOSED":
            case "CANCELED":
            case "APPROVED":
            case "DONE":
                issueStatusView.setBackground(getResources().getDrawable(R.drawable.status_green));
                break;

            default:
                issueStatusView.setBackground(getResources().getDrawable(R.drawable.status_yellow));
                break;
        }
        issueStatusView.setText(issueItem.getIssueStatus());


        TextView issuePriorityView = (TextView) rootView.findViewById(R.id.issuePriorityValue);
        issuePriorityView.setText(issueItem.getIssuePriority());

        ImageView issuePriorityLogo = (ImageView) rootView.findViewById(R.id.issuePriorityLogo);
        if (issueItem.getIssuePriority().matches("Low"))
            issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.low));
        if (issueItem.getIssuePriority().matches("Medium"))
            issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.medium));
        if (issueItem.getIssuePriority().matches("High"))
            issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.high));
        if (issueItem.getIssuePriority().matches("Showstopper"))
            issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.showstopper));


        ImageView issueAssigneeAvatar = (ImageView) rootView.findViewById(R.id.issueAssigneeAvatar);
        try {
            Bitmap assigneeBitmap = imageLoader.getBitmap(issueItem.getAssigneeURL());
            issueAssigneeAvatar.setImageBitmap(assigneeBitmap);
        } catch (Exception ex) {
            issueAssigneeAvatar.setImageDrawable(getResources().getDrawable(R.drawable.no_avatar));
        }

        TextView issueAssigneeView = (TextView) rootView.findViewById(R.id.issueAssigneeValue);
        issueAssigneeView.setText(issueItem.getAssignee());

        ImageView issueReporterAvatar = (ImageView) rootView.findViewById(R.id.issueReporterAvatar);
        try {
            Bitmap reporterBitmap = imageLoader.getBitmap(issueItem.getReporterURL());
            issueReporterAvatar.setImageBitmap(reporterBitmap);
        } catch (Exception ex) {
            issueReporterAvatar.setImageDrawable(getResources().getDrawable(R.drawable.no_avatar));
        }

        TextView issueReporterView = (TextView) rootView.findViewById(R.id.issueReporterValue);
        issueReporterView.setText(issueItem.getReporter());

        TextView issueResolutionView = (TextView) rootView.findViewById(R.id.issueResolutionValue);
        issueResolutionView.setText(issueItem.getResolution());

        TextView issueDescriptionView = (TextView) rootView.findViewById(R.id.issueDescriptionValue);
        issueDescriptionView.setText(issueItem.getDescription());

        ListView commentsListView = (ListView) rootView.findViewById(R.id.commentsList);
        CommentAdapter adapter = new CommentAdapter(inflater, issueItem.getComments());
        commentsListView.setAdapter(adapter);
        commentsListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setListViewHeightBasedOnChildren(commentsListView);

        FloatingActionButton fab = NavigationActivity.fab;
        fab.setVisibility(View.INVISIBLE);

        return rootView;
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView
     ****/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}

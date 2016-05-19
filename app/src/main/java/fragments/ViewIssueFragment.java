package fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.HashMap;
import java.util.Map;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mIssueKey = bundle.getString("ISSUE_KEY");
        }
        final ViewIssueModel issueItem = provider.getSingleIssueDetails(mIssueKey);

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

        ImageView issueStatusLogo = (ImageView) rootView.findViewById(R.id.issueStatusImage);
        try {
            Bitmap issueStatusBitmap = imageLoader.getBitmap(issueItem.getStatusIcon());
            issueStatusLogo.setImageBitmap(issueStatusBitmap);
        } catch (Exception ex) {
            issueStatusLogo.setImageDrawable(getResources().getDrawable(R.drawable.recent));
        }

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
        if(!issueItem.getAssigneeURL().matches("Empty")) {
            Bitmap assigneeBitmap = imageLoader.getBitmap(issueItem.getAssigneeURL());
            issueAssigneeAvatar.setImageBitmap(assigneeBitmap);
        } else {
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

        final ListView commentsListView = (ListView) rootView.findViewById(R.id.commentsList);
        if(issueItem.getComments().size() > 0) {
            CommentAdapter adapter = new CommentAdapter(inflater, issueItem.getComments());
            commentsListView.setAdapter(adapter);
        }
        commentsListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        final EditText commentBody    = (EditText) rootView.findViewById(R.id.commentArea);
        Button addCommentButton = (Button) rootView.findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentBody.getText().toString();
                if (!commentText.isEmpty() && commentText.trim().length() != 0) {
                    provider.addCommentIssue(issueItem.getIssueKey(), commentText);
                    ViewIssueModel model = provider.getSingleIssueDetails(issueItem.getIssueKey());
                    commentsListView.setAdapter(new CommentAdapter(inflater, model.getComments()));
                    setListViewHeightBasedOnChildren(commentsListView);
                    Snackbar.make(rootView, "Your comment added.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    Snackbar.make(rootView, "Please write comment first!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    commentBody.getText().clear();
                }
            }
        });

        FloatingActionMenu sortMenu = NavigationActivity.sortMenu;
        sortMenu.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = NavigationActivity.fab;
        fab.setVisibility(View.INVISIBLE);

        final FloatingActionMenu issueActionMenu = NavigationActivity.issueActionMenu;
        issueActionMenu.removeAllMenuButtons();
        issueActionMenu.setVisibility(View.VISIBLE);

        issueActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                int drawableId;
                if (opened) {
                    drawableId = R.drawable.plus;
                } else {
                    drawableId = R.drawable.actions;
                }
                Drawable drawable = getResources().getDrawable(drawableId);
                issueActionMenu.getMenuIconView().setImageDrawable(drawable);
            }
        });

        HashMap<String, String> transitions = provider.possibleTransitions(issueItem.getIssueKey());
        if (transitions.size() > 0) {
            for (Map.Entry entry : transitions.entrySet()) {
                FloatingActionButton actionButton = new FloatingActionButton(rootView.getContext());
                actionButton.setLayoutParams(issueActionMenu.getLayoutParams());
                actionButton.setButtonSize(FloatingActionButton.SIZE_MINI);
                actionButton.setColorNormal(R.color.colorAccent);
                actionButton.setColorPressed(R.color.colorPrimary);
                actionButton.setLabelText((String) entry.getValue());
                actionButton.setTag(entry.getKey());
                issueActionMenu.addMenuButton(actionButton);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FloatingActionButton floatingActionButton = (FloatingActionButton) v;
                        String actionTag    = floatingActionButton.getTag().toString();
                        provider.updateIssue(issueItem.getIssueKey(), actionTag);
                        FragmentManager fragmentManager = getFragmentManager();
                        Fragment viewIssueFragment = new ViewIssueFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("ISSUE_KEY", issueItem.getIssueKey());
                        viewIssueFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.contentNav, viewIssueFragment).addToBackStack("ViewFragment").commit();
                        fragmentManager.executePendingTransactions();
                    }
                });
            }
        }else{
            issueActionMenu.hideMenu(true);
        }

        setListViewHeightBasedOnChildren(commentsListView);
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() + 1));
        listView.setLayoutParams(params);
    }

}

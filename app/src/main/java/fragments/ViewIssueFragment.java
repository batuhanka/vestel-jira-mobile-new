package fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import adapter.ImageLoader;
import adapter.ViewIssueModel;
import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class ViewIssueFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();
	String mIssueKey;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


		Bundle bundle = this.getArguments();
		if (bundle != null) {
			mIssueKey 	= bundle.getString("ISSUE_KEY");
		}
		ViewIssueModel issueItem = provider.getSingleIssueDetails(mIssueKey);

		final View rootView 		= inflater.inflate(R.layout.fragment_view_issue, container, false);
		((NavigationActivity) getActivity()).setActionBarTitle("Issue Details");
		ImageLoader imageLoader = new ImageLoader(rootView.getContext());


		ImageView projectLogoView 	= (ImageView) rootView.findViewById(R.id.projectLogo);
		Bitmap projectBitmap		= imageLoader.getBitmap(issueItem.getProjectIconURL());
		projectLogoView.setImageBitmap(projectBitmap);

		TextView pnameAndIssueKeyView 	= (TextView) rootView.findViewById(R.id.pname_and_issuekey);
		String combinedText				= issueItem.getProjectName()+" / "+issueItem.getIssueKey();
		pnameAndIssueKeyView.setText(combinedText);

		TextView issueSummaryView 		= (TextView) rootView.findViewById(R.id.issueSummary);
		issueSummaryView.setText(issueItem.getIssueSummary());

		TextView issueTypeView 			= (TextView) rootView.findViewById(R.id.issueTypeValue);
		issueTypeView.setText(issueItem.getIssueType());

		ImageView issueTypeLogo			= (ImageView) rootView.findViewById(R.id.issueTypeImage);
		Bitmap issueTypeBitmap			= imageLoader.getBitmap(issueItem.getTypeIconURL());
		issueTypeLogo.setImageBitmap(issueTypeBitmap);


		TextView issueStatusView 			= (TextView) rootView.findViewById(R.id.issueStatusValue);
		issueStatusView.setText(issueItem.getIssueStatus());

		TextView issuePriorityView 			= (TextView) rootView.findViewById(R.id.issuePriorityValue);
		issuePriorityView.setText(issueItem.getIssuePriority());

		ImageView issuePriorityLogo			= (ImageView) rootView.findViewById(R.id.issuePriorityLogo);
		if(issueItem.getIssuePriority().matches("Low"))
			issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.low));
		if(issueItem.getIssuePriority().matches("Medium"))
			issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.medium));
		if(issueItem.getIssuePriority().matches("High"))
			issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.high));
		if(issueItem.getIssuePriority().matches("Showstopper"))
			issuePriorityLogo.setImageDrawable(getResources().getDrawable(R.drawable.showstopper));


		ImageView issueAssigneeAvatar	= (ImageView) rootView.findViewById(R.id.issueAssigneeAvatar);
		Bitmap assigneeBitmap			= imageLoader.getBitmap(issueItem.getAssigneeURL());
 		issueAssigneeAvatar.setImageBitmap(assigneeBitmap);

		TextView issueAssigneeView 		= (TextView) rootView.findViewById(R.id.issueAssigneeValue);
		issueAssigneeView.setText(issueItem.getAssignee());

		ImageView issueReporterAvatar	= (ImageView) rootView.findViewById(R.id.issueReporterAvatar);
		Bitmap reporterBitmap			= imageLoader.getBitmap(issueItem.getReporterURL());
 		issueReporterAvatar.setImageBitmap(reporterBitmap);

		TextView issueReporterView 		= (TextView) rootView.findViewById(R.id.issueReporterValue);
		issueReporterView.setText(issueItem.getReporter());

		TextView issueResolutionView 	= (TextView) rootView.findViewById(R.id.issueResolutionValue);
		issueResolutionView.setText(issueItem.getResolution());

		TextView issueDescriptionView 	= (TextView) rootView.findViewById(R.id.issueDescriptionValue);
		issueDescriptionView.setText(issueItem.getDescription());


		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.edit_issue));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Switching to edit issue issue screen...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
			}
		});

		return rootView;
	}
}

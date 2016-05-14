package fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.HashMap;

import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class CreateIssueFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		final View rootView 		= inflater.inflate(R.layout.fragment_create_issue, container, false);
		final Context context		= rootView.getContext();

		((NavigationActivity) getActivity()).setActionBarTitle("Create New Issue");

		ArrayAdapter projectAdapter = new ArrayAdapter<>(context,
				android.R.layout.simple_dropdown_item_1line,
				getResources().getStringArray(R.array.project_names));

		AutoCompleteTextView projectACView= (AutoCompleteTextView) rootView.findViewById(R.id.projectCI);
		projectACView.setAdapter(projectAdapter);
		projectAdapter.notifyDataSetChanged();

		ArrayAdapter userAdapter = new ArrayAdapter<>(context,
				android.R.layout.simple_dropdown_item_1line,
				getResources().getStringArray(R.array.user_names));

		AutoCompleteTextView assigneeACView = (AutoCompleteTextView) rootView.findViewById(R.id.assigneeIC);

		assigneeACView.setAdapter(userAdapter);
		userAdapter.notifyDataSetChanged();

		final AutoCompleteTextView priorityACView = (AutoCompleteTextView) rootView.findViewById(R.id.priorityIC);
		priorityACView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setItems(R.array.priority_values, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String[] priorities = getResources().getStringArray(R.array.priority_values);
							priorityACView.setText(priorities[which]);
							rootView.requestFocus();
						}
					});
					builder.create();
					builder.show();
				}
			}
		});


		final AutoCompleteTextView issueTypeACView = (AutoCompleteTextView) rootView.findViewById(R.id.issuetypeCI);
		issueTypeACView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setItems(R.array.issue_type_values, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String[] issueTypes = getResources().getStringArray(R.array.issue_type_values);
							issueTypeACView.setText(issueTypes[which]);
							rootView.requestFocus();
						}
					});
					builder.create();
					builder.show();
				}
			}
		});


		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.plus));
		fab.setVisibility(View.VISIBLE);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				collectCreateOptions(rootView);
				Snackbar.make(view, "Issue created successfully...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});

		return rootView;
	}

	public void collectCreateOptions(View rootView){

		AutoCompleteTextView projectView 	= (AutoCompleteTextView) rootView.findViewById(R.id.projectCI);
		AutoCompleteTextView issueTypeView 	= (AutoCompleteTextView) rootView.findViewById(R.id.issuetypeCI);
		AutoCompleteTextView summaryView 	= (AutoCompleteTextView) rootView.findViewById(R.id.summaryIC);
		AutoCompleteTextView priorityView 	= (AutoCompleteTextView) rootView.findViewById(R.id.priorityIC);
		AutoCompleteTextView assigneeView 	= (AutoCompleteTextView) rootView.findViewById(R.id.assigneeIC);
		EditText descriptionView			= (EditText) rootView.findViewById(R.id.descriptionIC);
		HashMap<String, String> details		= new HashMap<>();

		try {
			String project 		= projectView.getText().toString();
			String projectKey 	= project.substring(project.indexOf("(") + 1, project.indexOf(")"));
			String issueType 	= issueTypeView.getText().toString();
			String summary 		= new String(summaryView.getText().toString().getBytes("ISO-8859-1"), "UTF-8");
			String priority 	= priorityView.getText().toString();
			String assignee 	= assigneeView.getText().toString();
			String username 	= assignee.substring(assignee.indexOf("(") + 1, assignee.indexOf(")"));
			String description 	= new String(descriptionView.getText().toString().getBytes("ISO-8859-1"), "UTF-8");

			details.put("PROJECT", projectKey);
			details.put("ISSUE_TYPE", issueType);
			details.put("SUMMARY", summary);
			details.put("PRIORITY", priority);
			details.put("ASSIGNEE", username);
			details.put("DESCRIPTION", description);

			Log.e("BATU", "MAP : " + details);
			String issueKey	= provider.createIssue(details);

			FragmentManager fragmentManager = getFragmentManager();
			Fragment viewIssueFragment  	= new ViewIssueFragment();
			Bundle bundle 					= new Bundle();
			bundle.putString("ISSUE_KEY", issueKey);
			viewIssueFragment.setArguments(bundle);
			fragmentManager.beginTransaction().replace(R.id.contentNav, viewIssueFragment).addToBackStack("ViewIssueFragment").commit();
			fragmentManager.executePendingTransactions();

		}catch (Exception ex){	Log.e("BATU", "Create Issue Option Empty");	}

	}


}

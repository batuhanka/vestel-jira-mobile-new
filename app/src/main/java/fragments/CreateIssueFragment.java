package fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.MyArrayAdapter;
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

		MyArrayAdapter projectAdapter = new MyArrayAdapter<>(context,
				android.R.layout.simple_dropdown_item_1line,
				getResources().getStringArray(R.array.project_names));

		final AutoCompleteTextView projectACView= (AutoCompleteTextView) rootView.findViewById(R.id.projectCI);
		projectACView.setAdapter(projectAdapter);
		projectAdapter.notifyDataSetChanged();

		MyArrayAdapter userAdapter = new MyArrayAdapter<>(context,
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
					String projectKey = "";
					try {
						String project = projectACView.getText().toString();
						projectKey = project.substring(project.indexOf("(") + 1, project.indexOf(")"));
					} catch (Exception ex) {
						Log.e("BATU", ex.getMessage());
					}
					if (projectKey.isEmpty()) {
						Toast.makeText(rootView.getContext(), "Project can not be empty", Toast.LENGTH_SHORT).show();
						projectACView.requestFocus();
					} else {
						ArrayList<String> results = provider.findAvailableIssueTypes(projectKey);
						final CharSequence[] issueTypes = results.toArray(new CharSequence[results.size()]);
						builder.setItems(issueTypes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								issueTypeACView.setText(issueTypes[which]);
								rootView.requestFocus();
							}
						});
						builder.create();
						builder.show();
					}
				}
			}
		});

		FloatingActionMenu issueActionMenu = NavigationActivity.issueActionMenu;
		issueActionMenu.setVisibility(View.INVISIBLE);

		FloatingActionMenu sortMenu = NavigationActivity.sortMenu;
		sortMenu.setVisibility(View.INVISIBLE);

		FloatingActionButton fab = NavigationActivity.fab;
		fab.setImageDrawable(getResources().getDrawable(R.drawable.plus));
		fab.setVisibility(View.VISIBLE);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				collectCreateOptions(rootView);
			}
		});

		return rootView;
	}

	public void collectCreateOptions(View rootView){

		final AutoCompleteTextView projectView 	= (AutoCompleteTextView) rootView.findViewById(R.id.projectCI);
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

			details.put("ISSUE_TYPE", issueType);
			details.put("SUMMARY", summary);
			details.put("PRIORITY", priority);
			details.put("ASSIGNEE", username);
			details.put("DESCRIPTION", description);
			details.put("PROJECT", projectKey);

			HashMap<String, String> results	= provider.createIssue(details);
			if(results.get("ISSUE_KEY").matches("INVALID")){
				Toast.makeText(rootView.getContext(), results.get("ERRORS"), Toast.LENGTH_LONG).show();
			}else{
				if(results.get("ERRORS").matches("NO ERROR")){
					FragmentManager fragmentManager = getFragmentManager();
					Fragment viewIssueFragment = new ViewIssueFragment();
					Bundle bundle = new Bundle();
					bundle.putString("ISSUE_KEY", results.get("ISSUE_KEY"));
					viewIssueFragment.setArguments(bundle);
					Snackbar.make(rootView, "Issue created successfully...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
					fragmentManager.beginTransaction().replace(R.id.contentNav, viewIssueFragment).addToBackStack("ViewIssueFragment").commit();
					fragmentManager.executePendingTransactions();
				}
			}

		}catch (Exception ex){	Toast.makeText(rootView.getContext(), "You must fill all fields!", Toast.LENGTH_LONG).show();	}

	}


}

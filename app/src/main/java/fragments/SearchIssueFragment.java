package fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


import java.util.ArrayList;

import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

public class SearchIssueFragment extends Fragment {

	RestConnectionProvider provider = new RestConnectionProvider();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View rootView 				= inflater.inflate(R.layout.fragment_searched, container, false);
		final Context context		= rootView.getContext();

		ArrayList<String> assigneeItems	= new ArrayList<>();
		assigneeItems.add("ASSIGNEE1");
		assigneeItems.add("ASSIGNEE2");
		assigneeItems.add("ASSIGNEE3");
		assigneeItems.add("ASSIGNEE4");

		ArrayList<String> issueTypeItems = new ArrayList<>();
		issueTypeItems.add("TYPE1");
		issueTypeItems.add("TYPE2");
		issueTypeItems.add("TYPE3");
		issueTypeItems.add("TYPE4");

		ArrayList<String> reporterItems	= new ArrayList<>();
		reporterItems.add("REPORTER1");
		reporterItems.add("REPORTER2");
		reporterItems.add("REPORTER3");
		reporterItems.add("REPORTER4");


		AutoCompleteTextView projectAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.projectAutoComplete);

		projectAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AutoCompleteTextView view 		= (AutoCompleteTextView) v;
				ArrayList<String> projectItems 	= provider.getProjects();
				ArrayAdapter projectAdapter 	= new ArrayAdapter<>(context,
																	android.R.layout.simple_dropdown_item_1line,
																	projectItems);

				view.setAdapter(projectAdapter);
			}
		});



		AutoCompleteTextView assigneeAutoCompleteTextView 	= (AutoCompleteTextView) rootView.findViewById(R.id.assigneeAutoComplete);
		ArrayAdapter assigneeAdapter 						= new ArrayAdapter<>(	context,
																				android.R.layout.simple_dropdown_item_1line,
																				assigneeItems);
		assigneeAutoCompleteTextView.setAdapter(assigneeAdapter);

		AutoCompleteTextView issueTypeAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.issueTypeAutoComplete);
		ArrayAdapter issueTypeAdapter 				= new ArrayAdapter<>(	context,
				android.R.layout.simple_dropdown_item_1line,
				issueTypeItems);
		issueTypeAutoCompleteTextView.setAdapter(issueTypeAdapter);

		AutoCompleteTextView reporterAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.reporterAutoComplete);
		ArrayAdapter reporterAdapter 				= new ArrayAdapter<>(	context,
				android.R.layout.simple_dropdown_item_1line,
				reporterItems);
		reporterAutoCompleteTextView.setAdapter(reporterAdapter);


		return rootView;
	}

}

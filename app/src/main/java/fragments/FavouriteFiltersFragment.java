package fragments;

import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import java.util.HashMap;

import adapter.FilterAdapter;
import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;


public class FavouriteFiltersFragment extends Fragment {

    RestConnectionProvider provider = new RestConnectionProvider();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView                   = inflater.inflate(R.layout.fragment_filters, container, false);
        final HashMap<String, String> filters = provider.getFavouriteFilters();
        ListView filtersListView        = (ListView) rootView.findViewById(R.id.filtersList);
        FilterAdapter adapter           = new FilterAdapter(filters);
        filtersListView.setAdapter(adapter);

        filtersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout layout = (LinearLayout) view;
                TextView textView = (TextView) layout.findViewById(R.id.filterName);
                String filterName = textView.getText().toString();
                String filterUrl = filters.get(filterName);
                FragmentManager fragmentManager = getFragmentManager();
                Fragment searchResultsFragment = new SearchResultsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("SEARCH_URL", filterUrl);
                bundle.putString("FILTER_NAME", filterName);
                searchResultsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.contentNav, searchResultsFragment).addToBackStack("SearchResultsFragment").commit();
                fragmentManager.executePendingTransactions();
            }
        });

        ((NavigationActivity) getActivity()).setActionBarTitle("My Favourite Filters");

        FloatingActionMenu actionMenu = NavigationActivity.menu;
        actionMenu.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = NavigationActivity.fab;
        fab.setVisibility(View.INVISIBLE);

        return rootView;
    }



}

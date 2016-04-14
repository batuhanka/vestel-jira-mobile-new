package navigation;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fragments.ActivityStreamFragment;
import fragments.AssignedToMeFragment;
import fragments.ReportedToMeFragment;
import fragments.SearchIssueFragment;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RestConnectionProvider provider = new RestConnectionProvider();
    public static FloatingActionButton fab;

   @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       //TODO: Remove this dummy test function after implementation
       provider.xmlParser();

        fab        = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        fab.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Switching to create issue screen...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });


        DrawerLayout drawer             = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle    = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView   = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView userAvatar            = (ImageView) navigationView.findViewById(R.id.userAvatar);
        TextView userFullNameView       = (TextView) navigationView.findViewById(R.id.userFullName);
        String userFullNameText         = provider.getUserFullName();

        Bitmap userAvatarBitmap = provider.getUserAvatar();
        Bitmap resizedAvatar    = getResizedBitmap(userAvatarBitmap, 180, 180);
        userAvatar.setImageBitmap(resizedAvatar);
        userFullNameView.setText(userFullNameText);

       FragmentManager fragmentManager = getSupportFragmentManager();
       fragmentManager.beginTransaction().add(R.id.contentNav, new ActivityStreamFragment()).addToBackStack("ActivityStreamFragment").commit();

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        // GET CURRENT SIZE
        int width = bm.getWidth();
        int height = bm.getHeight();
        // GET SCALE SIZE
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    @Override
    public void onBackPressed() {

        //TODO: Onbackpress event method must be override
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
//        int stackCount = fragmentManager.getBackStackEntryCount();
//
//        if(stackCount > 1) {
//            fragmentManager.beginTransaction().remove(fragmentManager.getFragments().get(stackCount - 1)).commit();
//            Log.e("BATU","COUNT : "+stackCount);
//            Fragment latestFragment = fragmentManager.getFragments().get(stackCount - 2);
//            Log.e("BATU", "LATEST : " + latestFragment);
//            fragmentManager.beginTransaction().replace(R.id.contentNav, latestFragment).addToBackStack(latestFragment.getTag()).commit();
//        }

       // fragmentManager.beginTransaction().add(R.id.contentNav, new ActivityStreamFragment()).addToBackStack("ActivityStreamFragment").commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id                                  = item.getItemId();
        FragmentManager fragmentManager         = getSupportFragmentManager();
        RelativeLayout container                = (RelativeLayout) findViewById(R.id.contentNav);

        if (id == R.id.nav_assigned_issues) {
            container.removeAllViews();
            fragmentManager.beginTransaction().add(R.id.contentNav, new AssignedToMeFragment()).addToBackStack("AssignedFragment").commit();
        } else if (id == R.id.nav_reported_issues) {
            container.removeAllViews();
            fragmentManager.beginTransaction().add(R.id.contentNav, new ReportedToMeFragment()).addToBackStack("ReportedFragment").commit();
        } else if (id == R.id.nav_search_issues) {
            container.removeAllViews();
            fragmentManager.beginTransaction().add(R.id.contentNav, new SearchIssueFragment()).addToBackStack("SearchFragment").commit();
        } else if (id == R.id.nav_projects) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_log_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

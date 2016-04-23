package navigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fragments.ActivityStreamFragment;
import fragments.AssignedToMeFragment;
import fragments.ReportedToMeFragment;
import fragments.SearchIssueFragment;
import login.MainActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RestConnectionProvider provider = new RestConnectionProvider();
    public static FloatingActionButton fab;
    FragmentManager fragmentManager = getSupportFragmentManager();

   @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab        = (FloatingActionButton) findViewById(R.id.fab);
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

        Bitmap userAvatarBitmap = provider.getUserAvatar(MainActivity.getmUsername());
        Bitmap resizedAvatar    = getResizedBitmap(userAvatarBitmap, 180, 180);
        userAvatar.setImageBitmap(resizedAvatar);
        userFullNameView.setText(userFullNameText);

       FragmentManager fragmentManager = getSupportFragmentManager();
       fragmentManager.beginTransaction().replace(R.id.contentNav, new ActivityStreamFragment()).addToBackStack("ActivityStreamFragment").commit();
       fragmentManager.executePendingTransactions();

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.create_issue_link) {
            Toast.makeText(this,"CREATE ISSUE",Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.search_issue_link) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new SearchIssueFragment()).addToBackStack("SearchFragment").commit();
            fragmentManager.executePendingTransactions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recent_activities) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new ActivityStreamFragment()).addToBackStack("ActivityStreamsFragment").commit();
            fragmentManager.executePendingTransactions();
        } else if (id == R.id.nav_assigned_issues) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new AssignedToMeFragment()).addToBackStack("AssignedFragment").commit();
            fragmentManager.executePendingTransactions();
        } else if (id == R.id.nav_reported_issues) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new ReportedToMeFragment()).addToBackStack("ReportedFragment").commit();
            fragmentManager.executePendingTransactions();
        } else if (id == R.id.nav_search_issues) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new SearchIssueFragment()).addToBackStack("SearchFragment").commit();
            fragmentManager.executePendingTransactions();
        } else if (id == R.id.nav_favourite_filter) {

        } else if (id == R.id.nav_projects) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_log_out) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log Out");
            builder.setMessage("Will you log out ?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

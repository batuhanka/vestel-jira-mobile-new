package navigation;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import fragments.AssignedToMeFragment;
import fragments.ReportedToMeFragment;
import fragments.SearchIssueFragment;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RestConnectionProvider provider = new RestConnectionProvider();



   @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       //TODO: Remove this dummy test function after implementation
       provider.xmlParser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            fragmentManager.beginTransaction().add(R.id.contentNav, new AssignedToMeFragment()).commit();
        } else if (id == R.id.nav_reported_issues) {
            container.removeAllViews();
            fragmentManager.beginTransaction().add(R.id.contentNav, new ReportedToMeFragment()).commit();
        } else if (id == R.id.nav_search_issues) {
            container.removeAllViews();
            fragmentManager.beginTransaction().add(R.id.contentNav, new SearchIssueFragment()).commit();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

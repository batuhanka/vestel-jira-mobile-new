package navigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fragments.ActivityStreamFragment;
import fragments.AssignedToMeFragment;
import fragments.CreateIssueFragment;
import fragments.FavouriteFiltersFragment;
import fragments.ReportedToMeFragment;
import fragments.SearchIssueFragment;
import fragments.ViewIssueFragment;
import login.MainActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;

@SuppressWarnings("deprecation")
public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RestConnectionProvider provider = new RestConnectionProvider();
    public static FloatingActionButton fab;
    public static FloatingActionMenu sortMenu;
    public static FloatingActionMenu issueActionMenu;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkVPN()) {
            setContentView(R.layout.activity_navigation);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            issueActionMenu = (FloatingActionMenu) findViewById(R.id.issueActionMenu);
            sortMenu = (FloatingActionMenu) findViewById(R.id.sortActionMenu);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Switching to create issue screen...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            Bundle extras = getIntent().getExtras();
            boolean gcm_click = extras.getBoolean("GCM_CLICKED");
            if (gcm_click) {

                String username = extras.getString("USERNAME");
                String password = extras.getString("PASSWORD");
                String issueKey = extras.getString("ISSUE_KEY");
                loginToJIRA(username, password);
                Fragment viewIssueFragment = new ViewIssueFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ISSUE_KEY", issueKey);
                viewIssueFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.contentNav, viewIssueFragment).addToBackStack("ViewFragment").commit();
                fragmentManager.executePendingTransactions();

            } else {
                fragmentManager.beginTransaction().replace(R.id.contentNav, new ActivityStreamFragment()).addToBackStack("ActivityStreamFragment").commit();
                fragmentManager.executePendingTransactions();
            }

            TextView userFullNameView = (TextView) navigationView.findViewById(R.id.userFullName);
            String userFullNameText = provider.getUserFullName();
            userFullNameView.setText(userFullNameText);

            ImageView userAvatar = (ImageView) navigationView.findViewById(R.id.userAvatar);
            Bitmap userAvatarBitmap = provider.getUserAvatar(MainActivity.getmUsername());
            Bitmap resizedAvatar = provider.getResizedBitmap(userAvatarBitmap, 180, 180);
            userAvatar.setImageBitmap(resizedAvatar);

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(NavigationActivity.this).create();
            alertDialog.setTitle("VPN Connection Required");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Please check your VPN Settings");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.net.vpn.SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        }
    }

    private boolean checkVPN() {

        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception ex) {
            Log.e("BATU", "Network List didn't received");
        }

        if (networkList.contains("tun0")) {
            return true;
        } else {
            return false;
        }

    }

    private void loginToJIRA(String username, String password) {

        String JIRA_BASE_URL = "http://10.108.95.25/jira";
        String json = "";
        InputStream is;

        try {
            HttpClient client = new DefaultHttpClient();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            HttpPost post = new HttpPost(JIRA_BASE_URL + "/rest/auth/1/session");
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            post.setHeader("Content-type", "application/json");


            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", password);

            post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            HttpResponse response = client.execute(post, httpContext);
            is = response.getEntity().getContent();

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.get("session") != null) {
                JSONObject sessionJSON = new JSONObject(jsonObject.get("session").toString());
                String JSESSION_ID = sessionJSON.get("value").toString();
                MainActivity.setJsessionId(JSESSION_ID);
                MainActivity.setmUsername(username);
            }
        } catch (Exception e) {
            Log.e("BATU", "Error converting result " + e.toString());
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
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
            fragmentManager.beginTransaction().replace(R.id.contentNav, new CreateIssueFragment()).addToBackStack("CreateIssueFragment").commit();
            fragmentManager.executePendingTransactions();
            return true;
        }

        if (id == R.id.search_issue_link) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new SearchIssueFragment()).addToBackStack("SearchFragment").commit();
            fragmentManager.executePendingTransactions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.close_message_title);
            builder.setMessage(R.string.close_message);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    NavigationActivity.this.finish();
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
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recent_activities) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new ActivityStreamFragment()).addToBackStack("ActivityStreamsFragment").commit();
            fragmentManager.executePendingTransactions();
        } else if (id == R.id.nav_create_issue) {
            fragmentManager.beginTransaction().replace(R.id.contentNav, new CreateIssueFragment()).addToBackStack("CreateIssueFragment").commit();
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
            fragmentManager.beginTransaction().replace(R.id.contentNav, new FavouriteFiltersFragment()).addToBackStack("FiltersFragment").commit();
            fragmentManager.executePendingTransactions();
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

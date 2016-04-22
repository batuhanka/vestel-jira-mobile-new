package login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import navigation.NavigationActivity;
import project.ozyegin.vestel.com.vesteljiramobile.R;
import restprovider.RestConnectionProvider;


public class MainActivity extends AppCompatActivity {

    public static String mUsername;
    public static String mPassword;
    public static String JSESSION_ID;


    public EditText mUsernameView;
    public EditText mPasswordView;
    public Button mSignInButton;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignInButton = (Button) findViewById(R.id.signin_btn);
        mUsernameView = (EditText) findViewById(R.id.username_field);
        mPasswordView = (EditText) findViewById(R.id.password_field);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    public void attemptLogin() {

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        setmUsername(username);
        setmPassword(password);
        mAuthTask = new UserLoginTask(username, password, this);
        mAuthTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressWarnings("deprecation")
    public class UserLoginTask extends AsyncTask<Void, Integer, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private ProgressDialog loadingDialog;
        private Activity mActivity;

        UserLoginTask(String username, String password, Activity activity) {
            mUsername = username;
            mPassword = password;
            mActivity = activity;
            loadingDialog = new ProgressDialog(mActivity);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setMessage("Authenticating... Please wait...");
            loadingDialog.setCancelable(true);
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int i = 0;
            while (i <= 10) {
                publishProgress(i * 4);
                i++;
            }
            checkAccount(mUsername, mPassword);
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            loadingDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loadingDialog.hide();
            loadingDialog.dismiss();
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        private void checkAccount(String username, String password) {
            String json = "";
            InputStream is;
            String errorMessage = "";

            try {
                HttpClient client = new DefaultHttpClient();
                CookieStore cookieStore = new BasicCookieStore();
                HttpContext httpContext = new BasicHttpContext();
                HttpPost post = new HttpPost("http://10.108.95.25/jira/rest/auth/1/session");
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
                try {
                    JSONArray errorMessagesArray = jsonObject.getJSONArray("errorMessages");
                    errorMessage = errorMessagesArray.getString(0);
                } catch (Exception ex) {  Log.e("BATU", ex.getMessage());   }

                if (errorMessage.matches("Login failed")) {
                    new Thread() {
                        public void run() {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    alertDialog.setTitle("Login Failed");
                                    alertDialog.setMessage("Please check your username and password");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            });
                        }
                    }.start();
                } else if (jsonObject.get("session") != null) {
                    JSONObject sessionJSON = new JSONObject(jsonObject.get("session").toString());
                    JSESSION_ID = sessionJSON.get("value").toString();
                    Intent intent = new Intent(getBaseContext(), NavigationActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                }

            } catch (Exception ignored) {
                new Thread() {
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle("Connection Refused");
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
                        });
                    }
                }.start();
            }
        }
    }

    public static String getmUsername() {
        return mUsername;
    }

    public static void setmUsername(String mUsername) {
        MainActivity.mUsername = mUsername;
    }

    public static String getmPassword() {
        return mPassword;
    }

    public static void setmPassword(String mPassword) {
        MainActivity.mPassword = mPassword;
    }

    public static String getJsessionId() {
        return JSESSION_ID;
    }

}

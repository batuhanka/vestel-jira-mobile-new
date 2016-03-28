package restprovider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import login.MainActivity;

/**
 * Created by batuhanka on 26.03.2016.
 */
public class RestConnectionProvider {

    private String mUsername    = MainActivity.getmUsername();
    private String mJsessionID  = MainActivity.getJsessionId();
    private String JIRA_BASEURL = "http://10.108.95.25/jira";

    public RestConnectionProvider(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public  HashMap<String, List<String>> getAssignedIssues(){

        HashMap<String, List<String>> issues = new HashMap<String, List<String>>();

        try {

            String assignedToMeStr          = JIRA_BASEURL+"/rest/api/2/search?jql=assignee="+mUsername+"+and+resolution=unresolved";
            URL url                         = new URL(assignedToMeStr);
            HttpURLConnection connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID=" + mJsessionID);
            connection.setDoInput(true);
            connection.connect();
            InputStream input       = connection.getInputStream();
            BufferedReader reader   = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb        = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            JSONObject jsonObject   = new JSONObject(sb.toString());
            JSONArray jsonArray     = jsonObject.getJSONArray("issues");

            for (int i = 0; i < jsonArray.length(); i++) {
                String key      = jsonArray.getJSONObject(i).get("key").toString();
                String priority = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();

                if (issues.keySet().contains(priority)) {
                    issues.get(priority).add(key);
                } else {
                    List<String> tempList = new ArrayList<String>();
                    tempList.add(key);
                    issues.put(priority, tempList);
                }
            }

        } catch (Exception ex) {    }

        return issues;
    }

    public  HashMap<String, List<String>> getReportedIssues(){

        HashMap<String, List<String>> issues = new HashMap<String, List<String>>();

        try {

            String reportedToMeStr          = JIRA_BASEURL+"/rest/api/2/search?jql=reporter="+mUsername+"+and+resolution=unresolved";
            URL url                         = new URL(reportedToMeStr);
            HttpURLConnection connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID=" + mJsessionID);
            connection.setDoInput(true);
            connection.connect();
            InputStream input       = connection.getInputStream();
            BufferedReader reader   = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb        = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            JSONObject jsonObject   = new JSONObject(sb.toString());
            JSONArray jsonArray     = jsonObject.getJSONArray("issues");

            for (int i = 0; i < jsonArray.length(); i++) {
                String key      = jsonArray.getJSONObject(i).get("key").toString();
                String priority = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();

                if (issues.keySet().contains(priority)) {
                    issues.get(priority).add(key);
                } else {
                    List<String> tempList = new ArrayList<String>();
                    tempList.add(key);
                    issues.put(priority, tempList);
                }
            }

        } catch (Exception ex) {    }

        return issues;
    }

    public Bitmap getUserAvatar() {

        Bitmap myBitmap = null;

        try {

            String userInfo = JIRA_BASEURL+"/rest/api/2/user?username="+mUsername;
            URL url                         = new URL(userInfo);
            HttpURLConnection connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID=" + mJsessionID);
            connection.setDoInput(true);
            connection.connect();
            InputStream input       = connection.getInputStream();
            BufferedReader reader   = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb        = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            JSONObject jsonObject       = new JSONObject(sb.toString());
            JSONObject avatarUrlsJSON   = new JSONObject(jsonObject.get("avatarUrls").toString());
            String src                  = avatarUrlsJSON.get("48x48").toString();

            URL url2 = new URL(src);
            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
            connection2.setRequestProperty("Cookie", "JSESSIONID="+mJsessionID);
            connection2.setDoInput(true);
            connection2.connect();
            InputStream input2 = connection2.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input2);


        } catch (Exception e) {        }

        return myBitmap;

    }

    public String getUserFullName(){

        String userFullName = "";

        try {

            String userInfo                 = JIRA_BASEURL+"/rest/api/2/user?username="+mUsername;
            URL url                         = new URL(userInfo);
            HttpURLConnection connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID=" + mJsessionID);
            connection.setDoInput(true);
            connection.connect();
            InputStream input       = connection.getInputStream();
            BufferedReader reader   = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb        = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            JSONObject jsonObject   = new JSONObject(sb.toString());
            userFullName            = new String(jsonObject.getString("displayName").getBytes("ISO-8859-1"), "UTF-8");

        } catch (Exception ex) {    }

        return userFullName;
    }

}
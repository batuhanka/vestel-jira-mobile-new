package restprovider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import login.MainActivity;
import navigation.NavigationActivity;

public class RestConnectionProvider {

    private String mUsername            = MainActivity.getmUsername();
    private String mJsessionID          = MainActivity.getJsessionId();
    private String JIRA_REST_BASE_URL   = "http://10.108.95.25/jira/rest/api/2";

    public RestConnectionProvider(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public JSONObject createRestRequest(String requestURL){

        JSONObject jsonObject = null;
        try{

            URL url                         = new URL(requestURL);
            HttpURLConnection connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID=" + mJsessionID);
            connection.setDoInput(true);
            connection.connect();
            InputStream input       = connection.getInputStream();
            BufferedReader reader   = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb        = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line).append("\n");
            }

            jsonObject   = new JSONObject(sb.toString());

        }catch (Exception ex){
            Log.e("BATU",ex.getMessage());
        }
        return jsonObject;

    }

    public  HashMap<String, List<String>> getAssignedIssues(){

        HashMap<String, List<String>> issues = new HashMap<>();

        try {

            String assignedToMeStr  = JIRA_REST_BASE_URL+"/search?jql=assignee="+mUsername+"+and+resolution=unresolved";
            JSONObject jsonObject   = createRestRequest(assignedToMeStr);
            JSONArray jsonArray     = jsonObject.getJSONArray("issues");

            for (int i = 0; i < jsonArray.length(); i++) {
                String key          = jsonArray.getJSONObject(i).get("key").toString();
                String rawSummary   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("summary");
                String summary      = new String(rawSummary.getBytes("ISO-8859-1"), "UTF-8");
                String priority     = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();

                if (issues.keySet().contains(priority)) {
                    issues.get(priority).add(key+" "+summary);
                } else {
                    List<String> tempList = new ArrayList<>();
                    tempList.add(key+" "+summary);
                    issues.put(priority, tempList);
                }
            }

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return issues;
    }

    public  HashMap<String, List<String>> getReportedIssues(){

        HashMap<String, List<String>> issues = new HashMap<>();

        try {

            String reportedToMeStr  = JIRA_REST_BASE_URL+"/search?jql=reporter="+mUsername+"+and+resolution=unresolved";
            JSONObject jsonObject   = createRestRequest(reportedToMeStr);
            JSONArray jsonArray     = jsonObject.getJSONArray("issues");

            for (int i = 0; i < jsonArray.length(); i++) {
                String key          = jsonArray.getJSONObject(i).get("key").toString();
                String rawSummary   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("summary");
                String summary      = new String(rawSummary.getBytes("ISO-8859-1"), "UTF-8");
                String priority     = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();

                if (issues.keySet().contains(priority)) {
                    issues.get(priority).add(key+" "+summary);
                } else {
                    List<String> tempList = new ArrayList<>();
                    tempList.add(key+" "+summary);
                    issues.put(priority, tempList);
                }
            }

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return issues;
    }

    public Bitmap getUserAvatar() {

        Bitmap myBitmap = null;

        try {

            String userInfo             = JIRA_REST_BASE_URL+"/user?username="+mUsername;
            JSONObject jsonObject       = createRestRequest(userInfo);
            JSONObject avatarUrlsJSON   = new JSONObject(jsonObject.get("avatarUrls").toString());
            String src                  = avatarUrlsJSON.get("48x48").toString();

            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID="+mJsessionID);
            connection.setDoInput(true);
            connection.connect();
            InputStream input   = connection.getInputStream();
            myBitmap            = BitmapFactory.decodeStream(input);


        } catch (Exception ex) {
            Log.e("BATU", ex.getMessage());
        }

        return myBitmap;

    }

    public String getUserFullName(){

        String userFullName = "";

        try {

            String userInfo         = JIRA_REST_BASE_URL+"/user?username="+mUsername;
            JSONObject jsonObject   = createRestRequest(userInfo);
            userFullName            = new String(jsonObject.getString("displayName").getBytes("ISO-8859-1"), "UTF-8");

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return userFullName;
    }


    public  ArrayList<String> getProjects(){

        ArrayList<String> projects  = new ArrayList<>();
        String getAllProjects       = JIRA_REST_BASE_URL+"/project";


        try{

            URL url                         = new URL(getAllProjects);
            HttpURLConnection connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID=" + mJsessionID);
            connection.setDoInput(true);
            connection.connect();
            InputStream input       = connection.getInputStream();
            BufferedReader reader   = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
            StringBuilder sb        = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line).append("\n");
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject object = new JSONObject(jsonArray.get(i).toString());
                projects.add(object.getString("name"));
            }

        }catch (Exception ex){
            Log.e("BATU",ex.getMessage());
        }

        return projects;
    }

    public  ArrayList<String> getUsers(){

        ArrayList<String> users     = new ArrayList<>();
        String adminJSessionID      = getAdminJSessionID();
        char[] ch = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};



            for (int index = 0; index < ch.length; index++) {
                try {
                //String getAllUsers = JIRA_REST_BASE_URL + "/group?groupname=jira-users&expand=users";
                String getAllUsers = JIRA_REST_BASE_URL + "/user/search?username="+Character.toString(ch[index]);
                URL url = new URL(getAllUsers);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Cookie", "JSESSIONID=" +adminJSessionID );
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                JSONArray usersJsonArray = new JSONArray(sb.toString());

                for (int i = 0; i < usersJsonArray.length(); i++) {
                    JSONObject object = new JSONObject(usersJsonArray.get(i).toString());
                    users.add(new String(object.getString("displayName").getBytes("ISO-8859-1"), "UTF-8"));
                }

            }catch(Exception ex){
                Log.e("BATU", ex.getMessage());
            }
        }


        return users;
    }

    public String getAdminJSessionID(){

        String ADMIN_JSESSION_ID = "";
        String json = "";
        InputStream is;

        try{
            HttpClient client 			= new DefaultHttpClient();
            CookieStore cookieStore 	= new BasicCookieStore();
            HttpContext httpContext 	= new BasicHttpContext();
            HttpPost post 				= new HttpPost("http://10.108.95.25/jira/rest/auth/1/session");
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            post.setHeader("Content-type", "application/json");

            JSONObject obj = new JSONObject();
            obj.put("username", "admin2");
            obj.put("password", "tehanu");

            post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            HttpResponse response    = client.execute(post, httpContext);
            is                      = response.getEntity().getContent();

            try {
                BufferedReader reader   = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb        = new StringBuilder();
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
            if(jsonObject.get("session") != null){

                JSONObject sessionJSON  = new JSONObject(jsonObject.get("session").toString());
                ADMIN_JSESSION_ID       = sessionJSON.get("value").toString();

            }
            else{
                Log.e("BATU", "Admin Login Failed");
            }

        }catch(Exception ignored){
            Log.e("BATU", "Please check your VPN connections");
            Log.e("BATU", ignored.toString());
        }

        return ADMIN_JSESSION_ID;
    }

}
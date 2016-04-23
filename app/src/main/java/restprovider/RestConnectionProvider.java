package restprovider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.StrictMode;
import android.util.Base64;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.IssueModel;
import adapter.ViewIssueModel;
import login.MainActivity;

@SuppressWarnings("deprecation")
public class RestConnectionProvider {

    private String mUsername            = MainActivity.getmUsername();
    private String mPassword            = MainActivity.getmPassword();
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

    public  HashMap<String, List<IssueModel>> getAssignedIssues(){

        HashMap<String, List<IssueModel>> issues = new HashMap<>();

        try {

            String assignedToMeStr  = JIRA_REST_BASE_URL+"/search?jql=assignee="+mUsername+"+and+resolution=unresolved";
            JSONObject jsonObject   = createRestRequest(assignedToMeStr);
            JSONArray jsonArray     = jsonObject.getJSONArray("issues");

            for (int i = 0; i < jsonArray.length(); i++) {
                String key          = jsonArray.getJSONObject(i).get("key").toString();
                String rawSummary   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("summary");
                String summary      = new String(rawSummary.getBytes("ISO-8859-1"), "UTF-8");
                String priority     = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();
                String status       = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("status").get("name").toString();
                String issueType    = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("issuetype").get("name").toString();
                String typeIconURL  = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("issuetype").get("iconUrl").toString();

                if (issues.keySet().contains(priority)) {
                    IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL);
                    issues.get(priority).add(issueModel);
                } else {
                    List<IssueModel> tempList = new ArrayList<>();
                    IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL);
                    tempList.add(issueModel);
                    issues.put(priority, tempList);
                }
            }

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return issues;
    }

    public  HashMap<String, List<IssueModel>> getReportedIssues(){

        HashMap<String, List<IssueModel>> issues = new HashMap<>();

        try {

            String reportedToMeStr  = JIRA_REST_BASE_URL+"/search?jql=reporter="+mUsername+"+and+resolution=unresolved";
            JSONObject jsonObject   = createRestRequest(reportedToMeStr);
            JSONArray jsonArray     = jsonObject.getJSONArray("issues");

            for (int i = 0; i < jsonArray.length(); i++) {
                String key          = jsonArray.getJSONObject(i).get("key").toString();
                String rawSummary   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("summary");
                String summary      = new String(rawSummary.getBytes("ISO-8859-1"), "UTF-8");
                String priority     = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();
                String status       = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("status").get("name").toString();
                String issueType    = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("issuetype").get("name").toString();
                String typeIconURL  = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("issuetype").get("iconUrl").toString();

                if (issues.keySet().contains(priority)) {
                    IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL);
                    issues.get(priority).add(issueModel);
                } else {
                    List<IssueModel> tempList = new ArrayList<>();
                    IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL);
                    tempList.add(issueModel);
                    issues.put(priority, tempList);
                }
            }

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return issues;
    }


    public ViewIssueModel getSingleIssueDetails(String issueKey){

        ViewIssueModel resultIssueItem = null;

        try {

            String issueURL = JIRA_REST_BASE_URL + "/issue/" + issueKey;
            JSONObject jsonObject = createRestRequest(issueURL);

            Log.e("BATU", jsonObject.toString());

            String key          = jsonObject.get("key").toString();
            String summary      = new String(jsonObject.getJSONObject("fields").getString("summary").getBytes("ISO-8859-1"), "UTF-8");
            String priority     = jsonObject.getJSONObject("fields").getJSONObject("priority").get("name").toString();
            String status       = jsonObject.getJSONObject("fields").getJSONObject("status").get("name").toString();
            String issueType    = jsonObject.getJSONObject("fields").getJSONObject("issuetype").get("name").toString();
            String typeIconURL  = jsonObject.getJSONObject("fields").getJSONObject("issuetype").get("iconUrl").toString();
            String assignee     = new String(jsonObject.getJSONObject("fields").getJSONObject("assignee").get("displayName").toString().getBytes("ISO-8859-1"), "UTF-8");
            String assigneeURL  = jsonObject.getJSONObject("fields").getJSONObject("assignee").getJSONObject("avatarUrls").get("48x48").toString();
            String reporter     = new String(jsonObject.getJSONObject("fields").getJSONObject("reporter").get("displayName").toString().getBytes("ISO-8859-1"), "UTF-8");
            String reporterURL  = jsonObject.getJSONObject("fields").getJSONObject("reporter").getJSONObject("avatarUrls").get("48x48").toString();
            String projectName  = jsonObject.getJSONObject("fields").getJSONObject("project").get("name").toString();
            String projectURL   = jsonObject.getJSONObject("fields").getJSONObject("project").getJSONObject("avatarUrls").get("48x48").toString();
            String description  = new String(jsonObject.getJSONObject("fields").getString("description").getBytes("ISO-8859-1"), "UTF-8");
            String resolution   = jsonObject.getJSONObject("fields").getString("resolution");
            if(resolution.matches("null"))
                resolution = "Unresolved";


            resultIssueItem = new ViewIssueModel(
                    key,
                    projectURL,
                    projectName,
                    summary,
                    issueType,
                    typeIconURL,
                    status,
                    priority,
                    assignee,
                    assigneeURL,
                    reporter,
                    reporterURL,
                    resolution,
                    description
            );


        } catch (Exception ex) {
            Log.e("BATU", ex.getMessage());
        }

        return resultIssueItem;
    }

    public Bitmap getUserAvatar(String username) {

        Bitmap myBitmap = null;

        try {

            String userInfo             = JIRA_REST_BASE_URL+"/user?username="+username;
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


        for (char letter : ch) {
            try {
                //String getAllUsers = JIRA_REST_BASE_URL + "/group?groupname=jira-users&expand=users";
                String getAllUsers = JIRA_REST_BASE_URL + "/user/search?username=" + Character.toString(letter);
                URL url = new URL(getAllUsers);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Cookie", "JSESSIONID=" + adminJSessionID);
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

            } catch (Exception ex) {
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

    public ArrayList<HashMap<String,String>> getActivityStreams(){

        ArrayList<HashMap<String,String>> result = new ArrayList<>();

        try {
            String activityStreams = "http://10.108.95.25/jira/activity";
            URL url = new URL(activityStreams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String userAndPassword = mUsername + ":" + mPassword;
            final String basicAuth = "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(input, "UTF-8");
            int eventType = xpp.getEventType();
            boolean entryFlag = false;
            String tagName = "";

            String displayName  = "";
            String action       = "";
            String issueKey     = "";
            String issueSummary = "";
            String userName     = "";
            String published    = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    tagName = xpp.getName();
                    if(xpp.getName().matches("entry")){
                        entryFlag = true;
                    }
                }

                if (eventType == XmlPullParser.TEXT) {
                    if(entryFlag){

                        // retrieve display name of user
                        if( tagName.matches("name")) {
                            displayName = xpp.getText();
                        }

                        // retrieve issue key and action
                        if(tagName.matches("title")){
                            if(xpp.getText().contains("<a href")){
                                String temp     = xpp.getText();
                                String[] sub    = temp.split("</a>");
                                String[] real   = sub[1].trim().split("<a");
                                action          = real[0].trim();
                            }else{
                                issueKey = xpp.getText();
                            }
                        }

                        if(tagName.matches("published")){
                            published = xpp.getText();
                        }

                        //retrieve username
                        if(tagName.matches("username")){
                            userName = xpp.getText();
                        }

                        //retrieve issue summary
                        if(tagName.matches("summary")){
                            issueSummary = xpp.getText();
                        }

                    }
                }

                if (eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().matches("entry")){
                        entryFlag = false;
                        HashMap<String,String> map = new HashMap<>();
                        map.put("ISSUE_KEY",        issueKey);
                        map.put("ISSUE_SUMMARY",    issueSummary);
                        map.put("ACTION",           action);
                        map.put("USER_NAME",        userName);
                        map.put("DISPLAY_NAME",     displayName);
                        map.put("PUBLISHED",        published);

                        result.add(map);
                    }
                }

                eventType = xpp.next();
            }

            }catch(Exception ex){  Log.e("BATU",ex.getMessage());  }

            return result;
    }


}
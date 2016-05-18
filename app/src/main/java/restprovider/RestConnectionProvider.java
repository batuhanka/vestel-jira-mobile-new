package restprovider;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import adapter.CommentModel;
import adapter.IssueModel;
import adapter.ViewIssueModel;
import login.MainActivity;

@SuppressWarnings("deprecation")
public class RestConnectionProvider {

    private String mUsername            = MainActivity.getmUsername();
    private String mJsessionID          = MainActivity.getJsessionId();
    private String JIRA_BASE_URL        = "http://10.108.95.25/jira";

    public RestConnectionProvider(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public HashMap<String, String> createIssue(HashMap<String,String> details) {

        HashMap<String, String> results = new HashMap<>();
        String requestURL = JIRA_BASE_URL + "/rest/api/2/issue";
        try {
            HttpClient client = new DefaultHttpClient();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            HttpPost post = new HttpPost(requestURL);
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            post.setHeader("Content-type", "application/json");
            post.setHeader("Cookie", "JSESSIONID=" + mJsessionID);

            JSONObject myString = new JSONObject();
            JSONObject obj = new JSONObject();

            myString.put("project", new JSONObject().put("key", details.get("PROJECT")));
            myString.put("issuetype", new JSONObject().put("name", details.get("ISSUE_TYPE")));
            myString.put("summary", details.get("SUMMARY"));
            myString.put("priority", new JSONObject().put("name", details.get("PRIORITY")));
            myString.put("assignee", new JSONObject().put("name", details.get("ASSIGNEE")));
            myString.put("description", details.get("DESCRIPTION"));

            obj.put("fields", myString);

            post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            HttpResponse response = client.execute(post, httpContext);
            InputStream is = response.getEntity().getContent();


            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            JSONObject jsonObject = new JSONObject(sb.toString());
            Log.e("BATU", "RETURN JSON : "+jsonObject);
            try {
                String errors = jsonObject.getString("errors");
                results.put("ERRORS", errors);
                if (!errors.isEmpty()) {
                    results.put("ISSUE_KEY", "INVALID");
                }
            } catch (Exception ex) {
                Log.e("BATU", ex.getMessage());
            }
            try {
                String key = jsonObject.getString("key");
                if (!key.isEmpty()){
                    results.put("ISSUE_KEY", key);
                    results.put("ERRORS", "NO ERROR");
                }
            } catch (Exception ex) {
                Log.e("BATU", ex.getMessage());
            }


        } catch (Exception ex) {
            Log.e("BATU", ex.getMessage());
        }

        return results;

    }

    public void addCommentIssue(String issueKey, String commentText){

        String requestURL = JIRA_BASE_URL + "/rest/api/2/issue/"+issueKey+"/comment";
        try {
            HttpClient client       = new DefaultHttpClient();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            HttpPost post = new HttpPost(requestURL);
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            post.setHeader("Content-type", "application/json");
            post.setHeader("Cookie", "JSESSIONID=" + mJsessionID);

           JSONObject obj = new JSONObject();
           obj.put("body", commentText);

            post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            client.execute(post, httpContext);


        } catch (Exception ex) {
            Log.e("BATU", ex.getMessage());
        }
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

    public  HashMap<String, List<IssueModel>> getAssignedIssues(String sortType){

        HashMap<String, List<IssueModel>> issues = new HashMap<>();

        try {

            String assignedToMeStr  = JIRA_BASE_URL+"/rest/api/2/search?jql=assignee="+mUsername+"+and+resolution=unresolved";
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
                String createdRaw   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("created");
                String createdDate  = normalizeDate(createdRaw);

                switch (sortType){
                    case "PRIORITY":
                        if (issues.keySet().contains(priority)) {
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            issues.get(priority).add(issueModel);
                        } else {
                            List<IssueModel> tempList = new ArrayList<>();
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            tempList.add(issueModel);
                            issues.put(priority, tempList);
                        }
                        break;

                    case "DATE":
                        if (issues.keySet().contains(createdDate)) {
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            issues.get(createdDate).add(issueModel);
                        } else {
                            List<IssueModel> tempList = new ArrayList<>();
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            tempList.add(issueModel);
                            issues.put(createdDate, tempList);
                        }
                        break;

                    case "STATUS":
                        if (issues.keySet().contains(status)) {
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            issues.get(status).add(issueModel);
                        } else {
                            List<IssueModel> tempList = new ArrayList<>();
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            tempList.add(issueModel);
                            issues.put(status, tempList);
                        }
                        break;
                }


            }

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return issues;
    }

    private String normalizeDate(String createdRaw) {
        String result   = "";
        String[] temp   = createdRaw.split("T");
        String dateText = temp[0];
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date resultDate = dateFormat.parse(dateText);
            Calendar cal    = Calendar.getInstance();
            cal.setTime(resultDate);
            String month    = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            result = month+"-"+cal.get(Calendar.YEAR);

        }catch (Exception ex){ Log.e("BATU", ex.getMessage()); }
        return result;
    }

    public  HashMap<String, List<IssueModel>> getSearchResults(String searchUrl){

        HashMap<String, List<IssueModel>> issues = new HashMap<>();

        try {

            JSONObject jsonObject   = createRestRequest(searchUrl);
            JSONArray jsonArray     = jsonObject.getJSONArray("issues");

            for (int i = 0; i < jsonArray.length(); i++) {
                String key          = jsonArray.getJSONObject(i).get("key").toString();
                String rawSummary   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("summary");
                String summary      = new String(rawSummary.getBytes("ISO-8859-1"), "UTF-8");
                String priority     = "Medium";
                try {
                    priority = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();
                }catch (Exception ex){  Log.e("BATU", ex.getMessage()); }

                String status       = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("status").get("name").toString();
                String issueType    = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("issuetype").get("name").toString();
                String typeIconURL  = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("issuetype").get("iconUrl").toString();
                String createdRaw   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("created");
                String createdDate  = normalizeDate(createdRaw);

                if (issues.keySet().contains(priority)) {
                    IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                    issues.get(priority).add(issueModel);
                } else {
                    List<IssueModel> tempList = new ArrayList<>();
                    IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                    tempList.add(issueModel);
                    issues.put(priority, tempList);
                }
            }

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return issues;
    }

    public  HashMap<String, String> getFavouriteFilters(){

        HashMap<String, String> filters = new HashMap<>();
        String requestURL               = JIRA_BASE_URL+"/rest/api/2/filter/favourite?expand";

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

            JSONArray jsonArray = new JSONArray(sb.toString());
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject   = jsonArray.getJSONObject(i);
                String filterName       = jsonObject.getString("name");
                String searchUrl        = jsonObject.getString("searchUrl");
                filters.put(filterName, searchUrl);
            }


        }catch (Exception ex){
            Log.e("BATU",ex.getMessage());
        }

        return filters;
    }

    public  HashMap<String, List<IssueModel>> getReportedIssues(String sortType){

        HashMap<String, List<IssueModel>> issues = new HashMap<>();

        try {

            String reportedToMeStr  = JIRA_BASE_URL+"/rest/api/2/search?jql=reporter="+mUsername+"+and+resolution=unresolved";
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
                String createdRaw   = jsonArray.getJSONObject(i).getJSONObject("fields").getString("created");
                String createdDate  = normalizeDate(createdRaw);


                switch (sortType){
                    case "PRIORITY":
                        if (issues.keySet().contains(priority)) {
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            issues.get(priority).add(issueModel);
                        } else {
                            List<IssueModel> tempList = new ArrayList<>();
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            tempList.add(issueModel);
                            issues.put(priority, tempList);
                        }
                        break;

                    case "DATE":
                        if (issues.keySet().contains(createdDate)) {
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            issues.get(createdDate).add(issueModel);
                        } else {
                            List<IssueModel> tempList = new ArrayList<>();
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            tempList.add(issueModel);
                            issues.put(createdDate, tempList);
                        }
                        break;

                    case "STATUS":
                        if (issues.keySet().contains(status)) {
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            issues.get(status).add(issueModel);
                        } else {
                            List<IssueModel> tempList = new ArrayList<>();
                            IssueModel issueModel = new IssueModel(key, summary, status.toUpperCase(), issueType, typeIconURL, createdDate);
                            tempList.add(issueModel);
                            issues.put(status, tempList);
                        }
                        break;
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

            String issueURL = JIRA_BASE_URL+"/rest/api/2/issue/"+issueKey;
            JSONObject jsonObject = createRestRequest(issueURL);

            String key          = jsonObject.get("key").toString();
            String summary      = new String(jsonObject.getJSONObject("fields").getString("summary").getBytes("ISO-8859-1"), "UTF-8");
            String priority     = "Medium";
            try{
                priority = jsonObject.getJSONObject("fields").getJSONObject("priority").get("name").toString();
            }catch (Exception ex){  Log.e("BATU", ex.getMessage()); }
            String status       = jsonObject.getJSONObject("fields").getJSONObject("status").get("name").toString();
            String statusIcon   = jsonObject.getJSONObject("fields").getJSONObject("status").get("iconUrl").toString();
            String issueType    = jsonObject.getJSONObject("fields").getJSONObject("issuetype").get("name").toString();
            String typeIconURL  = jsonObject.getJSONObject("fields").getJSONObject("issuetype").get("iconUrl").toString();
            String assignee     = "Unassigned";
            try {
                assignee = new String(jsonObject.getJSONObject("fields").getJSONObject("assignee").get("displayName").toString().getBytes("ISO-8859-1"), "UTF-8");
            }catch (Exception ex){  Log.e("BATU", ex.getMessage()); }
            String assigneeURL  = "Empty";
            try {
                assigneeURL = jsonObject.getJSONObject("fields").getJSONObject("assignee").getJSONObject("avatarUrls").get("48x48").toString();
            }catch (Exception ex){  Log.e("BATU", ex.getMessage()); }
            String reporter     = new String(jsonObject.getJSONObject("fields").getJSONObject("reporter").get("displayName").toString().getBytes("ISO-8859-1"), "UTF-8");
            String reporterURL  = jsonObject.getJSONObject("fields").getJSONObject("reporter").getJSONObject("avatarUrls").get("48x48").toString();
            String projectName  = jsonObject.getJSONObject("fields").getJSONObject("project").get("name").toString();
            String projectURL   = jsonObject.getJSONObject("fields").getJSONObject("project").getJSONObject("avatarUrls").get("48x48").toString();
            String description  = "";
            try {
                description = new String(jsonObject.getJSONObject("fields").getString("description").getBytes("ISO-8859-1"), "UTF-8");
            }catch (Exception ex){  Log.e("BATU",ex.getMessage());  }
            String resolution   = "Unresolved";
            try {
                resolution = jsonObject.getJSONObject("fields").getJSONObject("resolution").getString("name");
            }catch (Exception ex){  Log.e("BATU",ex.getMessage());  }

            List<CommentModel> comments = new ArrayList<>();

            JSONArray commentJSONArray = jsonObject.getJSONObject("fields").getJSONObject("comment").getJSONArray("comments");
            for(int i=0; i<commentJSONArray.length(); i++){
                JSONObject commentJSON  = commentJSONArray.getJSONObject(i);
                String commentAuthor    = new String(commentJSON.getJSONObject("updateAuthor").getString("displayName").getBytes("ISO-8859-1"), "UTF-8");
                String commentAuthorURL = commentJSON.getJSONObject("updateAuthor").getJSONObject("avatarUrls").get("48x48").toString();
                String commentBody      = new String(commentJSON.getString("body").getBytes("ISO-8859-1"), "UTF-8");
                String commentCreated   = commentJSON.getString("created");
                CommentModel commentItem = new CommentModel(commentAuthor, commentAuthorURL, commentBody, commentCreated);
                comments.add(commentItem);
            }

            resultIssueItem = new ViewIssueModel(
                    key,
                    projectURL,
                    projectName,
                    summary,
                    issueType,
                    typeIconURL,
                    status,
                    statusIcon,
                    priority,
                    assignee,
                    assigneeURL,
                    reporter,
                    reporterURL,
                    resolution,
                    description,
                    comments
            );


        } catch (Exception ex) {
            Log.e("BATU", ex.getMessage());
        }

        return resultIssueItem;
    }

    public Bitmap getUserAvatar(String username) {

        Bitmap myBitmap = null;

        try {

            String userInfo             = JIRA_BASE_URL+"/rest/api/2/user?username="+username;
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

            String userInfo         = JIRA_BASE_URL+"/rest/api/2/user?username="+mUsername;
            JSONObject jsonObject   = createRestRequest(userInfo);
            userFullName            = new String(jsonObject.getString("displayName").getBytes("ISO-8859-1"), "UTF-8");

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }

        return userFullName;
    }

    public ArrayList<HashMap<String,String>> getActivityStreams(){

        ArrayList<HashMap<String,String>> result = new ArrayList<>();

        try {
            String activityStreams = JIRA_BASE_URL+"/activity";
            URL url = new URL(activityStreams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "JSESSIONID=" + mJsessionID);
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
                                if(xpp.getText().matches("Activity Stream"))
                                    break;
                                else
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

    public ArrayList<String> findAvailableIssueTypes(String projectKey) {

        ArrayList<String> results = new ArrayList<>();
        try {

            String projectInfo      = JIRA_BASE_URL+"/rest/api/2/project/"+projectKey;
            JSONObject jsonObject   = createRestRequest(projectInfo);
            JSONArray jsonArray     = jsonObject.getJSONArray("issueTypes");
            for(int i=0; i<jsonArray.length(); i++)
                results.add(jsonArray.getJSONObject(i).getString("name"));


        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }
        Log.e("BATU", results.toString());
        return results;
    }

    public HashMap<String, String> possibleTransitions(String issueKey) {

        HashMap<String, String> transitions = new HashMap<>();
        try {

            String transitionsInfo  = JIRA_BASE_URL+"/rest/api/2/issue/"+issueKey+"/transitions";
            JSONObject jsonObject   = createRestRequest(transitionsInfo);
            JSONArray jsonArray     = jsonObject.getJSONArray("transitions");
            for (int i = 0; i < jsonArray.length(); i++) {
                String transitionName  = jsonArray.getJSONObject(i).get("name").toString();
                String transitionID    = jsonArray.getJSONObject(i).get("id").toString();
                transitions.put(transitionID, transitionName);
            }

        } catch (Exception ex) {
            Log.e("BATU",ex.getMessage());
        }
        return transitions;
    }
}
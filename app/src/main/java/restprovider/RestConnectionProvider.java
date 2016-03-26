package restprovider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

    private String mUsername = MainActivity.getmUsername();
    private String mPassword = MainActivity.getmPassword();

    public RestConnectionProvider(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public HashMap<String, List<String>> getAssignedIssues() {

        HashMap<String, List<String>> issues = new HashMap<String, List<String>>();

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
            obj.put("username", mUsername);
            obj.put("password", mPassword);

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

                String assignedToMeStr  = "http://10.108.95.25/jira/rest/api/2/search?jql=assignee="+mUsername+"+and+resolution=unresolved";
                HttpGet get 			= new HttpGet(assignedToMeStr);

                get.setHeader("Content-type", "application/json");
                get.addHeader(response.getFirstHeader("Set-Cookie"));

                HttpResponse res 		= client.execute(get, httpContext);
                InputStream inputStream	= res.getEntity().getContent();
                BufferedReader reader 	= new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb 		= new StringBuilder();
                String line 			= null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json2 = sb.toString();
                JSONObject jsonObject2  = new JSONObject(json2);
                JSONArray jsonArray     = jsonObject2.getJSONArray("issues");


                for(int i=0; i<jsonArray.length(); i++){
                    String key      = jsonArray.getJSONObject(i).get("key").toString();
                    //String id       = jsonArray.getJSONObject(i).get("id").toString();
                    String priority = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();

                    if(issues.keySet().contains(priority)){
                        issues.get(priority).add(key);
                    }else{
                        List<String> templist = new ArrayList<String>();
                        templist.add(key);
                        issues.put(priority, templist);
                    }
                }

                return issues;
            }
            else{
                Log.e("BATU", "Login Failed");
            }

        }catch(Exception ignored){	Log.e("BATU", ignored.toString()); }
        return null;
    }

    public HashMap<String, List<String>> getReportedIssues() {

        HashMap<String, List<String>> issues    = new HashMap<String, List<String>>();

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
            obj.put("username", mUsername);
            obj.put("password", mPassword);

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

                String reportedToMeStr  = "http://10.108.95.25/jira/rest/api/2/search?jql=reporter="+mUsername+"+and+resolution=unresolved";
                HttpGet get 			= new HttpGet(reportedToMeStr);

                get.setHeader("Content-type", "application/json");
                get.addHeader(response.getFirstHeader("Set-Cookie"));

                HttpResponse res 		= client.execute(get, httpContext);
                InputStream inputStream	= res.getEntity().getContent();
                BufferedReader reader 	= new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb 		= new StringBuilder();
                String line 			= null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json2 = sb.toString();
                JSONObject jsonObject2  = new JSONObject(json2);
                JSONArray jsonArray     = jsonObject2.getJSONArray("issues");


                for(int i=0; i<jsonArray.length(); i++){
                    String key      = jsonArray.getJSONObject(i).get("key").toString();
                    //String id       = jsonArray.getJSONObject(i).get("id").toString();
                    String priority = jsonArray.getJSONObject(i).getJSONObject("fields").getJSONObject("priority").get("name").toString();

                    if(issues.keySet().contains(priority)){
                        issues.get(priority).add(key);
                    }else{
                        List<String> templist = new ArrayList<String>();
                        templist.add(key);
                        issues.put(priority, templist);
                    }
                }

                return issues;
            }
            else{
                Log.e("BATU", "Login Failed");
            }

        }catch(Exception ignored){	Log.e("BATU", ignored.toString()); }
        return null;
    }

    public Bitmap getUserAvatar() {

        String json = "";
        InputStream is;
        Bitmap myBitmap = null;

        try {
            HttpClient client = new DefaultHttpClient();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            HttpPost post = new HttpPost("http://10.108.95.25/jira/rest/auth/1/session");
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            post.setHeader("Content-type", "application/json");

            JSONObject obj = new JSONObject();
            obj.put("username", mUsername);
            obj.put("password", mPassword);

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

                String userInfo = "http://10.108.95.25/jira/rest/api/2/user?username=batuhanka";
                HttpGet get = new HttpGet(userInfo);

                get.setHeader("Content-type", "application/json");
                get.addHeader(response.getFirstHeader("Set-Cookie"));

                HttpResponse res = client.execute(get, httpContext);
                InputStream inputStream = res.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                String json2 = sb.toString();
                JSONObject jsonObject2      = new JSONObject(json2);
                JSONObject avatarUrlsJSON   = new JSONObject(jsonObject2.get("avatarUrls").toString());
                String src                  = avatarUrlsJSON.get("48x48").toString();

                JSONObject sessionJSON = new JSONObject(jsonObject.get("session").toString());

                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty ("Cookie","JSESSIONID="+sessionJSON.get("value"));
                connection.setDoInput(true);
                connection.connect();
                InputStream input   = connection.getInputStream();
                myBitmap            = BitmapFactory.decodeStream(input);



            }
        } catch (Exception e) {

        }

        return myBitmap;

    }

    public String getUserFullName(){

        String userFullName = "";
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
            obj.put("username", mUsername);
            obj.put("password", mPassword);

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

                String userInfo = "http://10.108.95.25/jira/rest/api/2/user?username="+mUsername;
                HttpGet get 			= new HttpGet(userInfo);

                get.setHeader("Content-type", "application/json");
                get.addHeader(response.getFirstHeader("Set-Cookie"));

                HttpResponse res 		= client.execute(get, httpContext);
                InputStream inputStream	= res.getEntity().getContent();
                BufferedReader reader 	= new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb 		= new StringBuilder();
                String line 			= null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json2            = sb.toString();
                JSONObject jsonObject2  = new JSONObject(json2);
                userFullName            = new String(jsonObject2.getString("displayName").getBytes("ISO-8859-1"), "UTF-8");
            }
            else{
                Log.e("BATU", "Login Failed");
            }

        }catch(Exception ignored){	Log.e("BATU", ignored.toString()); }
        return userFullName;
    }

}
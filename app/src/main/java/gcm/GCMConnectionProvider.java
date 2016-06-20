package gcm;

import android.os.StrictMode;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GCMConnectionProvider {

    public GCMConnectionProvider(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void sendMessage(String message, String to){

        String API_KEY = "AIzaSyCgf8_EMe2O4tx8CFCjq-jogYDJA9GnKM8";

        try {

            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", message);
            jGcmData.put("to", to);

            // What to send in GCM message.
            jGcmData.put("data", jData);

            Log.e("BATU","DATA : "+jGcmData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key="+API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());



            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String read;
            while((read=br.readLine()) != null) {
                sb.append(read);
            }
            br.close();
            String resp = sb.toString();
            Log.e("BATU","RESP "+resp);

        } catch (Exception e) {
            Log.e("BATU", e.getMessage());
        }
    }

}

package adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import project.ozyegin.vestel.com.vesteljiramobile.R;

public class LazyAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> mDataList;
    private LayoutInflater mInflater;
    public ImageLoader imageLoader;

    public LazyAdapter(LayoutInflater inflater, ArrayList<HashMap<String, String>> dataList) {
        mInflater   = inflater;
        mDataList   = dataList;
        imageLoader = new ImageLoader(mInflater.getContext());
    }

    public int getCount() {
        return mDataList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = mInflater.inflate(R.layout.list_row, null);

        TextView issueKey       = (TextView)vi.findViewById(R.id.issueKey); // issue key
        TextView actionInfo     = (TextView)vi.findViewById(R.id.actionInfo); // action info
        TextView author         = (TextView)vi.findViewById(R.id.author); // author
        TextView issueSummary   = (TextView)vi.findViewById(R.id.issueSummary); // issue summary
        TextView publishTime    = (TextView)vi.findViewById(R.id.publishTime); // issue summary
        ImageView thumb_image   = (ImageView)vi.findViewById(R.id.list_avatar); // thumb image

        HashMap<String, String> item = mDataList.get(position);

        // Setting all values in list view
        issueKey.setText(item.get("ISSUE_KEY"));
        actionInfo.setText(item.get("ACTION"));
        author.setText(item.get("DISPLAY_NAME"));
        issueSummary.setText(item.get("ISSUE_SUMMARY"));
        publishTime.setText(calculateTime(item.get("PUBLISHED")));
        imageLoader.displayImage(item.get("USER_NAME"), thumb_image);
        return vi;
    }

    @SuppressWarnings("deprecation")
    public String calculateTime(String rawTime){

        String result   = "";
        String[] temp   = rawTime.split("T");
        String dateText = temp[0];
        String hourText = rawTime.substring(rawTime.indexOf("T")+1, rawTime.indexOf("."));
        String timeText = dateText+" "+hourText;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();

        try {
            Date resultTime = dateFormat.parse(timeText);
            resultTime.setHours(resultTime.getHours()+3);

            long diff           = currentTime.getTime() - resultTime.getTime();
            long diffMinutes    = diff / (60 * 1000) % 60;
            long diffHours      = diff / (60 * 60 * 1000);
            int diffInDays      = (int) ((resultTime.getTime() - currentTime.getTime()) / (1000 * 60 * 60 * 24));

            if(diffInDays > 0){
                result += diffInDays+" days ";
            }
            if(diffHours > 0){
                result += diffHours+" hours ";
            }
            if(diffMinutes > 0){
                if(diffMinutes == 1)
                    result += diffMinutes+" minute ";
                else
                    result += diffMinutes+" minutes ";
            }

            if(diffInDays == 0 && diffHours == 0 && diffMinutes == 0){
                result = "Just Now";
                return result;
            }

        }catch (Exception ex){ Log.e("BATU", ex.getMessage()); }
        return result+"ago";
    }

}

package adapter;

import java.util.ArrayList;
import java.util.HashMap;

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
        ImageView thumb_image   =(ImageView)vi.findViewById(R.id.list_avatar); // thumb image

        HashMap<String, String> item = mDataList.get(position);

        // Setting all values in list view
        issueKey.setText(item.get("ISSUE_KEY"));
        actionInfo.setText(item.get("ACTION"));
        author.setText(item.get("DISPLAY_NAME"));
        imageLoader.displayImage(item.get("USER_NAME"), thumb_image);
        return vi;
    }
}

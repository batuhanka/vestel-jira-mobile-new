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

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        HashMap<String, String> item = mDataList.get(position);

        // Setting all values in listview
        title.setText(item.get("ISSUE_KEY"));
        artist.setText(item.get("ISSUE_SUMMARY"));
        duration.setText(item.get("DISPLAY_NAME"));
        imageLoader.DisplayImage(item.get("USER_NAME"), thumb_image);
        return vi;
    }
}

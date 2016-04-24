package adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import project.ozyegin.vestel.com.vesteljiramobile.R;

public class CommentAdapter extends BaseAdapter {

    private List<CommentModel> mDataList;
    private LayoutInflater mInflater;


    public CommentAdapter(LayoutInflater inflater, List<CommentModel> dataList) {
        mInflater   = inflater;
        mDataList   = dataList;
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
        View vi = convertView;
        if(convertView == null)
            vi = mInflater.inflate(R.layout.comment_row, null);

        TextView author         = (TextView)vi.findViewById(R.id.authorName);       // comment author name
        ImageView authorAvatar  = (ImageView)vi.findViewById(R.id.authorAvatar);    // author avatar
        TextView commentBody    = (TextView)vi.findViewById(R.id.commentBody);      // comment body
        TextView commentTime    = (TextView)vi.findViewById(R.id.commentTime);      // comment time

        CommentModel commentItem= mDataList.get(position);

        ImageLoader imageLoader = new ImageLoader(mInflater.getContext());

        // Setting all values in list view
        author.setText(commentItem.getAuthor());
        authorAvatar.setImageBitmap(imageLoader.getBitmap(commentItem.getAuthorURL()));
        commentBody.setText(commentItem.getBody());
        commentTime.setText(calculateTime(commentItem.getCreated()));

        return vi;
    }

    @SuppressWarnings("deprecation")
    public String calculateTime(String rawTime){

        String result   = "";
        String[] temp   = rawTime.split("T");
        String dateText = temp[0];
        String hourText = rawTime.substring(rawTime.indexOf("T") + 1, rawTime.indexOf("."));
        String timeText = dateText+" "+hourText;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Format formatter    = new SimpleDateFormat("dd/MMM/yy HH:mm");
            Date resultTime     = dateFormat.parse(timeText);
            result              = formatter.format(resultTime);
        }catch (Exception ex){  }

        return result;

    }

}

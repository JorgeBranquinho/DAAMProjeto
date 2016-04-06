package almapenada.daam.utility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import almapenada.daam.R;


public class EventFeedAdapter extends BaseAdapter{

    private EventFeedAdapter self;
    private Activity activity;
    private ArrayList<Event> data;
    private static LayoutInflater inflater=null;


    public EventFeedAdapter(Activity a, ArrayList<Event> d) {
        self = this;
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data==null) return 0;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(convertView==null)
            v = inflater.inflate(R.layout.feed_event_tab, null);



        return v;
    }
}

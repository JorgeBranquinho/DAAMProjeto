package almapenada.daam.utility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;


public class EventFeedAdapter extends BaseAdapter{

    private EventFeedAdapter self;
    private Activity activity;
    private ArrayList<Event> data;
    private ArrayList<String> message;
    private static LayoutInflater inflater=null;


    public EventFeedAdapter(Activity a, ArrayList<Event> d, ArrayList<String> message) {
        self = this;
        activity = a;
        data = d;
        this.message = message;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(convertView==null)
            v = inflater.inflate(R.layout.feed_event_tab, null);

        TextView desc = (TextView) v.findViewById(R.id.feed_event_desc);

        desc.setText(message.get(position));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerActivity)activity).viewEventDetails((Event) getItem(position));
            }
        });
        return v;
    }
}

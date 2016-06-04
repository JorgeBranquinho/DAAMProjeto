package almapenada.daam.utility;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import almapenada.daam.R;


public class CustomRowAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] names;
    private Drawable[] images;
    private int friendsOrGroups;


    public CustomRowAdapter( Activity context, String[] names, Drawable[] images, int friendsOrGroups ) {
        super(context, R.layout.general_row_tab,names);
        this.context = context;
        this.names = names;
        this.images = images;
        this.friendsOrGroups = friendsOrGroups;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = null;
        rowView = inflater.inflate(R.layout.general_row_tab,null,true);


        TextView txtTitle = (TextView) rowView.findViewById(R.id.friends_list_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.friends_list_image);

        txtTitle.setText(names[position]);
        if (images != null) {
            imageView.setImageDrawable(images[position]);
        }
        return rowView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }
}

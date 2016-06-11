package almapenada.daam.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.fragments.FriendsFragment;
import almapenada.daam.fragments.FriendsListFragment;
import almapenada.daam.fragments.ProfileFragment;
import almapenada.daam.fragments.ProfileFriendFragment;


public class CustomRowAdapter extends ArrayAdapter<String> {

    private FragmentActivity activity;
    private boolean friend_group;
    private Activity context;
    private String[] names;
    private Drawable[] images;
    private CustomRowAdapter self = this;


    public CustomRowAdapter(Activity context, String[] names, Drawable[] images, boolean friend_group, FragmentActivity activity) {
        super(context, R.layout.general_row_tab,names);
        this.context = context;
        this.names = names;
        this.images = images;
        this.friend_group = friend_group;
        this.activity = activity;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = null;
        rowView = inflater.inflate(R.layout.general_row_tab,null,true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.list_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);
        final int pos = position;
        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(friend_group == true)
                    ((DrawerActivity)activity).viewFragment(new ProfileFriendFragment(), names[pos], false, -1);
                if(friend_group == false)
                    ((DrawerActivity)activity).viewFragment(new FriendsListFragment(), names[pos], true, R.drawable.plus);
            }
        });


        rowView.setOnTouchListener(new View.OnTouchListener() {
            private int list_position = position;
            private float x1, x2;
            static final int MIN_DISTANCE = 50;
            private boolean avoid_double_click = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x1 = event.getX();
                } else {
                    x2 = event.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE && !avoid_double_click) {
                        avoid_double_click = true;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("Tem a certeza que quer remover "+ names[pos] + "?")
                                .setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        avoid_double_click = false;
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                            /*Toast.makeText(activity, "Removido", Toast.LENGTH_SHORT).show();
                                            database = new EventsDatabase(activity);
                                            database.deleteById(e.getId() + 1);
                                            database.close();//para escrever as mudanças nas DB
                                            database = new EventsDatabase(activity);//reabrir ligacao
                                            database.close();*/
                                        names[list_position] = "";
                                        images[list_position] = null;
                                        self.notifyDataSetChanged();
                                        avoid_double_click = false;
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    } else {
                        //user fez tap
                    }
                }
                return false;
            }
        });



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

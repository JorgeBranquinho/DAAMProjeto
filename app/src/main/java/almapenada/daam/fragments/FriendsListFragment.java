package almapenada.daam.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import almapenada.daam.R;
import almapenada.daam.utility.CustomRowAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private ListView list_friends;
    private String[] friends_list_names = new String[10];
    private Drawable[] friends_list_images = new Drawable[10];

    public FriendsListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);


        ListView list_friends = (ListView) rootView.findViewById(R.id.friends_listview);

        for ( int i = 0 ; i < 10 ; i++ ) {
            friends_list_names[i] = "Andre " + i;
            friends_list_images[i] = rootView.getResources().getDrawable(R.drawable.user);
        }

        list_friends.setAdapter(new CustomRowAdapter(getActivity(), friends_list_names,friends_list_images ));

        return rootView;
    }

}

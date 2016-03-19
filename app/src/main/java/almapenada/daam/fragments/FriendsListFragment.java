package almapenada.daam.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import almapenada.daam.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {


    public FriendsListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_frinds_list, container, false);


        return rootView;
    }

}

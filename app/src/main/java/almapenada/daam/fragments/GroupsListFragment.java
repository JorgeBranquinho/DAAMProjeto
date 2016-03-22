package almapenada.daam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import almapenada.daam.R;

/**
 * Created by Diogo on 22/03/2016.
 */
public class GroupsListFragment extends Fragment  {
    public GroupsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        return rootView;
    }

}
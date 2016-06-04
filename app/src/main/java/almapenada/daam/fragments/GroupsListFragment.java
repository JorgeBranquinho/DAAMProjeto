package almapenada.daam.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import almapenada.daam.R;
import almapenada.daam.utility.CustomRowAdapter;

/**
 * Created by Diogo on 22/03/2016.
 */
public class GroupsListFragment extends Fragment  {

    private ListView list_groups;
    private CustomRowAdapter adapter;
    private View rootView;
    private String[] groups_list_names = new String[5];
    private Drawable[] groups_list_images = new Drawable[3];
    private String[] dummy_groups = { "Família", "Amigos Chegados", "Faculdade", "Engate" };


    public GroupsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);


        final ListView list_groups = (ListView) rootView.findViewById(R.id.group_listview);

        for ( int i = 0 ; i < dummy_groups.length ; i++ ) {
            groups_list_images[i] = rootView.getResources().getDrawable(R.drawable.user);
        }

        adapter = new CustomRowAdapter(getActivity(), dummy_groups, groups_list_images, 1);
        list_groups.setAdapter(adapter);

        list_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Hello " , Toast.LENGTH_LONG).show();
            }
        });


        return rootView;




    }


}
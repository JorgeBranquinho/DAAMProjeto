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

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.utility.CustomRowAdapter;
import almapenada.daam.utility.User;

/**
 */
public class GroupsListFragment extends Fragment  {

    private ListView list_groups;
    private CustomRowAdapter adapter;
    private View rootView;
    //private String[] groups_list_names = new String[3];
    //private Drawable[] groups_list_images = new Drawable[3];
    //private String[] dummy_groups = { "Família", "Amigos", "Faculdade"};
    private User[] dummy_users;

    public GroupsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_groups_list, container, false);

        final ListView list_groups = (ListView) rootView.findViewById(R.id.groups_listview);

        createDummys();

        adapter = new CustomRowAdapter(getActivity(), dummy_users, false, getActivity());
        list_groups.setAdapter(adapter);

        /*list_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((DrawerActivity)(getActivity())).viewMyProfile(adapter.getItem(position));
                //Toast.makeText(getContext(), groups_list_names[position], Toast.LENGTH_LONG).show();
            }
        });*/


        return rootView;

    }

    private void createDummys(){
        dummy_users=new User[3];
        dummy_users[0]=new User();
        dummy_users[0].setFirstName("Família");
        dummy_users[0].setPictureDrawable(rootView.getResources().getDrawable(R.drawable.user));
        dummy_users[1]=new User();
        dummy_users[1].setFirstName("Amigos");
        dummy_users[1].setPictureDrawable(rootView.getResources().getDrawable(R.drawable.user));
        dummy_users[2]=new User();
        dummy_users[2].setFirstName("Faculdade");
        dummy_users[2].setPictureDrawable(rootView.getResources().getDrawable(R.drawable.user));
    }

}
package almapenada.daam.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import almapenada.daam.R;
import almapenada.daam.utility.CustomRowAdapter;

/**
 */
public class GroupsListFragment extends Fragment  {

    private ListView list_friends;
    private CustomRowAdapter adapter;
    private View rootView;
    private EditText inputSearch;
    private String[] groups_list_names = new String[3];
    private Drawable[] groups_list_images = new Drawable[3];
    private String[] dummy_groups = { "Família", "Amigos", "Faculdade"};


    public GroupsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_groups_list, container, false);

        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        final ListView list_groups = (ListView) rootView.findViewById(R.id.groups_listview);

        for ( int i = 0 ; i < dummy_groups.length ; i++ ) {
            groups_list_images[i] = rootView.getResources().getDrawable(R.drawable.user);
        }

        adapter = new CustomRowAdapter(getActivity(), dummy_groups, groups_list_images);
        list_friends.setAdapter(adapter);

        list_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), groups_list_names[position], Toast.LENGTH_LONG).show();
            }
        });



        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });



        return rootView;

    }

}
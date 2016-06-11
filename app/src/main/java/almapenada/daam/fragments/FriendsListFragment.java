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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import almapenada.daam.MainActivity;
import almapenada.daam.R;
import almapenada.daam.utility.CustomRowAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private ListView list_friends;
    private CustomRowAdapter adapter;
    private View rootView;
    private EditText inputSearch;
    private String[] friends_list_names = new String[5];
    private Drawable[] friends_list_images = new Drawable[5];
    private String[] dummy_friends = { "Jorge Branquinho", "Ivo Silva", "Daniela Costa", "Andre Carvalho", "Diogo Leo" };
    private String[] dummy_users = { "Jorge Branquinho", "Ivo Silva", "Daniela Costa", "Andre Carvalho", "Diogo Leo", "Manuel Azeiteiro", "Francisco", "A tua mae"
                                , "A mae dele", "Tó Zé", "Ana.." };


    public FriendsListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        final ListView list_friends = (ListView) rootView.findViewById(R.id.friends_listview);

        for ( int i = 0 ; i < dummy_friends.length ; i++ ) {
            friends_list_images[i] = rootView.getResources().getDrawable(R.drawable.user);
        }

        adapter = new CustomRowAdapter(getActivity(), dummy_friends, friends_list_images);
        list_friends.setAdapter(adapter);

        list_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Hello " + friends_list_names[position], Toast.LENGTH_LONG).show();
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

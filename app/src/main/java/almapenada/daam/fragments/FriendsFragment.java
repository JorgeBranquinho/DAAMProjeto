package almapenada.daam.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import almapenada.daam.R;
import almapenada.daam.utility.FragmentsAdapter;


public class FriendsFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.friends_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.friends_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentsAdapter ad = new FragmentsAdapter(getFragmentManager());
        ad.addFragment(new FriendsListFragment(),getResources().getString(R.string.tab_title_friends));
        ad.addFragment(new GroupsListFragment(),getResources().getString(R.string.tab_title_groups));
        viewPager.setAdapter(ad);
    }



}
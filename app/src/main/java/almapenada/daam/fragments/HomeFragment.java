package almapenada.daam.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.utility.FragmentsAdapter;


public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);
        ((DrawerActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_home));
        ((DrawerActivity)getActivity()).removeBar(true, false,false);
        viewPager = (ViewPager) rootView.findViewById(R.id.home_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentsAdapter ad = new FragmentsAdapter(getFragmentManager());
        ad.addFragment(new FeedFragment(), getResources().getString(R.string.tab_title_feed));
        ad.addFragment(new NotificationsFragment(),getResources().getString(R.string.tab_title_notifications));
        viewPager.setAdapter(ad);
    }
}

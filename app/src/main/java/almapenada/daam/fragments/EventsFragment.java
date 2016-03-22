package almapenada.daam.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventAdapter;
import almapenada.daam.R;


public class EventsFragment extends Fragment {

    private ViewPager viewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.fragment_events, container, false);

        viewpager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewpager != null) {
            setupViewPager(viewpager);
        }
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.rectangulo1);
        tabLayout.setupWithViewPager(viewpager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter ad = new Adapter(getFragmentManager());
        ad.addFragment(new EventListFragment(), "List");
        ad.addFragment(new GroupsListFragment(), "Map");
        viewPager.setAdapter(ad);
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}

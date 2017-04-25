package com.assistmeapp.amrga.assistme;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.assistmeapp.amrga.assistme.model.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class ContainerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton floatingButton;

    public static DbHelper dbHelper;
    public static SQLiteDatabase db;
    public static UpdateSubject updateSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateSubject = UpdateSubject.getInstance();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        floatingButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewTaskActivity.class);
                startActivity(intent);
            }
        });

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    private void setupViewPager (ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        TodayFragment todayFragment = new TodayFragment();
        ThisWeekFragment thisWeekFragment = new ThisWeekFragment();
        ThisMonthFragment thisMonthFragment = new ThisMonthFragment();
        updateSubject.register(todayFragment);
        updateSubject.register(thisWeekFragment);
        updateSubject.register(thisMonthFragment);
        adapter.addFragment(todayFragment, "Today");
        adapter.addFragment(thisWeekFragment, "This Week");
        adapter.addFragment(thisMonthFragment, "This Month");
        todayFragment.addUpdateSubject(updateSubject);
        thisWeekFragment.addUpdateSubject(updateSubject);
        thisMonthFragment.addUpdateSubject(updateSubject);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}

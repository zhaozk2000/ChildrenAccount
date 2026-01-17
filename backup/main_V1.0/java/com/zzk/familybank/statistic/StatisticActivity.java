package com.zzk.familybank.statistic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.zzk.familybank.R;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DataModel;

import java.util.Date;

public class StatisticActivity extends AppCompatActivity {
    StatisticPagerAdapter pagerAdapter;
    ViewPager viewPager;
    PeriodSelectFragment periodSelectFragment;
    Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        int childId = getIntent().getIntExtra(Common.HOLDER_ID, -1);
        holder = DataModel.getDataModel(this).findHolderById(childId);

        periodSelectFragment = (PeriodSelectFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentPeriodSelect);
        periodSelectFragment.fillAccountList(holder);
        periodSelectFragment.setOnPeriodChangeListener(new PeriodSelectFragment.OnPeriodChangeListener() {
            @Override
            public void onPeriodChanged(Date start, Date end, int accountIndex) {
                int index = viewPager.getCurrentItem();
                PeriodFragment fragment = (PeriodFragment) pagerAdapter.getFragment(index);
                fragment.setPeriod(start, end, accountIndex);
            }

            @Override
            public void onYearMonthChanged(int yearMonth) {
                int index = viewPager.getCurrentItem();
                PeriodFragment fragment = (PeriodFragment) pagerAdapter.getFragment(index);
                fragment.setYearMonth(yearMonth);
            }
        });

        viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        pagerAdapter = new StatisticPagerAdapter(this, periodSelectFragment, holder, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = tab.getPosition();
                PeriodFragment fragment = (PeriodFragment) pagerAdapter.getFragment(index);
                if(fragment!=null) {    //  还未创建，那么在创建时设置起止日期
                    fragment.setPeriod(periodSelectFragment.getStartDate(), periodSelectFragment.getEndDate(), periodSelectFragment.getAccountIndex());
                    fragment.setYearMonth(periodSelectFragment.getYearMonth());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int index = tab.getPosition();
                PeriodFragment fragment = (PeriodFragment) pagerAdapter.getFragment(index);
                if(fragment!=null) {    //  还未创建，那么在创建时设置起止日期
                    fragment.setPeriod(periodSelectFragment.getStartDate(), periodSelectFragment.getEndDate(), periodSelectFragment.getAccountIndex());
                    fragment.setYearMonth(periodSelectFragment.getYearMonth());
                }
            }
        });

    }
}
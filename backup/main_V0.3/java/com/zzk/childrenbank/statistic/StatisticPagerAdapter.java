package com.zzk.childrenbank.statistic;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zzk.childrenbank.data.Child;

class StatisticPagerAdapter extends FragmentPagerAdapter {
    private static final String[] TAB_TITLES = {"收支统计", "分类统计", "往来统计"};
    private Context mContext;
    private SparseArray<Fragment> fragments;
    private PeriodSelectFragment periodSelectFragment;
    private Child child;

    public StatisticPagerAdapter(Context context, PeriodSelectFragment periodSelectFragment, Child child, FragmentManager fm) {
        super(fm);
        mContext = context;
        this.periodSelectFragment = periodSelectFragment;
        fragments = new SparseArray<>(getCount());
        this.child = child;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        PeriodFragment fragment = null;
        switch (position){
            case 0:
                fragment = InOutStatisticFragment.newInstance(child);
                break;
            case 1:
                fragment = CategoryStatisticFragment.newInstance(child);
                break;
            case 2:
                fragment = SrcDestStatisticFragment.newInstance(child);
                break;
        }
        fragment.setPeriodSelectFragment(periodSelectFragment); //  在Fragment的onCreateView之前执行
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        super.destroyItem(container, position, object);
        fragments.remove(position);
    }

    public Fragment getFragment(int position){
        return fragments.get(position);
    }
}

package com.softwill.alpha.utils;

import androidx.viewpager.widget.ViewPager;

public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

    private int currentPage;

    @Override
    public void onPageSelected(int position) {
        currentPage = position;
    }

    public final int getCurrentPage() {
        return currentPage;
    }
}
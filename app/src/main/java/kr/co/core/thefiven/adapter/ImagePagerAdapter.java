package kr.co.core.thefiven.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import kr.co.core.thefiven.data.OtherProfileImageData;
import kr.co.core.thefiven.fragment.ImageFrag;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<OtherProfileImageData> list = new ArrayList<>();

    public void setGender(String gender) {
        this.gender = gender;
    }

    String gender;

    public ImagePagerAdapter(FragmentManager fm, ArrayList<OtherProfileImageData> list) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i) {
        ImageFrag currentFragment = new ImageFrag();
        currentFragment.setData(list.get(i), gender);
        return currentFragment;
    }

    public void setList(ArrayList<OtherProfileImageData> list) {
            this.list = list;
            notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
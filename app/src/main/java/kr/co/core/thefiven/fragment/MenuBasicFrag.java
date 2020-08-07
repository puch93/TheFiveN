package kr.co.core.thefiven.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class MenuBasicFrag extends Fragment {

    public interface OnStateChangeListener {
        void onStateChanged();
        void onGoChattingFrag();
    }

    public OnStateChangeListener onStateChangeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnStateChangeListener) {
            onStateChangeListener = (OnStateChangeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnStateChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onStateChangeListener = null;
    }
}

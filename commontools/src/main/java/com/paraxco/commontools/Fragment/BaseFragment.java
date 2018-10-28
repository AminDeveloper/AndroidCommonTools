package com.paraxco.commontools.Fragment;

import android.support.v4.app.Fragment;


/**
 *
 */

public abstract class BaseFragment extends Fragment {
    private boolean isShowing = false;

    public abstract void onPageShow();


    public void onPageHide() {
    }


    @Override
    public void onStart() {
        super.onStart();
        onPageShow();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

//    protected void showMessage(int res) {
//        if (getActivity() == null)
//            return;
//        ((BaseActivity) getActivity()).showMessage(res);
//    }

    protected boolean isAttached() {
        return getActivity() != null;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (getContext() != null) {
                onPageShow();
                isShowing = true;

            }
        } else {
            onPageHide();
            isShowing = false;
        }
    }

    public boolean isShowing() {
        return isShowing;
    }
}

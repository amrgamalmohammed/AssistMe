package com.assistmeapp.amrga.assistme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrga on 9/9/2016.
 */
public class UpdateSubject {

    private static List<FragmentUtil> fragments;
    private static UpdateSubject updateSubject;

    private UpdateSubject() {
        fragments = new ArrayList<>();
    }

    public static UpdateSubject getInstance() {
        if (updateSubject == null) {
            updateSubject = new UpdateSubject();
        }
        return updateSubject;
    }

    public void register (FragmentUtil fragment) {
        fragments.add(fragment);
    }

    public void notifyUpdate(String fragmentName) {
        for (FragmentUtil fragment : fragments) {
            if (fragment.getName().equals(fragmentName)) {
                fragment.updateView();
            }
            fragment.updateViewBlind();
        }
    }

}

package com.assistmeapp.amrga.assistme;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

/**
 * Created by amrga on 9/10/2016.
 */
public class ActionsFragment extends DialogFragment{

    private DialogAdapter dialogAdapter;
    private RecyclerView recyclerView;
    private Button dialogDismiss;

    public ActionsFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dialogAdapter = new DialogAdapter(getActivity(), "actionFragment");
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_actions);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dialogAdapter);

        dialogDismiss = (Button) view.findViewById(R.id.dialog_dismiss);
        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}

package com.assistmeapp.amrga.assistme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by amrga on 9/12/2016.
 */
public class ThisMonthFragment extends Fragment implements FragmentUtil {

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private UpdateSubject updateSubject;

    public ThisMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmAdapter = new AlarmAdapter(this.getContext(), "ThisMonthFragment", updateSubject);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_this_month, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_month);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(alarmAdapter);
        return view;
    }

    @Override
    public void updateView() {
        this.alarmAdapter.commitUpdate();
    }

    @Override
    public void updateViewBlind() {
        this.alarmAdapter.commitUpdateBlind();
    }

    @Override
    public String getName() {
        return "ThisMonthFragment";
    }

    public void addUpdateSubject(UpdateSubject updateSubject) {
        this.updateSubject = updateSubject;
    }
}

package com.example.neighbourapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.neighbourapplication.controller.IncidentController;
import com.example.neighbourapplication.controller.WatchProgrammeController;

public class WatchProgrammeFragment extends Fragment {
    RecyclerView watchProgrammeRecycler;
    WatchProgrammeAdapter watchProgrammeAdapter;
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle SavedInstanceState) {
        View rootView = inflater.inflate(R.layout.programme_fragment, container, false);
        watchProgrammeRecycler = rootView.findViewById(R.id.watchProgrammeRecycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        watchProgrammeAdapter = new WatchProgrammeAdapter(getContext());
        WatchProgrammeController watchProgrammeController = new WatchProgrammeController(getContext());
        watchProgrammeController.getWatchProgrammes(watchProgrammeAdapter);
        watchProgrammeRecycler.setLayoutManager(layoutManager);

        watchProgrammeRecycler.setAdapter(watchProgrammeAdapter);

        return rootView;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        getActivity().setTitle("Watch Programme Activity");
    }
}

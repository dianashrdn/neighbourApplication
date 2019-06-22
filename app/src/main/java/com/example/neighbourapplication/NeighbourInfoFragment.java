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
import com.example.neighbourapplication.controller.UserController;

public class NeighbourInfoFragment extends Fragment {
    RecyclerView homeRecycler;
    UserAdapter userAdapter;
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.neighbour_fragment, container, false);
        homeRecycler = rootView.findViewById(R.id.homeRecycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        userAdapter = new UserAdapter(getContext());
        UserController userController = new UserController(getContext());
        userController.getUsers(userAdapter);
        homeRecycler.setLayoutManager(layoutManager);

        homeRecycler.setAdapter(userAdapter);

        return rootView;
    }

    @Override
    public  void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //set Title for layout
        getActivity().setTitle("Neighbour Info");
    }
}

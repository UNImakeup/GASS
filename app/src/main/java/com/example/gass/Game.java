package com.example.gass;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class Game extends Fragment {

    private GameViewModel mViewModel;
    private ImageButton p5Button;
    private ImageButton workoutButton;

    public static Game newInstance() {
        return new Game();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        // TODO: Use the ViewModel
        View view = getView();
        p5Button = view.findViewById(R.id.p5Button);
        workoutButton = view.findViewById(R.id.workoutButton);

        //Kan også tilføje knappen der fører til p5, når jeg har
        /*
        p5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), p5Activity.class);
                startActivity(intent);
            }
        });
         */

        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WorkoutActivity.class);
                startActivity(intent);
            }
        });

    }

}
package com.example.gass;

import java.util.ArrayList;

public class ExerciseData {
    private ArrayList<Exercise> exercises = new ArrayList<>();
    private int difficulty;
    private static ExerciseData instance;


    private ExerciseData() {
        //Bare have tom konstruktor, hvor man så kan tilføje gennem en add funktion i guess.
        // For at hente dem kan man så printe alle elementerne i ArrayListen, ved at sige .size og traverse gennem i et setText. Så kan man i sige: "in exercise 1 you got" + Exercises.get(0).getReps + "reps"
        //Kunne have en printReps funktion herinde.
    }

    public static ExerciseData getInstance(){
        if(instance == null){
            instance = new ExerciseData();
        }
        return instance;
    }

    //addExercise, for at tilføje en øvelse, når den er færdig.
    public void addExercise(Exercise exercise){
        exercises.add(exercise);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }


    //getExercises, for at få listen over gennemførte øvelser. Kunne så printes ud med en funktion der laver Strings, ligesom i BMImodel.
    public ArrayList<Exercise> getExercises(){
        return exercises;
    }

    //clearExercises, der kan gøres når øvelserne er færdige. Altså på skærmen hvor de printes, efter de printes.
    public void clearExercises(){
        exercises.clear();
    }

    //Måske getSum(), hvor vi lægger alle repsne sammen, så det kan lægges op på databasen.
    //https://stackoverflow.com/questions/16242733/sum-all-the-elements-java-arraylist
    public int getSum(){
        int sum = 0;
        for(int i = 0; i < exercises.size(); i++){
            sum += exercises.get(i).getReps();
        }
        return sum-1; //-1 fordi backbends starter på en rep af en eller anden grund. Det her burde jeg kunne fikse.
    }
}

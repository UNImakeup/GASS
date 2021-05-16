package com.example.gass;

public class User {
    //Skal muligvis bruge klassen i delen hvor vi skal hente den andens reps og sådan, ligesom i profil.
    private String user;
    private int competitionID;
    private int userCompetitionID;

    private static User instance;

    private User() {
    }

    public static User getInstance() {
        if(instance == null){
            instance = new User();
        }
        return instance;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) { //Kan bruges når man laver bruger, eller når man logger ind. Skal vi bruge i databaseklassen, for at, når man logger ind, at den også gemmer brugeren på harddisken. og her i objektet.
        this.user = user;
    }


    public int getCompetitionID() {
        return this.competitionID;
    }

    public void setCompetitionID(int competitionID) {
        this.competitionID = competitionID;
    }

    public int getUserCompetitionID() {
        return this.userCompetitionID;
    }

    public void setUserCompetitionID(int userCompetitionID) {
        this.userCompetitionID = userCompetitionID;
    }

}


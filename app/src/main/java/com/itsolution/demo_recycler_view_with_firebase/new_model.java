package com.itsolution.demo_recycler_view_with_firebase;

public class new_model {


    String FirstName;
    String LastName;
    String image;


    public new_model() {
    }

    public new_model(String firstName, String lastName, String image) {
        FirstName = firstName;
        LastName = lastName;
        this.image = image;
    }
    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

}

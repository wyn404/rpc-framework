package com.rpc.test.service;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {
    private static final long serialVersionUID = -3475626311941868983L;
    private String firstName;
    private String lastName;

    public Person(){}

    public Person(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return firstName + " "  + lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        Person p = (Person) obj;
        return this.firstName.equals(p.firstName) && this.lastName.equals(p.lastName);
    }

    @Override
    public int hashCode() {
        return this.firstName.hashCode() ^ this.lastName.hashCode();
    }
}

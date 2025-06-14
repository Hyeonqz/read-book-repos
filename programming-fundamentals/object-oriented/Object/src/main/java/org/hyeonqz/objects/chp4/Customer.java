package org.hyeonqz.objects.chp4;

public class Customer {
    private String name;
    private String id;

    public Customer (String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

}

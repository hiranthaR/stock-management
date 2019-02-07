package com.hirantha.models.data.customer;

import javafx.beans.property.SimpleStringProperty;

public class Customer {

    private SimpleStringProperty id;
    private String image_url;
    private String title;
    private String name;
    private String address;
    private String telephone;
    private int rank;

    public static String womanUrl = "/com/hirantha/icons/woman.png";
    public static String manUrl = "/com/hirantha/icons/man.png";


    public Customer(String id, boolean man, String title, String name, String address, String telephone, int rank) {

        this.id = new SimpleStringProperty(id);
        this.image_url = man ? manUrl : womanUrl;
        this.title = title;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.rank = rank;
    }

    public String getId() {
        return id.get();
    }

    public void setId(String _id) {
        this.id.set(_id);
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(boolean man) {
        this.image_url = man ? manUrl : womanUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "_id='" + id + '\'' +
                ", image_url='" + image_url + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", rank=" + rank +
                '}';
    }
}

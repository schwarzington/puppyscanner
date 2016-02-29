package com.puppyscanner.puppy;

import java.util.Date;

public class Puppy {
	protected String name;
	protected String gender;
	protected int age;
	protected String id;
	protected String size;
	protected String color;
	protected String breed;
	protected String location;
	protected Date intakeDate;
	protected float cost;
	protected String[] pictureURL;
	protected String description;

	public Puppy(String pName, String pGender, int pAge, String pBreed) {
		this.name = pName;
		this.gender = pGender;
		this.age = pAge;
		this.breed = pBreed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getIntakeDate() {
		return intakeDate;
	}

	public void setIntakeDate(Date intakeDate) {
		this.intakeDate = intakeDate;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public String[] getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String[] pictureURL) {
		this.pictureURL = pictureURL;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

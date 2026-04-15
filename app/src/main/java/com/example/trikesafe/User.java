package com.example.trikesafe;

public class User {
    private String userId, firstName, middleName, lastName, email, role, idNumber, plateNumber, parentNumber;

    public User() {}

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getParentNumber() { return parentNumber; }
    public void setParentNumber(String parentNumber) { this.parentNumber = parentNumber; }

    public String getFullName() {
        String f = (firstName != null) ? firstName : "";
        String m = (middleName != null && !middleName.isEmpty()) ? middleName + " " : "";
        String l = (lastName != null) ? lastName : "";
        return (f + " " + m + l).replace("  ", " ").trim();
    }
}
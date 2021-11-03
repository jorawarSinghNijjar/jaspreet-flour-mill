//package com.jaspreetFlourMill.accountManagement.util;
//
//public final class UserSession {
//
//    private static UserSession instance;
//
//    private String userId;
//    private UserType userType;
//
//    public enum UserType{
//        ADMIN,
//        EMPLOYEE,
//        NULL
//    }
//
//    private UserSession(String userId,UserType userType){
//        this.userId = userId;
//        this.userType = userType;
//    }
//
//
//    public static UserSession getInstance(String userId, UserType userType){
////        System.out.println("--------------->"+instance.toString());
//        if(instance == null){
//            instance = new UserSession(userId,userType);
//            System.out.println("Logging in...." + instance.toString());
//        }
//        return instance;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public UserType getUserType() {
//        return userType;
//    }
//
//    public void cleanSession(){
//        userId = "";
//        userType = UserType.NULL;
//        System.out.println("Logging out...." + instance.toString());
//    }
//
//    @Override
//    public String toString() {
//        return "UserSession{" +
//                "userId='" + userId + '\'' +
//                ", userType=" + userType +
//                '}';
//    }
//}

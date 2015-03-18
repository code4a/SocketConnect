package com.sk.socketconnect.utils;

public interface Constant {

    String HOST = "192.168.1.112";
    int PORT = 3999;
    
    String ENCODING = "UTF-8";

    /***************************** action *********************************/
    String LOGIN = "LOGIN";
    String GETTASK = "GETTASKLIST";
    String GETTASKPOINT = "GETTASKPOINT";
    String UNLOADIMAGE = "_IMAGE_";

    /***************************** Bundle Key **********************************/
    String LOGIN_RESULT = "login_result";
    String GETTASK_RESULT = "gettask_result";
    String GETTASKPOINT_RESULT = "gettaskpoint_result";
    
    /***************************** response Key **********************************/
    String LOGIN_RESULT_SUCCESS = "_MYSQL_LOGIN_YES_";
    String LOGIN_RESULT_FAILED = "_MYSQL_LOGIN_NO_";
    String GETTASK_RESULT_SUCCESS = "TASKLIST";
    String GETTASKPOINT_SUCCESS = "TASKPOINTLIST";
    
}

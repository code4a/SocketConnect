package com.sk.socketconnect.utils;

public interface Constant {

    String HOST = "115.28.142.17";
    int PORT = 3999;
    
    String ENCODING = "UTF-8";

    /***************************** action *********************************/
    String LOGIN = "LOGIN";
    String GETTASK = "GETTASKLIST";
    String GETTASKPOINT = "GETTASKPOINT";
    String UNLOADINFODES = "_WARNING_";
    String UNLOADIMAGE = "_IMAGE_";
    String UNLOADIMAGE_START = "_IMAGE_START_";
    String IMTALK = "_IM_MSG_";

    /***************************** Bundle Key **********************************/
    String LOGIN_RESULT = "login_result";
    String GETTASK_RESULT = "gettask_result";
    String GETTASKPOINT_RESULT = "gettaskpoint_result";
    String USER_ID = "user_id";
    String GET_POSITION_INFO = "get_position_info";
    
    String CURRENTPOSITONX = "currentPositonX";
    String CURRENTPOSITONY = "currentPositonY";
    String CURRENTLOCATIONSTR = "currentLocationStr";
    
    /***************************** response Key **********************************/
    String LOGIN_RESULT_SUCCESS = "_MYSQL_LOGIN_YES_";
    String LOGIN_RESULT_FAILED = "_MYSQL_LOGIN_NO_";
    String GETTASK_RESULT_SUCCESS = "TASKLIST";
    String GETTASKPOINT_SUCCESS = "TASKPOINTLIST";
    String UNLOADIMAGE_START_SUCCESS = "_IMAGE_READY_";
    String UNLOADIMAGE_SUCCESS = "_SEND_IMAGE_SUCCESS_";
    
}

package com.utils;

public interface Constants {



    //seesion object
    String TEMP_SMS_LIST = "session_temp_sms_list";
    String TEMP_MAIL_LIST = "session_temp_mail_list";
    //Sms Mail Status
    int SMS_MAIL_STATUS_READY = 0;
    int SMS_MAIL_STATUS_SENT = 1;


    /**
     * entity status
     */
    int ENTITY_STATUS_REMOVED = -1;
    int ENTITY_STATUS_USABLE = 1;
    int ENTITY_STATUS_UNUSABLE = 2;




    /**
     * entity attrnames
     */
    String DELEGATE_SEPARATOR = ".";
    String INDICATE = "Indicate";
    String FK_PREFIX = "_FK";
    String OBJ_PREFIX = "_OBJ";

    //propertyReader
    String COL_DELIM_IN_PROPERTIES = ",";

    String GET_ENTITY_TYPE_METHOD = "getEntityType";
    String KEY_FOR_PAGE_COUNT = "PageCount";
    String KEY_FOR_QUERY_RESULT = "QueryResult";


    //Form Submit Type
    String FORM_SUBMIT_OPERATION_TYPE = "operationType";
    String FORM_SUBMIT_OPERATION_DELETE = "D";
    String FORM_SUBMIT_OPERATION_REAL_DELETE = "R";
    String FORM_SUBMIT_OPERATION_UPDATE = "M";
    String FORM_SUBMIT_OPERATION_INSERT = "A";


    // *
    // *
    // *
    // *

    String COL_DELIM = "\u001C";
    String ROW_DELIM = "\u001B";
    String TEXT_QUALIFIER = "\u001A";
    String SPEARMARK = "\u0014";
    String COL_DELIM_BAK = "\u001D";
    String PARAGRAPH_DELIM = "\u0015";


    String DATATYPE_STRING = "String";
    String DATATYPE_INGEGER = "Integer";
    String DATATYPE_DOUBLE = "Double";
    String DATATYPE_LONG = "Long";
    //    public static final String DATATYPE_FLOAT ="java.lang.Float";
//    public static final String DATATYPE_NUMBER = "java.lang.Number";
    String DATATYPE_BOOLEAN = "Boolean";
    String DATATYPE_SQL_DATE = "Date";
    //    public static final String DATATYPE_UTIL_DATE = "java.util.Date";
    String DATATYPE_TIME = "Time";
    String DATATYPE_BLOB = "Blob";


    String FORWARD_SUCCESS = "success";
    String FORWARD_ERROR = "error";
    String FORWARD_ORDER_LIST_CUSSERV = "cus_serv";
    String FORWARD_ORDER_LIST_SELF_WH = "self_wh";
    String FORWARD_ORDER_LIST_DISTRIBUTOR = "distributor";


    //public String for model savedata.jsp
    String PAGE_FLAG = "bsuccess";
    String ERROR_MSG = "errormsg";
    String COMMON_CODE = "commoncode";
    String COMMON_RETCODE = "commonretcode";
    String COMMON_UUID = "commonuuid";
    String PAGE_INITFALSE = "initFalse";
    String PAGE_SUCCESS = "yes";
    String PAGE_FAIL = "no";
    String BACK_MSG = "back_msg";
    String BUSINESS_ERROR_MSG = "businesserrormsg";
    String SYSTEM_ERROR_MSG = "systemerrormsg";
    String BUSINESS_ERROR_MSG_TRACE = "businesserrormsgtrace";
    String SYSTEM_ERROR_MSG_TRACE = "systemerrormsgtrace";
    String TABLE_DATA = "tabledatas";

    //public String for model ModelGetMainInfoAction.java
    String MAIN_INFO = "maininfo";
    String DIRECT = "commondirect";

    int PRE_PAGE = 1;

    long TIME_INV = 8 * 3600 * 1000;
    String EMPTY_PREFIX = "_EMPTY";

    //Issue Recorde type
    int ISSUE_TYPE_QULITY = 6;
    int ISSUE_TYPE_LOGI = 11;
    int ISSUE_TYPE_BILL = 1;
    int ISSUE_TYPE_OTHER = 4;
    int ISSUE_TYPE_order = 9;


}


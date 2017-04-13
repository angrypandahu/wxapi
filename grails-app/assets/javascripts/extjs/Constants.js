var CONFIRM_TYPE_SIGN = 1;
var CONFIRM_TYPE_SEND = 2;
var CONFIRM_TYPE_RECEIVE = 3;
var CONFIRM_TYPE_DOCUMENT = 4;
var CONFIRM_TYPE_BILL = 5;
var CONFIRM_TYPE_APPROVE = 6;
var CONFIRM_TYPE_ACCOUNT = 7;
var CONFIRM_TYPE_MANUAL = 8;
var CONFIRM_TYPE_SUBMIT= 9;
var CONFIRM_TYPE_BILL_SUBMIT= 10;
var CONFIRM_TYPE_INVOICE= 11;
var CONFIRM_TYPE_TRACK_BILL= 12;
var CONFIRM_TYPE_MAKE_DOC= 20;
var TASK_STATUS_COMPLETED = 1;
var TASK_STATUS_NEW = 0;
var APPROVE_STATUS_SUBMIT = 2;
var APPROVE_STATUS_OK = 3;
var APPROVE_STATUS_NO = 4;

var PRE_RW_ID = "__FE_-";
var PRE_RW_ID1 = "__FE_1-";
var PRE_RW_ID2 = "__FE_2-";
var PRE_RW_ID3 = "__FE_3-";


var PRE_RO_ID = "__FR_-";
var PRE_RO_ID1 = "__FR_1-";
var PRE_RO_ID2 = "__FR_2-";
var PRE_RO_ID3 = "__FR_3-";

var FORM_RW_SIG = "__FE_";
var FORM_RO_SIG = "__FR_";

var TYPE_STRING = "String";
var TYPE_INGEGER = "Integer";
var TYPE_DOUBLE = "Double";
var TYPE_LONG = "Long";
var TYPE_BOOLEAN = "Boolean";
var TYPE_DATE = "Date";
var TYPE_BLOB = "Blob";




var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
var space = '<span>&nbsp;&nbsp;</span>';
var UPLOAD_TYPE = "upload_type";
var type_convert = {String:'string',Integer:'int',Double:'float',Long:'int',Boolean:'boolean',Date:'date'};
var type_Edit = {String:{xtype:'textfield',allowBlank: false},Integer:{xtype:'numberfield',allowBlank: false},Double:{xtype:'numberfield',allowBlank: false},Long:{xtype:'numberfield',allowBlank: false},Boolean:{xtype:'textfield'},Date:{xtype:'datefield',allowBlank: false}};

var _ADD = "A";
var _MODIFY = "M";
var _DELETE = "D";

var _ROW_DELIM = "\u001B";
var _FIELD_DELIM = "\u001C";
var _COL_DELIM_BAK = "\u001D";


var EDIT_TEXT = "text";
var EDIT_SELECT = "select";
var EDIT_OUTER = "outer";



//ComboBox 数据源定义
var COMBO_PROJECT_TYPE = "project_type";
var COMBO_PROJECT_TYPE_ALL = "project_type_all";
var COMBO_CUSTOMER_TYPE = "customer_type";
var COMBO_CUSTOMER_TYPE_SIM = "customer_type_sim";
var COMBO_CUSTOMER_TYPE_TODO = "customer_type_to_do";
var COMBO_LOGISTIC_TYPE = "logistic_type";
var COMBO_SALES_CORP = "org_corporation";
var COMBO_TASK_TEMPLATE_TYPE="taskTemplate_type";
var COMBO_REGION_DEPT = "org_region";
var COMBO_SALES_DEPT = "org_department";
var COMBO_SALES_DEPTAll = "org_departmentAll";
var COMBO_ROLE_TYPE  = "role_name";
var COMBO_TASK_TEMP = "task_temp";
var COMBO_CONFIRM_TYPE = "confirm_type";
var COMBO_TASK_TYPE = "task_type";
var COMBO_CUSTOMER_CLASSIFY1 = "customer_classify1";
var COMBO_CUSTOMER_CLASSIFY2 = "customer_classify2";
var COMBO_CUSTOMER_SALESPERSON = "customer_salesPerson";
var COMBO_VIPCUSTOMER_NAME = "vipCustomer_name";
var COMBO_SELECT_TYPE = "select_type";
var COMBO_CUSTOMER_CODE = "customer_code";

var COMBO_DOCUMENT_TYPE = "document_name";
var COMBO_TASK_STATUS = "task_status";
var COMBO_TASK_NAME = "task_name";
var COMBO_SALES_CORP_ALL = "org_corporation_all";
var COMBO_PAYMENT_PLAN_NODE = "payment_plan_node";




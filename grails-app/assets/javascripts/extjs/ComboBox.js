var taskTypeDS = new ComboBoxDS("taskTypeDS", COMBO_TASK_TYPE, null);
var projectTypeDS = new ComboBoxDS("projectTypeDS", COMBO_PROJECT_TYPE, '-1');


var customerTypeDS = new ComboBoxDS("customerTypeDS", COMBO_CUSTOMER_TYPE, null);
var taskTemplateTypeDS = new ComboBoxDS("taskTemplateTypeDS", COMBO_TASK_TEMPLATE_TYPE, null);
var customerTypeSimDS = new ComboBoxDS("customerTypeSimDS", COMBO_CUSTOMER_TYPE_SIM, null);
var logisticTypeDS = new ComboBoxDS("logisticTypeDS", COMBO_LOGISTIC_TYPE, null);
var corpDS = new ComboBoxDS("corpDS", COMBO_SALES_CORP, null);
var corpDS_All = new ComboBoxDS("corpDS_All", COMBO_SALES_CORP_ALL, null);
var regDS = new ComboBoxDS("regDS", COMBO_REGION_DEPT, -1);
var deptDS = new ComboBoxDS("deptDS", COMBO_SALES_DEPT, -1);
var deptAllDS = new ComboBoxDS("deptAllDS", COMBO_SALES_DEPTAll, -1);
var roleTypeDS = new ComboBoxDS("roleTypeDS", COMBO_ROLE_TYPE, null);

var confirmDS = new ComboBoxDS("confirmDS", COMBO_CONFIRM_TYPE, null);
var customerClassify1DS = new ComboBoxDS("customerClassify1DS", COMBO_CUSTOMER_CLASSIFY1, null);
var customerClassify2DS = new ComboBoxDS("customerClassify2DS", COMBO_CUSTOMER_CLASSIFY2, null);


var customerCodeDS = new ComboBoxDS("customerCodeDS", COMBO_CUSTOMER_CODE, null);

var approvePersonStore = Ext.create('Ext.data.Store', {
    fields: ['name', 'value'],
    proxy: {
        type: 'ajax',
        url: URL_PATH + '/biz/approve/GetSubmitApprove.do',
        reader: {
            type: "json",
            root: "contents"
        }
    },
    listeners: {
        load: function (s, recs) {
        }
    },
    autoLoad: false

});


var loadApprovePerson = function (custom_uuid) {
    approvePersonStore.proxy.url = URL_PATH + '/biz/approve/GetSubmitApprove.do?Customer_uuid=' + custom_uuid;
    approvePersonStore.load(function (records, operation, success) {
    });
};


var SelectCustomerEvent = function (custom_uuid, projectId) {
    var projectCB = Ext.getCmp(projectId);
    if (typeof(projectCB) == "undefined") {
        return;
    }
    projectCB.clearValue();
    projectTypeDS.Reload(COMBO_PROJECT_TYPE, custom_uuid, projectCB);
};
var customerComboBox = new Ext.form.ComboBox({
    xtype: 'combobox',
    editable: true,
    triggerAction: 'all', //设成all
    emptyText: '客户名称',
    id: 'customer_id',
    store: customerTypeSimDS.GetStore(),
    name: 'customer_id',
    displayField: 'name',
    valueField: 'value',
    labelAlign: 'right',
    width: 250,
    listeners: {
        select: function (com, rec) {
            var custom_uuid = com.value;
            SelectCustomerEvent(custom_uuid, 'project_id');
        },
        specialkey: function (field, e) {
            if (e.getKey() === e.ENTER) {
                queryFunction(field)
            }
        },
        beforequery: function (e) {
            beforequeryFunction(e);
        }
    }
});

var projectComboBox = new Ext.form.ComboBox({
    xtype: 'combobox',
    emptyText: '项目名称',
    editable: true,
    triggerAction: 'all', //设成all
    width: 250,
    id: 'project_id',
    name: 'project_id',
    store: projectTypeDS.GetStore(),
    displayField: 'name',
    valueField: 'value',
    listeners: {
        specialkey: function (field, e) {
            if (e.getKey() === e.ENTER) {
                queryFunction(field)
            }
        },
        beforequery: function (e) {
            beforequeryFunction(e);
        }
    }
});
var projectTextField = {
    xtype: 'textfield',
    emptyText: '项目名称',
    width: 250,
    name: 'Project_name',
    listeners: {
        specialkey: function (field, e) {
            if (e.getKey() === e.ENTER) {
                queryFunction(field)
            }
        }
    }
};
var beginComboBox = new Ext.form.field.Date({
    xtype: 'datefield',
    emptyText: '起始时间', //value: new Date(),
    id: 'date_begin',
    format: 'Y-m-d',
    name: 'begin',
    width: 100,
    listeners: {
        specialkey: function (field, e) {
            if (e.getKey() === e.ENTER) {
                queryFunction(field)
            }
        }
    }

});
var endComboBox = new Ext.form.field.Date({
    xtype: 'datefield',
    emptyText: '截止时间', //value: new Date(),
    id: 'date_end',
    format: 'Y-m-d',
    name: 'end',
    width: 100,
    listeners: {
        specialkey: function (field, e) {
            if (e.getKey() === e.ENTER) {
                queryFunction(field)
            }
        }
    }

});
var salesOrgComboBox = new Ext.form.ComboBox({
    xtype: 'combobox',
    emptyText: '公司',
    width: 70,
    id: 'salesOrg',
    name: 'salesOrg',
    store: corpDS.GetStore(),
    displayField: 'name',
    valueField: 'value',
    listeners: {
        select: function (com, rec) {
            var reDept = Ext.getCmp('regDept');
            reDept.clearValue();
            regDS.Reload(COMBO_REGION_DEPT, com.value, reDept);
            var salesDeprtnt = Ext.getCmp('salesDeprtnt');
            salesDeprtnt.clearValue();
        }
    }
});


var customerCodeComboBox = new Ext.form.ComboBox(
    { xtype: 'combobox',
        editable: true,
        triggerAction: 'all', //设成all
        emptyText: '客户编码',
        id: 'customerCode',
        name: 'Customer_code',
        store: customerCodeDS.GetStore(),
        displayField: 'value',
        valueField: 'value',
        labelAlign: 'right',
        width: 110,
        listeners: {
            specialkey: function (field, e) {
                if (e.getKey() === e.ENTER) {
                    queryFunction(field)
                }
            },
            beforequery: function (e) {
                beforequeryFunction(e);
            }
        }

    });

var queryButton = new Ext.button.Button({
    xtype: 'button',
    text: '查询',
    tooltip: '根据用户设定的条件查询',
    icon: URL_PATH + '/images/_query.gif',
    id: 'queryBtn'

});
var beforequeryFunction = function (e) {
    var combo = e.combo;
    if (!e.forceAll) {
        var value = e.query;
        combo.store.filterBy(function (record, id) {
            var text = record.get(combo.displayField);
//用自己的过滤规则,如写正则式
            return (text.indexOf(value) != -1);    //实现的核心
        });
        combo.expand();
        return false;
    }
};


function getUrlPara(othis) {
    var ownerCt = othis.ownerCt.items.items;
    var arrComboBox = [];
    for (var i = 0; i < ownerCt.length; i++) {
        var item = ownerCt[i];
        arrComboBox.push(item);

    }
    return getJsonOrUrlPara(arrComboBox, false);
}
function getJsonOrUrlPara(arrComboBox, isJson) {
    var arrUrlPara = [];
    var jsonObject = {};
    var jsonStr;
    if (arrComboBox.length > 0) {
        for (var i = 0; i < arrComboBox.length; i++) {
            var comboBox = arrComboBox[i];
            var comboBoxValue;
            var XType = comboBox.getXType();
            if (XType == 'datefield' || XType == 'combobox' || XType == 'textfield' || XType == "hidden" || XType == 'monthfield') {
                if (XType == 'datefield' || XType == 'monthfield') {
                    comboBoxValue = comboBox.getRawValue();
                } else {
                    comboBoxValue = comboBox.getValue();
                }
                if (comboBoxValue) {
                    if (comboBox.getId() == "customerCode") {
                        comboBoxValue = Ext.String.trim(comboBoxValue);
                        if (comboBoxValue.substring(0, 1) != "0") {
                            comboBoxValue = "0" + comboBoxValue;
                        }
                    }
                    arrUrlPara.push(comboBox.getName() + "=" + comboBoxValue);
                    jsonObject[comboBox['sql']] = comboBoxValue;
                }
            }


        }
    }
    jsonObject['pressQuery'] = 'pressQuery';
    arrUrlPara.push("pressQuery=pressQuery");
    return isJson ? JSON.stringify(jsonObject) : arrUrlPara.join('&');
}
function getUrlPara_toolbar(othis) {
    var parentContainer = othis.ownerCt.ownerCt;
    var toolbars = parentContainer.query("toolbar");
    var arrComboBox = [];
    for (var i = 0; i < toolbars.length; i++) {
        var toolbar = toolbars[i];
        var ownerCt = toolbar.items.items;
        for (var j = 0; j < ownerCt.length; j++) {
            var item = ownerCt[j];
            arrComboBox.push(item);
        }

    }
    return getJsonOrUrlPara(arrComboBox, true);
    /*
     var ownerCt = othis.ownerCt.items.items;
     var arrComboBox = [];
     for (var i = 0; i < ownerCt.length; i++) {
     var item = ownerCt[i];
     arrComboBox.push(item);

     }
     var arrUrlPara = [];
     if(arrComboBox.length > 0){
     for (var i = 0; i < arrComboBox.length; i++) {
     var comboBox = arrComboBox[i];
     var comboBoxValue;
     var XType = comboBox.getXType();
     if(XType=='datefield' || XType == 'combobox' || XType == 'textfield' || XType == "hidden" || XType=='monthfield'){
     if(XType == 'datefield' || XType=='monthfield' ){
     comboBoxValue = comboBox.getRawValue();
     }else{
     comboBoxValue = comboBox.getValue();
     }
     if(comboBoxValue){
     if(comboBox.getId() == "customerCode"){
     comboBoxValue = Ext.String.trim(comboBoxValue);
     if(comboBoxValue.substring(0,1) != "0"){
     comboBoxValue =  "0" +comboBoxValue;
     }
     }
     arrUrlPara.push(comboBox.getName() + "=" + comboBoxValue );
     }
     }


     }
     }
     arrUrlPara.push("pressQuery=pressQuery");
     return  arrUrlPara.join('&');
     */
}
function queryFunction(field) {

}

function GetContentStore(kind) {
    return Ext.create('Ext.data.Store', {
        fields: ['name', 'value'],
        proxy: {
            type: 'ajax',
            url: URL_PATH + encodeURI('/CommonAction.do?act=content_str&para=' + kind),
            reader: {
                type: "json",
                root: "content"
            }
        },
        listeners: {
            load: function (s, recs) {

            }
        },
        autoLoad: true
    });

}
function GetOverReason(parent,isAutoLoad) {
    return Ext.create('Ext.data.Store', {
        fields: ['name', 'value'],
        proxy: {
            type: 'ajax',
            url: URL_PATH + encodeURI('/CommonAction.do?act=Over_reason&parent=' + parent),
            reader: {
                type: "json",
                root: "content"
            }
        },
        listeners: {
            load: function (s, recs) {

            }
        },
        autoLoad: !!isAutoLoad
    });

}
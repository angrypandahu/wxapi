Ext.Loader.setConfig({enabled: true});

// Ext.Loader.setPath('Ext.ux', '../ux/');
Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.util.*',
    'Ext.toolbar.Paging',
    // 'Ext.ux.PreviewPlugin',
    'Ext.ModelManager',
    'Ext.tip.QuickTipManager'
]);

//定义用户编辑弹出对话框
var frmUser = Ext.create('Ext.form.Panel', {
    plain: true,
    border: 0,
    bodyPadding: 5,

    fieldDefaults: {
        labelWidth: 80,
        anchor: '100%'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'  // Child items are stretched to full width
    },
    defaultType: 'textfield',
    items: [
        {
            //不可见字段，表示该记录的Add Modify Delete
            id: PRE_RW_ID + 'operationType',
            xtype: 'hiddenfield',
            name: TYPE_STRING
        },
        { //UUID 字段
            id: PRE_RW_ID + user_uuid,
            xtype: 'hiddenfield',
//        fieldLabel: 'UUID',
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + user_username,
            afterLabelTextTpl: '<span style="color:red">*</span>',
            allowBlank: false,
            enforceMaxLength: true,
            maxLength: 20,
            fieldLabel: '登录名',
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + user_password,
            afterLabelTextTpl: '<span style="color:red">*</span>',
            inputType: 'password',
            allowBlank: false,
            enforceMaxLength: true,
            maxLength: 20,
            fieldLabel: '密码',
            name: TYPE_STRING
        },
        {
            id: user_password,
            afterLabelTextTpl: '<span style="color:red">*</span>',
            inputType: 'password',
            allowBlank: false,
            enforceMaxLength: true,
            maxLength: 20,
            fieldLabel: '确认密码',
            vtype: 'password',
            initialPassField: PRE_RW_ID + user_password,
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + user_name,
            afterLabelTextTpl: '<span style="color:red">*</span>',
            allowBlank: false,
            enforceMaxLength: true,
            maxLength: 20,
            fieldLabel: '用户名称',
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + user_phone,
            fieldLabel: '用户电话',
            enforceMaxLength: true,
            maxLength: 200,
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + user_email,
            fieldLabel: 'E-Mail',
            enforceMaxLength: true,
            maxLength: 2000,
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + user_memo,
            fieldLabel: '备注',
            enforceMaxLength: true,
            maxLength: 200,
            name: TYPE_STRING
        }
    ]
});

//新增用户对话框
var win = Ext.create('Ext.window.Window', {
    title: '新增用户',
    width: 300,
    height: 280,
    minWidth: 300,
    minHeight: 200,
    layout: 'fit',
    plain: true,
    modal: true, resizable: false,
    closable: true,
    closeAction: 'hide',
    items: frmUser,

    buttons: [
        {
            text: '确定',
            handler: function () {
                //是否合法输入
                if (frmUser.getForm().isValid()) {
                    //保存的路径
                    ctlSave.Action = URL_PATH + "/baseinfo/user/SaveUsers.do";
                    //保存后的动作
                    ctlSave.AfterSave = function () {
                        if (ctlSave.strErrorMsg) {
                            //保存失败
                            alert(ctlSave.strErrorMsg);
                        } else { //保存成功
                            //得到Form中所有字段值
                            var form = frmUser.getForm();
                            var username = form.findField(PRE_RW_ID + user_username).getValue();
                            var name = form.findField(PRE_RW_ID + user_name).getValue();
                            var phone = form.findField(PRE_RW_ID + user_phone).getValue();
                            var mail = form.findField(PRE_RW_ID + user_email).getValue();

                            //表格数组
                            var rec = [ctlSave.strUUID, username, name, phone, mail];

                            //是新增还是修改
                            var amd = frmUser.getForm().findField(PRE_RW_ID + 'operationType').getValue();

                            //设置表格中的内容
                            if (amd == _MODIFY) {
                                UserGrid.SetRow(rec)
                            } else {
                                UserGrid.Add(-1, rec);
                            }

                            //清空表单,隐藏窗口
                            frmUser.getForm().reset();
                            win.hide();
                        }
                    };
                    //得到保存内容，并保存
                    ctlSave.strMap = _GetPageMapString(frmUser.getForm());
                    ctlSave.Save();
                }
            }
        },
        {
            text: '取消',
            handler: function () {
                frmUser.getForm().reset();  //清空表单
                win.hide();
            }
        }
    ]
});

//定义角色编辑弹出对话框
var frmRole = Ext.create('Ext.form.Panel', {
    plain: true,
    border: 0,
    bodyPadding: 5,

    fieldDefaults: {
        labelWidth: 80,
        anchor: '100%'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'  // Child items are stretched to full width
    },
    defaultType: 'textfield',
    items: [
        {
            //不可见字段，表示该记录的Add Modify Delete
            id: PRE_RW_ID + 'operationType',
            xtype: 'hiddenfield',
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + role_uuid,
            xtype: 'hiddenfield',
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + role_code,
            afterLabelTextTpl: '<span style="color:red">*</span>',
            allowBlank: false,
            enforceMaxLength: true,
            maxLength: 20,
            fieldLabel: '角色编码',
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + role_name,
            afterLabelTextTpl: '<span style="color:red">*</span>',
            allowBlank: false,
            enforceMaxLength: true,
            maxLength: 20,
            fieldLabel: '角色名称',
            name: TYPE_STRING
        },
        {
            id: PRE_RW_ID + role_desc,
            enforceMaxLength: true,
            maxLength: 100,
            fieldLabel: '角色描述',
            name: TYPE_STRING
        }
    ]
});

//新增角色对话框
var win_role = Ext.create('Ext.window.Window', {
    title: '新增角色',
    width: 300,
    height: 280,
    minWidth: 300,
    minHeight: 200,
    layout: 'fit',
    plain: true,
    modal: true, resizable: false,
    closable: true,
    closeAction: 'hide',
    items: frmRole,

    buttons: [
        {
            text: '确定',
            handler: function () {
                //是否合法输入
                if (frmRole.getForm().isValid()) {
                    //保存的路径
                    ctlSave.Action = URL_PATH + "/baseinfo/role/SaveRoles.do";
                    //保存后的动作
                    ctlSave.AfterSave = function () {
                        if (ctlSave.strErrorMsg) {
                            //保存失败
                            alert(ctlSave.strErrorMsg);
                        } else { //保存成功
                            //得到Form中所有字段值
                            var form = frmRole.getForm();
                            var code = form.findField(PRE_RW_ID + role_code).getValue();
                            var name = form.findField(PRE_RW_ID + role_name).getValue();
                            var desc = form.findField(PRE_RW_ID + role_desc).getValue();

                            //表格数组
                            var rec = [ctlSave.strUUID, false, code, name, desc];

                            //是新增还是修改
                            var amd = frmRole.getForm().findField(PRE_RW_ID + 'operationType').getValue();

                            //设置表格中的内容
                            if (amd == _MODIFY) {
                                RoleGrid.SetRow(rec)
                            } else {
                                RoleGrid.Add(-1, rec);
                            }

                            //清空表单,隐藏窗口
                            frmRole.getForm().reset();
                            win_role.hide();
                        }
                    };
                    //得到保存内容，并保存
                    ctlSave.strMap = _GetPageMapString(frmRole.getForm());
                    ctlSave.Save();
                }
            }
        },
        {
            text: '取消',
            handler: function () {
                frmRole.getForm().reset();  //清空表单
                win_role.hide();
            }
        }
    ]
});

//组织树数据源
Ext.define('Task', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'userOrg', type: 'boolean'},
        {name: 'text', type: 'string'},
        {name: 'code', type: 'string'},
        {name: 'Org_hierarchy', type: 'string'},
        {name: 'UserOrg_uuid', type: 'string'}
    ]
});
var orgTreeStore = Ext.create('Ext.data.TreeStore', {
    model: 'Task',
    root: {
        expanded: true
    },
    proxy: {
        type: 'ajax',
        url: '/org/all'
    }
});


// Go ahead and create the TreePanel now so that we can use it below
var treePanel = Ext.create('Ext.tree.Panel', {
    id: 'tree-panel',
    region: 'east',
    height: '100%',
    width: '50%',
    viewConfig: {
        plugins: {
            ptype: 'treeviewdragdrop'
        }
    },
//    height: 200,
//    minSize: 150,
    rootVisible: false,
    autoScroll: true,
    store: orgTreeStore,
    listeners: {
        cellclick: function (t, td, col, rec, tr, row) {
            if (col == 0) {
                var val = rec.get('userOrg');
                rec.set('userOrg', !val);
                var id = rec.get('id');
                if (!val)
                    arrOrgCheck[id] = rec;
                else
                    arrOrgCheck[id] = undefined;

                if (UserGrid.Grid.getSelectionModel().getSelection().length > 0)
                    Ext.getCmp('SaveOrgBtn').setDisabled(false);
            }
        }
    },
    columns: [
        {
//        xtype:'checkcolumn',
            text: '用户组织',
            flex: 1,
            renderer: function (value, metaData) {
                if (value) {
                    return '<div class="x-grid-checkcolumn x-grid-checkcolumn-checked">&nbsp;</div>';
                } else {
                    return '<div class="x-grid-checkcolumn">&nbsp;</div>';
                }
            },
            dataIndex: 'userOrg'
        },
        {
            xtype: 'treecolumn', //this is so we know which column will show the tree
            text: '组织',
            flex: 6,
            sortable: true,
            dataIndex: 'text'
        },
        {
            text: '代码',
            flex: 2,
            dataIndex: 'code',
            sortable: true
        },
        {
            text: '层别',
            flex: 2,
            hidden: true,
            dataIndex: 'Org_hierarchy',
            sortable: false
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            items: [
                {
                    id: 'OrgTxt',
                    xtype: 'tbtext',
                    text: '组织 ',
                    baseCls: 'cls_toolbarTitle'
                },
                '-',
                {
                    id: 'SaveOrgBtn',
                    text: '保存',
                    tooltip: '保存用户组织变更',
                    icon: '/assets/menu/_save.gif',
                    disabled: true,
                    handler: function () {

                        //保存用户角色关系
                        ctlSave.Action = URL_PATH + "/baseinfo/org/SaveUserOrg.do?User_uuid=" + UserGrid.GetCell("User_uuid");

                        //保存完成后的动作
                        ctlSave.AfterSave = function () {
                            if (ctlSave.strErrorMsg) {
                                //提示错误信息
                                alert(ctlSave.strErrorMsg);
                            } else {
                                //
                                //                                                //去掉表格中的修改标记
                                //                                                treePanel.store.commitChanges();
                                //保存按钮Disabled
                                Ext.getCmp('SaveOrgBtn').setDisabled(true);
                            }
                        };

                        //设置保存内容
                        var coll = [];
                        coll.push("operationType" + _FIELD_DELIM + "UserOrg_orgId");
                        coll.push(TYPE_STRING + _FIELD_DELIM + TYPE_STRING);

                        for (var id in arrOrgCheck) {
                            if (arrOrgCheck[id]) {
                                coll.push(_ADD + _FIELD_DELIM + id);
                            }
                        }
                        ctlSave.strCollection = coll.join(_ROW_DELIM);
                        ctlSave.Save();
                    }
                },
                {
                    align: 'right',
                    text: '导入',
                    id: "btnImport_org_user",
                    icon: '/assets/menu/_excel.gif',
                    handler: function () {
                        win_UF.setTitle("批量导入用户");
                        win_UF.show();
                        frmUF.getForm().findField(UPLOAD_TYPE).setValue(UPLOAD_TYPE_USER_SALE);
                        uploadCallBack = function () {
                        }
                    }

                }

            ]
        }
    ]

});


//以下是用户管理界面显示，可酌情添加实际内容。
Ext.onReady(function () {
    function initTable(arr) {
        var store = Ext.create('Ext.data.Store', {
            pageSize: 10,
            remoteSort: true,
            fields: arr['fields'],
            proxy: {
                type: 'ajax',
                url: arr['url'],
                reader: {
                    root: 'datas',
                    totalProperty: 'totalCount'
                }
            }
        });
        Ext.create('Ext.grid.Panel', {
            header: false,
            store: store,
            columns: arr['columns'],
            height: "100%",
            width: "100%",
            tbar: {
                items: [{
                    text: '新增',
                    icon: '/assets/menu/add.gif',
                    handler: function () {
                        window.open(arr['createUrl'])

                    }
                }]
            },
            bbar: Ext.create('Ext.PagingToolbar', {
                store: store,
                displayInfo: true,
                displayMsg: '记录 {0} - {1} of {2}',
                emptyMsg: "无记录",
                items: [
                    '-']
            }),
            renderTo: arr['renderTo']
        });
        store.loadPage(1)

    }

    //动态设定TreePanel的高度。
    UserGrid.AfterReset = function () {
        treePanel.height = UserGrid.Height;
    };

    // Add the additional 'advanced' VTypes
    Ext.apply(Ext.form.field.VTypes, {
        password: function (val, field) {
            if (field.initialPassField) {
                var pwd = field.up('form').down('#' + field.initialPassField);
                return (val == pwd.getValue());
            }
            return true;
        },
        passwordText: '密码与确认密码不符！'
    });

    Ext.create('Ext.Viewport', {
        layout: {
            type: 'border',
            padding: 0
        },
        items: [
            {
                region: 'north',
                height: '50%',
                layout: {
                    type: 'border',
                    padding: 0
                },
                items: [
//******** 用户表区域
                    {
                        xtype: 'container',
                        region: 'west',
                        height: '100%',
                        width: '50%',
                        layout: 'fit',
                        id: 'con_user'
                    },
//******** 组织表区域
                    treePanel
                ]

            },
            {
                region: 'south',
                height: '50%',
                layout: {
                    type: 'border',
                    padding: 0
                },
                items: [
//******** 角色表区域
                    {
                        xtype: 'container',
                        region: 'west',
                        height: '100%',
                        width: '50%',
                        autoScroll: true,
                        layout: 'fit',
                        id: 'con_role'

                    },
//******** 权限表区域
                    {
                        region: 'east',
                        height: '100%',
                        width: '50%',
                        autoScroll: true,
                        layout: 'fit',
                        items: null,
                        id: 'con_priv',
//                        html: '<div style="height:100%;width:100%" id="con_priv"></div>',
                        dockedItems: [
                            {
                                xtype: 'toolbar',
                                items: [
                                    {
                                        id: 'PrivTxt',
                                        xtype: 'tbtext',
                                        text: '权限 ',
                                        baseCls: 'cls_toolbarTitle'
                                    },
                                    '-',
                                    {
                                        id: 'SavePrivBtn',
                                        text: '保存',
                                        tooltip: '保存角色权限变更',
                                        icon: '/assets/menu/_save.gif',
                                        disabled: true,
                                        handler: function () {
                                            //保存用户角色关系
                                            ctlSave.Action = URL_PATH + "/baseinfo/role/SaveRolePrivileges.do?Role_uuid=" + RoleGrid.GetCell("Role_uuid");

                                            //保存完成后的动作
                                            ctlSave.AfterSave = function () {
                                                if (ctlSave.strErrorMsg) {
                                                    //提示错误信息
                                                    alert(ctlSave.strErrorMsg);
                                                } else {
                                                    //去掉表格中的修改标记
                                                    PrivGrid.Grid.store.commitChanges();
                                                    //保存按钮Disabled
                                                    Ext.getCmp('SavePrivBtn').setDisabled(true);
                                                }
                                            };

                                            //设置保存内容
                                            ctlSave.strCollection = PrivGrid.GetCheckString("checkbox", _ADD, "Privilege_uuid");

                                            //要保存到RolePrivilege表的Privilege_id中，所以替换保存字串中的属性名称
                                            ctlSave.strCollection = ctlSave.strCollection.replace("Privilege_uuid", "RolePrivilege_privilegeId");
                                            ctlSave.Save();
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ]
    });
    initTable(userTable);
    initTable(roleTable);


});


//用户、角色、权限 三个表格定义
var UserGrid = new TData({
    name: 'UserGrid',
    url: URL_PATH + '/baseinfo/user/GetAllUsersAction.do',
    isFlex: true
});

var RoleGrid = new TData({
    name: 'RoleGrid',
    url: URL_PATH + '/baseinfo/role/GetAllRolesAction.do',
    isFlex: true,
    container: 'con_role'
});

var PrivGrid = new TData({
    name: 'PrivGrid',
    url: URL_PATH + '/baseinfo/privilege/GetAllPrivilegesAction.do',
    isFlex: true,
    container: 'con_priv'
});

//后台保存数据的全局对象
var ctlSave = new SaveData("ctlSave");

//后台取Form数据的全局对象
var ctlFormData = new FormData("ctlFormData");

//取得用户角色对应关系全局对象。该对象只用于取数据，不需要设置Container属性，也就不显示。
var ntUserRoles = new TData({name: 'ntUserRoles'});

//取得用户组织对应关系全局对象。该对象只用于取数据，不需要设置Container属性，也就不显示。
var ntUserOrgs = new TData({name: 'ntUserOrgs'});

//取得角色权限对应关系全局对象。该对象只用于取数据，不需要设置Container属性，也就不显示。
var ntRolePrivileges = new TData({name: 'ntRolePrivileges'});

//组织树，checkbox记录器
var arrOrgCheck = [];
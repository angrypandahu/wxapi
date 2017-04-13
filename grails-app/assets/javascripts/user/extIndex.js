Ext.Loader.setConfig({enabled: true});

// Ext.Loader.setPath('Ext.ux', '../ux/');
Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'App.base': '/assets/extjs/base'
    }
});
Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.util.*',
    'Ext.toolbar.Paging',
    // 'Ext.ux.PreviewPlugin',
    'Ext.ModelManager',
    'Ext.tip.QuickTipManager'
]);


//以下是用户管理界面显示，可酌情添加实际内容。
Ext.onReady(function () {
    Ext.create('Ext.Viewport', {
        layout: {
            type: 'border',
            padding: 0
        },
        items: [
            {
                xtype: 'container',
                region: 'north',
                height: '100%',
                width: '100%',
                layout: 'fit',
                autoScroll: true,
                //以下两处，可添加 Extjs元素或者 html
//                html:'<div style="height:100%;width:100%" id="con_cus"></div>',
                id: 'con_cus'
            }
        ]
    });

    var store = Ext.create('Ext.data.Store', {
        pageSize: 10,
        remoteSort: true,
        storeId: 'simpsonsStore',
        fields: fields,
        proxy: {
            type: 'ajax',
            url: 'http://localhost:8080/menu/usermanager?isJson=true',
            reader: {
                root: 'datas',
                totalProperty: 'totalCount'
            }
        }
    });

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


    Ext.create('Ext.grid.Panel', {
        header: false,
        store: Ext.data.StoreManager.lookup('simpsonsStore'),
        columns: columns,
        height: "100%",
        width: "100%",
        tbar: {
            items: [{
                text: '新增',
                tooltip: '新增用户',
                icon: '/assets/menu/add.gif',
                handler: function () {
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
                    win.show();

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
        renderTo: 'con_cus'
    });
    store.loadPage(1)


});

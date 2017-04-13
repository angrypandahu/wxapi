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


//以下是用户管理界面显示，可酌情添加实际内容。
var UserGrid;
var RoleGrid;
var PrivilegeGrid;
Ext.onReady(function () {
    var treePanelTable = new ExtTreePanel(treePanel, {}, [{
        id: 'OrgTxt',
        xtype: 'tbtext',
        text: '组织 '
    },
        '-',{
            text: '新增',
            icon: '/assets/menu/add.gif',
            handler: function () {
                window.open("/org/create")

            }
        },
        {
            id: 'SaveOrgBtn',
            text: '保存',
            xtype: 'button',
            tooltip: '保存用户组织变更',
            icon: '/assets/menu/_save.gif',
            handler: function () {
                var userSelection = UserGrid.grid.getSelectionModel().getSelection();
                var treeSelection = treePanelTable.treePanel.getSelectionModel().getSelection();
                if (userSelection && userSelection.length > 0) {
                    var id = userSelection[0].get("user_id_label");
                    var org = [];
                    for (var i = 0; i < treeSelection.length; i++) {
                        org.push(treeSelection[i].get("id"));
                    }
                    Ext.Ajax.request({
                        url: '/userOrg/ajaxSave',
                        headers: {},
                        params: {'isJson': true, 'user': id, 'org': org},
                        method: 'POST',
                        success: function (response, options) {
                            Ext.MessageBox.alert('成功', "保存成功!");
                        },
                        failure: function (response, options) {
                            Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                        }
                    });
                } else {
                    Ext.MessageBox.alert("提示", "请选择用户!");
                }


            }
        }]);

    Ext.create('Ext.Viewport', {
        layout: {
            type: 'border',
            padding: 0
        },
        items: [
            {
                region: 'west',
                height: '100%',
                width: '50%',
                layout: {
                    type: 'border',
                    padding: 0
                },
                items: [
                    {
                        xtype: 'container',
                        region: 'north',
                        height: '50%',
                        width: '100%',
                        layout: 'fit',
                        id: 'con_user'
                    },
                    {
                        xtype: 'container',
                        region: 'south',
                        height: '50%',
                        width: '100%',
                        layout: 'fit',
                        id: 'con_role'
                    }

                ]

            },
            {
                region: 'east',
                height: '100%',
                width: '50%',
                layout: {
                    type: 'border',
                    padding: 0
                },
                items: [
                    treePanelTable.treePanel,
                    {
                        xtype: 'container',
                        region: 'south',
                        height: '50%',
                        width: '100%',
                        layout: 'fit',
                        id: 'con_privilege'
                    }

                ]

            }
        ]
    });
    UserGrid = new ExtGrid(userTable, {
        itemclick: function (view, record, item, index) {
            var id = record.get("user_id_label");
            Ext.Ajax.request({
                url: '/userRole/getUserRoles',
                headers: {},
                params: {'isJson': true, 'user': id},
                method: 'GET',
                success: function (response, options) {
                    var responseText = response.responseText;
                    var parseJSON = $.parseJSON(responseText);
                    if (parseJSON.success) {
                        var idList = parseJSON.data['roleList'];
                        RoleGrid.selectGrid(idList, 'role_id_label');
                    }

                },
                failure: function (response, options) {
                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                }
            });

            Ext.Ajax.request({
                url: '/org/getOrgs',
                headers: {},
                params: {'isJson': true, 'user': id},
                method: 'GET',
                success: function (response, options) {
                    var responseText = response.responseText;
                    var parseJSON = $.parseJSON(responseText);
                    if (parseJSON.success) {
                        var idList = parseJSON.data['orgList'];
                        treePanelTable.selectTree(idList);
                    }

                },
                failure: function (response, options) {
                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                }
            })
        }
    }, [{
        id: 'UserTxt',
        xtype: 'tbtext',
        text: '用户 '
    },
        '-', {
            text: '新增',
            icon: '/assets/menu/add.gif',
            handler: function () {
                window.open("/user/create")

            }
        }]);
    RoleGrid = new ExtGrid(roleTable, {
        itemclick: function (view, record, item, index) {
            var id = record.get("role_id_label");
            Ext.Ajax.request({
                url: '/rolePrivilege/getRolePrivileges',
                headers: {},
                params: {'isJson': true, 'role': id},
                method: 'GET',
                success: function (response, options) {
                    var responseText = response.responseText;
                    var parseJSON = $.parseJSON(responseText);
                    if (parseJSON.success) {
                        var idList = parseJSON.data['privilegeList'];
                        PrivilegeGrid.selectGrid(idList, 'privilege_id_label');
                    }

                },
                failure: function (response, options) {
                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                }
            });


        }
    }, [{
        id: 'RoleTxt',
        xtype: 'tbtext',
        text: '角色 '
    },
        '-', {
            text: '新增',
            icon: '/assets/menu/add.gif',
            handler: function () {
                window.open("/role/create")

            }
        },
        {
            id: 'SaveRoleBtn',
            text: '保存',
            xtype: 'button',
            icon: '/assets/menu/_save.gif',
            handler: function () {
                var userSelection = UserGrid.grid.getSelectionModel().getSelection();
                var roleSelection = RoleGrid.grid.getSelectionModel().getSelection();
                if (userSelection && userSelection.length > 0) {
                    var id = userSelection[0].get("user_id_label");
                    var role = [];
                    for (var i = 0; i < roleSelection.length; i++) {
                        role.push(roleSelection[i].get("role_id_label"));
                    }
                    Ext.Ajax.request({
                        url: '/userRole/ajaxSave',
                        headers: {},
                        params: {'isJson': true, 'user': id, 'role': role},
                        method: 'POST',
                        success: function (response, options) {
                            Ext.MessageBox.alert('成功', "保存成功!");
                        },
                        failure: function (response, options) {
                            Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                        }
                    });
                } else {
                    Ext.MessageBox.alert("提示", "请选择用户!");
                }


            }
        }], true);


    PrivilegeGrid = new ExtGrid(privilegeTable, {}, [{
        id: 'PrivilegeTxt',
        xtype: 'tbtext',
        text: '权限 '
    },
        '-',
        {
            text: '新增',
            icon: '/assets/menu/add.gif',
            handler: function () {
                window.open("/privilege/create")

            }
        },
        {
            id: 'SavePrivilegeBtn',
            text: '保存',
            xtype: 'button',
            icon: '/assets/menu/_save.gif',
            handler: function () {
                var roleSelection = RoleGrid.grid.getSelectionModel().getSelection();
                var privilegeSelection = PrivilegeGrid.grid.getSelectionModel().getSelection();
                if (roleSelection && roleSelection.length > 0) {
                    var id = roleSelection[0].get("role_id_label");
                    var privilege = [];
                    for (var i = 0; i < privilegeSelection.length; i++) {
                        privilege.push(privilegeSelection[i].get("privilege_id_label"));
                    }
                    Ext.Ajax.request({
                        url: '/rolePrivilege/ajaxSave',
                        headers: {},
                        params: {'isJson': true, 'role': id, 'privilege': privilege},
                        method: 'POST',
                        success: function (response, options) {
                            Ext.MessageBox.alert('成功', "保存成功!");
                        },
                        failure: function (response, options) {
                            Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                        }
                    });
                } else {
                    Ext.MessageBox.alert("提示", "请选择角色!");
                }


            }
        }], true);

});

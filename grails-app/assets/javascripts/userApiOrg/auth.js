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
var AccountGrid;
Ext.onReady(function () {
    var treePanelTable = new ExtTreePanel(treePanel, {}, [{
        id: 'OrgTxt',
        xtype: 'tbtext',
        text: '组织 '
    },
        '-',
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
                    var apiOrg = [];
                    for (var i = 0; i < treeSelection.length; i++) {
                        apiOrg.push(treeSelection[i].get("id"));
                    }
                    Ext.Ajax.request({
                        url: '/userApiOrg/ajaxSave',
                        headers: {},
                        params: {'isJson': true, 'user': id, 'apiOrg': apiOrg},
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
                        id: 'con_account'
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
                    treePanelTable.treePanel

                ]

            }
        ]
    });
    UserGrid = new ExtGrid(userTable, {
        itemclick: function (view, record, item, index) {
            var id = record.get("user_id_label");
            Ext.Ajax.request({
                url: '/apiAccount/getApiAccounts',
                headers: {},
                params: {'isJson': true, 'user': id},
                method: 'GET',
                success: function (response, options) {
                    var responseText = response.responseText;
                    var parseJSON = $.parseJSON(responseText);
                    if (parseJSON.success) {
                        var idList = parseJSON.data['apiAccountList'];
                        AccountGrid.selectGrid(idList, 'apiAccount_id_label');
                    }

                },
                failure: function (response, options) {
                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                }
            });

            Ext.Ajax.request({
                url: '/apiOrg/getApiOrgs',
                headers: {},
                params: {'isJson': true, 'user': id},
                method: 'GET',
                success: function (response, options) {
                    var responseText = response.responseText;
                    var parseJSON = $.parseJSON(responseText);
                    if (parseJSON.success) {
                        var idList = parseJSON.data['apiOrgList'];
                        treePanelTable.selectTree(idList);
                    }

                },
                failure: function (response, options) {
                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                }
            })
        }
    });
    AccountGrid = new ExtGrid(accountTable, {}, [{
        id: 'AccountTxt',
        xtype: 'tbtext',
        text: '账户 '
    },
        '-',
        {
            id: 'SaveAccountBtn',
            text: '保存',
            xtype: 'button',
            icon: '/assets/menu/_save.gif',
            handler: function () {
                var userSelection = UserGrid.grid.getSelectionModel().getSelection();
                var accountSelection = AccountGrid.grid.getSelectionModel().getSelection();
                if (userSelection && userSelection.length > 0) {
                    var id = userSelection[0].get("user_id_label");
                    var apiAccount = [];
                    for (var i = 0; i < accountSelection.length; i++) {
                        apiAccount.push(accountSelection[i].get("apiAccount_id_label"));
                    }
                    Ext.Ajax.request({
                        url: '/userApiAccount/ajaxSave',
                        headers: {},
                        params: {'isJson': true, 'user': id, 'apiAccount': apiAccount},
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

});

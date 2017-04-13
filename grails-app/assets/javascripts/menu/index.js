Ext.Loader.setConfig({enabled: true});

function _GetURLPath() {
    var documentURL = document.URL;
    pos = documentURL.indexOf("//");
    pos = documentURL.indexOf("/", pos + 2);
    pos = documentURL.indexOf("/", pos + 1);
    return documentURL.substring(0, pos);
}
var URL_PATH = _GetURLPath();
Ext.require([
    'Ext.tip.QuickTipManager',
    'Ext.container.Viewport',
    'Ext.layout.*',
    'Ext.form.Panel',
    'Ext.form.Label',
    'Ext.data.*',
    'Ext.selection.*',
    'Ext.tab.Panel',
    'Ext.panel.*',
    'Ext.toolbar.*',
    'Ext.button.*',
    'Ext.container.ButtonGroup',
    'Ext.layout.container.Table'
]);

//
// This is the home layout definition.

Ext.onReady(function () {

    Ext.tip.QuickTipManager.init();
    var userName = $("#LoginUserName").html();

    var contentPanel = {
        id: 'content-panel',
//         title: '界面',
        collapsible: false,
        region: 'center', // this is what makes this panel into a region within the containing layout
        layout: 'card',
        margins: '2 5 5 0',
        activeItem: 0,
        border: true,
        items: [
            {
                xtype: 'tabpanel',
                id: 'card-tabs-panel',
                plain: true,  //remove the header border
                activeTab: 0,
                style: 'background-color:#dfe8f6; ',
                defaults: {bodyStyle: 'padding:3px'},
                items: [{
                    title: '开始',
                    html: '欢迎使用API管理系统！'
                }]
            }
        ]
//         html:'欢迎使用立邦电子商城系统！'
    };

    var store = Ext.create('Ext.data.TreeStore', {
        root: {
            expanded: true
        },
        proxy: {
            type: 'ajax',
            url: '/menu/getMenus'
        }
    });
//
//     // Go ahead and create the TreePanel now so that we can use it below
    var treePanel = Ext.create('Ext.tree.Panel', {
        id: 'tree-panel',
        title: '功能选择',
        region: 'north',
        split: true,
        height: 360,
        minSize: 150,
        rootVisible: false,
        autoScroll: true,
        store: store
    });
//
    treePanel.getSelectionModel().on('select', function (selModel, record) {
        if (record.get('leaf')) {
            var url = record.get('hrefTarget') + "?isFrame=true";
            var title = record.get('text');
            var id = record.internalId;
            if (id == "logout") {
                if (window.confirm('是否退出系统？')) {
                    window.location.href = url;
                }
                return;
            }


            var tabs = Ext.getCmp('card-tabs-panel');
            if (tabs.queryById(id)) {
                tabs.setActiveTab(id);
            } else {

                tabs.add({
                    closable: true,
                    id: id,
                    html: '<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
                    icon: '/assets/tabs.gif',
                    title: record.get('text')
                }).show();
            }
            treePanel.getSelectionModel().deselectAll();
//            var panel = Ext.getCmp('content-panel');
//            panel.setTitle(record.get('text'));
//            panel.body.update('<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="' + _GetURLPath() + url + '"></iframe>');

        }
    });

    // This is the Details panel that contains the description for each example layout.
    var detailsPanel = {
        id: 'details-panel',
        title: '记事簿',
        region: 'center',
        bodyStyle: 'padding-bottom:15px;background:#eee;',
        autoScroll: true,
        collapsible: false,
        html: '<textarea cols="30" rows="100" id="notepad" style="width:100%;height: 100%;overflow: auto;" ondragenter="this.focus();" onchange="_SetNote(this.value)"></textarea>'
    };


    // Finally, build the home layout once all the pieces are ready.  This is also a good
    // example of putting together a full-screen BorderLayout within a Viewport.
    Ext.create('Ext.Viewport', {
        layout: 'border',
        bodyBorder: false,
        items: [
            {
                xtype: 'box',
                id: 'header',
                region: 'north',
                collapsible: false,
                html: '<div style="height: 100%;font-size: 15px;line-height: 30px;padding-left: 7px;padding-right: 7px"> <span>API管理系统</span><span style="float: right">当前用户:' + userName + '</span></div>',
                height: 30
            }, {
                layout: 'border',
                collapsible: true,
                id: 'layout-browser',
                region: 'west',
                border: false,
                split: true,
                margins: '2 0 5 5',
                width: 175,
                minSize: 100,
                maxSize: 300,
                items: [treePanel, detailsPanel]
            },
            contentPanel
        ]
    });

    Ext.getElementById("notepad").value = _GetNote();
});


function _SetNote(sValue) {
    document.cookie = "notepad_value" + "=" + escape(sValue) + ";expires=Mon, 31 Dec 9999 23:59:59 UTC;";
}


function _GetNote() {
    // cookies are separated by semicolons
    var aCookie = document.cookie.split("; ");
    for (var i = 0; i < aCookie.length; i++) {
        // a name/value pair (a crumb) is separated by an equal sign
        var aCrumb = aCookie[i].split("=");
        if ("notepad_value" == aCrumb[0]) {
            if (aCrumb.length > 1)
                return unescape(aCrumb[1]);
            else
                return unescape("");
        }
    }

    // a cookie with the requested name does not exist
    return "";
}




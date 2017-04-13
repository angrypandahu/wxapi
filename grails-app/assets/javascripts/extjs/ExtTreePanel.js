function ExtTreePanel(arr, listeners, tbar) {
    listeners = listeners || {};
    tbar = tbar || [];
    var me = this;

//组织树数据源
    Ext.define(arr['name'], {
        extend: 'Ext.data.Model',
        fields: arr['fields']

    });
    this.orgTreeStore = Ext.create('Ext.data.TreeStore', {
        model: arr['name'],
        root: {
            expanded: true
        },
        proxy: {
            type: 'ajax',
            url: arr['url']
        }
    });

    this.selModel = Ext.create('Ext.selection.CheckboxModel', {
//                checkOnly:true,
        listeners: {
            beforedeselect: function () {

            }
        }

    });
// Go ahead and create the TreePanel now so that we can use it below
    this.treePanel = Ext.create('Ext.tree.Panel', {
        id: 'tree-panel',
        region: 'east',
        height: '100%',
        width: '100%',
        viewConfig: {
            plugins: {
                ptype: 'treeviewdragdrop'
            }
        },
//    height: 200,
//    minSize: 150,
        rootVisible: false,
        autoScroll: true,
        store: me.orgTreeStore,
        listeners: listeners,
        selModel: me.selModel,
        tbar: tbar,
        columns: arr['columns']

    });
    this.selectTree = function (idList) {
        var selectionModel = me.treePanel.getSelectionModel();
        selectionModel.deselectAll();
        for (var i = 0; i < idList.length; i++) {
            var id = idList[i];
            var nodeById = me.orgTreeStore.getNodeById(id);
            selectionModel.select(nodeById,true);
        }

    };

}
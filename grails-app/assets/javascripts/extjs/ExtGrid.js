function ExtGrid(arr, listeners, tbar, isSelModel) {
    listeners = listeners || {};
    tbar = tbar || {};
    isSelModel = isSelModel || false;
    var me = this;
    this.store = Ext.create('Ext.data.Store', {
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
    this.grid = Ext.create('Ext.grid.Panel', {
        header: false,
        store: me.store,
        columns: arr['columns'],
        height: "100%",
        width: "100%",
        listeners: listeners,
        selModel: isSelModel ? Ext.create('Ext.selection.CheckboxModel', {
                listeners: {
                    beforedeselect: function () {
                    }
                }
            }) : {},
        tbar: tbar,
        bbar: Ext.create('Ext.PagingToolbar', {
            store: me.store,
            displayInfo: true,
            displayMsg: '记录 {0} - {1} of {2}',
            emptyMsg: "无记录",
            items: [
                '-']
        }),
        renderTo: arr['renderTo']
    });
    this.store.loadPage(1);
    this.selectGrid = function (idList, field) {
        var selectionModel = me.grid.getSelectionModel();
        selectionModel.deselectAll();
        for (var i = 0; i < idList.length; i++) {
            var id = idList[i];
            for (var j = 0; j < me.store.getTotalCount(); j++) {
                var record = me.store.getAt(j);
                var fieldValue = record.get(field);
                if (fieldValue == id) {
                    selectionModel.select(record, true);
                }
            }
        }

    };

}
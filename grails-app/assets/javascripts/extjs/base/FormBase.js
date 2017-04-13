Ext.define('App.base.FormBase', {
    extend: 'Ext.form.Panel',
    xtype: 'form',
    layout: {
        type: 'vbox',
        padding: '3',
        margin: '5 0 0 0',
        align: 'stretch'
    },
    frame: true,
    fieldDefaults: {
        msgTarget: 'side',
        labelAlign: 'right',
        labelWidth: 75

    },
    Grid:null,
    items: null
});
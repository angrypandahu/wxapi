Ext.define('App.base.WindowBase', {
    extend: 'Ext.window.Window',
    title: 'title',
    width: 650,
    height: 430,
    layout: 'fit',
    modal: true,
    closable: true,
    maximizable: true,
    closeAction: 'hide',
    resizable: true,
    plain: true,
    border: false,
    items: null


});


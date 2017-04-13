var URL_PATH = _GetURLPath();
Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'App.base': URL_PATH + '/base'
    }
});
Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.util.*',
    'Ext.state.*',
    'Ext.selection.CheckboxModel'
]);

//ComboBox 数据源定义


function TData(arrPara) {
    if (!arrPara['name']) {
        alert("Missing object name when you create a new table!");
    }
    //Properties
    this.name = arrPara['name'];
    this.Grid = null;
    this.rs_main = new RecordSet();
    this.DataURL = arrPara['url'] ? arrPara['url'] : "";
    this.container = arrPara['container'] ? arrPara['container'] : "";
    this.PostValue = '{}';
    this.uniqeColumn = arrPara['uniqeColumn'] ? arrPara['uniqeColumn'] : -1;
    this.editColumn = arrPara['editColumn'] ? arrPara['editColumn'] : null;
    this.addDelete = arrPara['addDelete'];
    this.summary = arrPara['summary'] ? arrPara['summary'] : {};
    this.sortable = arrPara['sortable'] == undefined ? true : arrPara['sortable'];
    this.isRenderTo = arrPara['isRenderTo'];
//    this.ScreenWidth = 1366;
//    this.ScreenHeight = 768;
//    this.adjustRateW = 1;
//    this.adjustRateH = 1;
    this.height = arrPara['height'] ? arrPara['height'] : 0;
    this.width = arrPara['width'] ? arrPara['width'] : 0;

    this.cellEditing = null;
    this.itemDbClick = null;
    this.isFlex = arrPara['isFlex'] ? true : false;
    this.outerCheckSelect = arrPara['outerCheckSelect'];
    this.rowNumber = arrPara['rowNumber'];
    this.render = arrPara['render'];

    //wwg add 2013-2-2
    this.editColumnType = arrPara['editColumnType'];

    //Methods

    //After Url calling, Load data from response string.
    this.LoadData = function (data, withHead) {

        var isFirst = true;
        if (this.rs_main.FieldCount > 0) {
            isFirst = false;
        }


        var arrRowStr;
        if (data) {
            arrRowStr = data.split(_ROW_DELIM);
        } else {
            arrRowStr = [];
        }
        var arrRow = [];

        //deal with head type ...
        if (withHead) {
            if (arrRowStr.length < 4) {
                var errMsg = _getErrorPageMsg(data);
                if (errMsg) {
                    alert(errMsg);
                }
                return;
            }
            var arrAttr = arrRowStr.shift().split(_FIELD_DELIM);
            var arrHead = arrRowStr.shift().split(_FIELD_DELIM);
            var arrType = arrRowStr.shift().split(_FIELD_DELIM);
            var arrWidths = arrRowStr.shift().split(_FIELD_DELIM);
            var arrHides = arrRowStr.shift().split(_FIELD_DELIM);

            if (isFirst) {
                //Adjust Height and width with the screen hw
                this.AdjustHW(arrWidths, arrHides);
                //Head and Type
                this.rs_main.InitHead(arrAttr, arrHead, arrType, arrWidths, arrHides);
            }
        }

        //deal with rows
        for (iRow in arrRowStr) {
            arrRow.push(arrRowStr[iRow].split(_FIELD_DELIM));
        }
        this.rs_main.InitRecords(arrRow);

        //whether create grid
        if (!this.container) return this.AfterReset();


        var myData = this.GetData();
        var storeSet = {
            fields: this.GetField(),
            data: myData
        };
        //buffer 若超过400条记录，则采用Buffer方式，将不能排序。

        if (this.rs_main.RecordCount > 400) {
            storeSet['buffered'] = true;
            storeSet['pageSize'] = this.rs_main.RecordCount > 5000 ? this.rs_main.RecordCount : 5000;
        }
        ////if (this.rs_main.RecordCount > 2000) {
        //    storeSet['buffered'] = true;
        //    storeSet['pageSize'] = this.rs_main.RecordCount > 5000 ? this.rs_main.RecordCount : 5000;
        ////}

        //若存在可编辑字段，则将编辑结果保存到Recordset中
        if (this.editColumn) {
            this.cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1});
            storeSet['listeners'] = {
                update: {
                    fn: function (s, rec, op, names, eops) {
                        var rowIndex = s.data.indexOf(rec);
                        var colIndex = this.GetIndex(names);
//                    var value = rec.data[names];

                        this.rs_main.Records[rowIndex][colIndex] = rec.data[names];
                        this.rs_main.Edit(rowIndex);
                        this.AfterEdit(s, rec, op, names, eops);

                    }
                },
                scope: this
            };
        }

        var store = Ext.create('Ext.data.ArrayStore', storeSet);

        //Create columns automatically
        var column = this.GetColumn();

        //Add column's property after auto create.
        this.SetColumn(column);

        var gridPanel = {
            store: store,
//            multiSelect: false,
//            columnLines: true,
            stateId: 'stateGrid_' + this.name,
            features: [
                {ftype: 'summary'}
            ],
            bodyStyle: "height:100%;width:100%",
            columns: column,

//            width: this.width,
            autoScroll: true,
            header: false,
//            renderTo: this.container,
            iconCls: 'icon-grid',

            viewConfig: {
                stripeRows: true,
                enableTextSelection: true
            }
        };
        if (this.isRenderTo) {
            gridPanel['renderTo'] = this.container;
            gridPanel['height'] = $("#" + this.container).height();
        }
        if (this.editColumn) {
            gridPanel['plugins'] = [this.cellEditing];
            gridPanel['sortableColumns'] = false;
        }

        //外部check box 列
        if (this.outerCheckSelect) {
            var that = this;
            gridPanel['selModel'] = Ext.create('Ext.selection.CheckboxModel', {
//                checkOnly:true,
                listeners: {
                    beforedeselect: that.BeforeDeselect
                }

            });
        } else {
            gridPanel['multiSelect'] = false;


        }


        //若允许增加删除，则出现增加删除按钮
        if (this.addDelete) {
            gridPanel['dockedItems'] = [
                {
                    xtype: 'toolbar',
                    items: [
                        {
                            text: '增加',
                            id: this.name + 'addBtn',
//                    disabled:true,
                            icon: URL_PATH + '/images/add.gif',
                            handler: function () {
                                this.Add(0, new Array(this.rs_main.FieldCount));
                                if (this.editColumn) {
                                    for (key in this.editColumn) {
                                        this.cellEditing.startEdit(0, this.GetIndex(key));
                                        break;
                                    }
                                }
                            },
                            scope: this
                        },
                        '-',
                        {
                            itemId: this.name + 'btnDel',
                            text: '删除',
                            icon: URL_PATH + '/images/fam/delete.gif',
//                    disabled: true,
                            handler: function () {
                                this.Remove();
//                            Ext.getCmp(this.name + 'btnDel').setDisabled(true);
                            },
                            scope: this
                        }
                    ]
                }
            ];
        }

        this.SetGrid(gridPanel);
        if (gridPanel['listeners'] == null || gridPanel['listeners'] == "undefined") {
            gridPanel['listeners'] = {};
        }
        var othis = this;
        gridPanel['listeners'].sortchange = function (ct, column, direction, eOpts) {

            var store = this.getStore();
            var tempArr = [];
            store.each(function (rec) {
                var arr = othis.rs_main.AttributeNames[0];
                var uuid = rec.get(arr);
                for (var j = 0; j < othis.rs_main.Records.length; j++) {
                    if (uuid == othis.rs_main.Records[j][0]) {
                        tempArr.push(othis.rs_main.Records[j]);
                        break;
                    }
                }

            });
            othis.rs_main.Records = tempArr;


        };


//Below is grid deal code
        if (this.Grid) {
//            this.Grid.clearListeners();
            this.Grid.destroy();
            this.Grid = null;
        } else {

        }
        this.Grid = Ext.create('Ext.grid.Panel', gridPanel);

        //Clear AMD records
        this.rs_main.AddRecs = [];
        this.rs_main.EditRecs = [];
        this.rs_main.DelRecs = [];
        return this.Grid;
    };


    //Call url and get response string
//    this.Reset = function () {
//
//        this.Reset();
//
//    };

    this.Reset = function () {
        var grid = null;
        if (this.DataURL) {
            Ext.MessageBox.wait("数据加载中", "请等待");
            $.get(this.DataURL, {postkey: this.PostValue, callObj: this.name}, function (data, textStatus) {
                var obj;
                eval("obj = " + this.url.substring(this.url.indexOf("callObj") + 8));
                obj.LoadData(data, true);
                var container = Ext.getCmp(obj.container);
                if (container) {
                    container.add(obj.Grid).doLayout();
                }
                Ext.MessageBox.hide();
                obj.AfterReset();
            });
        }
    };
    this.SaveCookie = function (cookieName) {
        var arrRecord = this.rs_main.Records;
        var arrData = [];
        for (i in arrRecord) {
            arrData.push(arrRecord[i].join(_FIELD_DELIM));
        }
        var strData = arrData.join(_ROW_DELIM);
        _SetCookie(cookieName, strData);
    };


    this.LoadCookie = function (cookieName) {
        var strData = _GetCookie(cookieName);
        if (strData) this.LoadData(strData, false);
    };


    this.Excel = function (arrTerm) {
        var url;
        var excelTerm = "";
        if (this.rs_main.RecordCount <= 0) {
            alert("无记录可以导出！");
            return;
        }
        if (arrTerm) {
            excelTerm = arrTerm.join(_FIELD_DELIM);
        }

        if (this.DataURL.indexOf("?") > 0) {
            url = this.DataURL + "&excel=" + excelTerm;
        } else {
            url = this.DataURL + "?excel=" + excelTerm;
        }
        if (this.PostValue.length > 0) {
            url += "&postkey=" + this.PostValue;
        }
        open(encodeURI(url), "_self");

    };
    this.ExcelDirect = function (arrTerm) {
        var url;
        var excelTerm = "";
        //if (this.rs_main.RecordCount <= 0) {
        //    alert("无记录可以导出！");
        //    return;
        //}
        if (arrTerm) {
            excelTerm = arrTerm.join(_FIELD_DELIM);
        }

        if (this.DataURL.indexOf("?") > 0) {
            url = this.DataURL + "&excel=" + excelTerm;
        } else {
            url = this.DataURL + "?excel=" + excelTerm;
        }
        if (this.PostValue.length > 0) {
            url += "&postkey=" + this.PostValue;
        }
        open(url, "_self");

    };

//Get Column for grid
    this.GetColumn = function () {
        var arrColumn = [];
        var allHead = this.rs_main.Fields;
        var allType = this.rs_main.FieldTypes;
        var allIndex = this.rs_main.AttributeNames;
        var allWidth = this.rs_main.widths;
        var allHide = this.rs_main.Hides;

        //序号
        if (this.rowNumber) {
            arrColumn.push({xtype: 'rownumberer', width: 40});
        }

        for (var i = 0; i < this.rs_main.FieldCount; i++) {
            var oneColumn = [];
            oneColumn['text'] = allHead[i];
            if (allIndex[i] == "lockedColumn") {
                oneColumn['locked'] = true;
            }
            if (this.isFlex) {
                oneColumn['flex'] = parseInt(allWidth[i]);
            } else {
                oneColumn['width'] = parseInt(allWidth[i]);
            }
            oneColumn['hidden'] = allHide[i] == "true";
            oneColumn['dataIndex'] = allIndex[i];
            oneColumn['sortable'] = this.sortable;

            //处理Checkbox
            if (allIndex[i] == "checkbox") {
                oneColumn['xtype'] = 'checkcolumn';
                oneColumn['listeners'] = {checkchange: this.CheckboxChange};
            }


            if (this.editColumn && this.editColumn[allIndex[i]]) {
                //可编辑字段.
                if (this.editColumnType && this.editColumnType[allIndex[i]]) {
                    //wwg add 2013-2-2
                    oneColumn['editor'] = this.editColumnType[allIndex[i]];
                } else {
                    oneColumn['editor'] = type_Edit[allType[i]];
                }
            }

            //处理数字表现样式
            if (allType[i] == TYPE_DOUBLE || allType[i] == TYPE_INGEGER || allType[i] == TYPE_LONG) {
                oneColumn['align'] = 'right';
                oneColumn['xtype'] = 'numbercolumn';
                if (allType[i] == TYPE_DOUBLE) {
                    oneColumn['format'] = '0,000.00';
                } else {
                    oneColumn['format'] = '0,000';
                }
            }

            //处理合计
            if (this.summary[allIndex[i]]) {
                oneColumn['summaryType'] = this.summary[allIndex[i]];
                if (allType[i] == TYPE_DOUBLE) {
                    oneColumn['summaryRenderer'] = function (value) {
                        return Ext.util.Format.numberRenderer('0,000.00')(value);
                    }
                } else {
                    oneColumn['summaryRenderer'] = function (value) {
                        return Ext.util.Format.numberRenderer('0,000')(value);
                    }
                }

            }


            //处理render
            if (this.render && this.render[allIndex[i]]) {
                oneColumn['renderer'] = this.render[allIndex[i]];
//                oneColumn['sortable'] = false;
            }
            arrColumn.push(oneColumn);
        }

        return arrColumn;
    };


//Get data for grid
    this.GetData = function (isJson) {
        if (isJson) {
            var jsonData = [];
            var allIndex = this.rs_main.Fields;
            var allData = this.rs_main.Records;
            for (var iRow = 0; iRow < this.rs_main.RecordCount; iRow++) {
                var row = {};
                var rec = allData[iRow];
                for (var iCol = 0; iCol < this.rs_main.FieldCount; iCol++) {
                    row[allIndex[iCol]] = rec[iCol];
                }
                jsonData.push(row);
            }
            return jsonData;

        } else {
            return this.rs_main.Records;
        }


    };


//Get all fileds for grid.
    this.GetField = function (isJson) {

        if (isJson) {
            return this.rs_main.Fields;
        }

        var arrAllField = [];
        var allType = this.rs_main.FieldTypes;
        var allIndex = this.rs_main.AttributeNames;

        for (var i = 0; i < this.rs_main.FieldCount; i++) {

            var oneField = [];
            oneField['name'] = allIndex[i];
            oneField['type'] = type_convert[allType[i]];
            if (allType[i] == TYPE_DATE) {
                oneField['dateFormat'] = 'Y-m-d';
            }
            oneField['convert'] = null;
            oneField['defaultValue'] = undefined;

            arrAllField.push(oneField);
        }

        return arrAllField;
    };


//Adjust height and width with the screen WH
    this.AdjustHW = function (arrWid, arrHide) {
        if (this.container) {

            var sumWidth = 0;
            for (i = 0; i < arrWid.length; i++) {
                if (arrHide[i] != "true") {
                    sumWidth += parseInt(arrWid[i]);
                }
            }

//            var cont = $("#" + this.container);
//            if (this.width <= 0) {
//                this.width = cont.width();
//            }
//
//            if (this.height <= 0) {
//                this.height = cont.height();
//            }

            var alreadySum = 0;
            var lastShowColumn = 0;
            var fixW = 0;
            if (this.outerCheckSelect) fixW += 25;
            if (this.rowNumber) fixW += 40;
            var actualWidth = this.width - fixW;

            if (sumWidth < actualWidth) {
//                this.isFlex = true;
                for (var i = 0; i < arrWid.length; i++) {

                    arrWid[i] = actualWidth * (arrWid[i] / sumWidth);
                    if (arrHide[i] != "true") {
                        alreadySum += parseInt(arrWid[i]);
                        lastShowColumn = i;
                    }
                }
                arrWid[lastShowColumn] = arrWid[lastShowColumn] + actualWidth - alreadySum - 20;
            }

        }
    };


//根据字符串index返回数字index (eg. 'member' -> 4)
    this.GetIndex = function (index) {
//        var newIndex = index;
        if (isNaN(index)) {
            var newIndex = parseInt(this.rs_main.AttributeNames[index]);
            if (isNaN(newIndex) || newIndex > this.rs_main) {
                alert("AtrributeNames or Index error:" + index);
            }
        }
        return newIndex;
    };


//Add Record
//rowIndex -- a number, Add a new record before which rowIndex
//record -- a array.  the record array contain all fields according to the grid define.
    this.Add = function (rowIndex, record) {
        if (rowIndex < 0) {
            rowIndex = this.rs_main.RecordCount;
        }

        if (this.uniqeColumn > -1) {
            var arrSource = this.rs_main.Records;
            for (var iuc = 0; iuc < arrSource.length; iuc++) {
                if (record[this.uniqeColumn] == arrSource[iuc][this.uniqeColumn]) {
                    this.SetRow(record, iuc);
                    return;
                }
            }
        }

        if (this.Grid) {
            this.Grid.getStore().insert(rowIndex, [record]);
            this.rs_main.MoveTo(rowIndex);
            this.rs_main.Add(record);
        }

    };

//Remove all selected records from the grid and recordset
    this.Remove = function () {
        if (this.Grid) {
            var rows = this.Grid.getSelectionModel().getSelection();// 返回值为 Record 数组
            var store = this.Grid.getStore();
            var index;

            for (var i = rows.length - 1; i >= 0; i--) {
                index = store.data.indexOf(rows[i]);
                store.remove(rows[i]);

                this.rs_main.MoveTo(index);
                this.rs_main.Delete();
            }
        }
    };

    this.RemoveCheck = function () {
        if (this.Grid) {
            var store = this.Grid.getStore();
            var index;

            var count = store.count();
            for (var i = count - 1; i >= 0; i--) {
                var row = store.getAt(i);
                if (row.get("checkbox")) {
                    index = store.data.indexOf(row);
                    store.remove(row);

                    this.rs_main.MoveTo(index);
                    this.rs_main.Delete();
                }
            }

        }
    };

//Clear all data
    this.Clear = function () {
        if (this.Grid) {
            var store = this.Grid.getStore();
            var index;

            var count = store.count();
            for (var i = count - 1; i >= 0; i--) {
                var row = store.getAt(i);
                index = store.data.indexOf(row);
                store.remove(row);

                this.rs_main.MoveTo(index);
                this.rs_main.Delete();
            }

        }
    };


//Get Cell value
    /**
     * @return {string}
     */
    this.GetCell = function (colAttr) {
        var rec = this.Grid.getSelectionModel().getSelection();
        if (rec.length > 0) {
            return rec[0].get(colAttr);
        } else {
            return "";
        }
    };
//Update cell value
    this.SetCell = function (value, colIndex, rowIndex) {
        if (!colIndex) colIndex = 0;

        colIndex = this.GetIndex(colIndex);

        var rows = this.Grid.getSelectionModel().getSelection();// 返回值为 Record 数组
        var store = this.Grid.getStore();

        if (rowIndex == undefined) {
            if (rows.length > 0) {
                rowIndex = store.data.indexOf(rows[0]);
            } else {
                return;
            }
        } else if (isNaN(rowIndex)) {
            var rs = this.rs_main.Records;
            for (var irow = 0; irow < this.rs_main.RecordCount; irow++) {
                if (rs[irow][0] == rowIndex) {
                    rowIndex = irow;
                    break;
                }
            }
            if (isNaN(rowIndex)) return;

        }
        var record = store.data.getAt(rowIndex);
        if (this.rs_main.Records[rowIndex][colIndex] != value) {
            this.rs_main.Records[rowIndex][colIndex] = value;
            record.set(this.rs_main.AttributeNames[colIndex], value);
            this.rs_main.Edit(rowIndex);

        }
    };

    this.SetRow = function (recordUpd, rowIndex) {

        var rows = this.Grid.getSelectionModel().getSelection();// 返回值为 Record 数组

        if (rowIndex == undefined) {
            if (rows.length > 0) {
                rowIndex = this.Grid.store.data.indexOf(rows[0]);
            } else {
                return;
            }
        }

        if (rowIndex < this.rs_main.RecordCount) {
            var record = this.Grid.getStore().data.getAt(rowIndex);
            for (var i = 0; i < recordUpd.length; i++) {
                if (recordUpd[i] != undefined) {
                    record.set(this.rs_main.AttributeNames[i], recordUpd[i]);
                    this.rs_main.Records[rowIndex][i] = recordUpd[i];
                }
            }
            this.rs_main.Edit(rowIndex);
        }
    };


//Get Add Modify Delete records and combine them into a string
//arrCols -- which columns to get
    /**
     * @return {string}
     */
    this.GetAMDString = function (arrCols) {
        for (i in arrCols) {
            arrCols[i] = this.GetIndex(arrCols[i]);
        }
        var arrTemp = [];
        var arrAttr = [];

        arrAttr.push(this.rs_main.AttributeNames);

        var arrType = [];
        arrType.push(this.rs_main.FieldTypes);

        var strA = this.GetString(this.rs_main.AddRecs, arrCols, _ADD);
        var strM = this.GetString(this.rs_main.EditRecs, arrCols, _MODIFY);
        var strD = this.GetString(this.rs_main.DelRecs, arrCols, _DELETE);

        arrTemp.push(this.GetString(arrAttr, arrCols, "operationType"));
        arrTemp.push(this.GetString(arrType, arrCols, TYPE_STRING));
        if (strA) {
            arrTemp.push(strA);
        }
        if (strM) {
            arrTemp.push(strM);
        }
        if (strD) {
            arrTemp.push(strD);
        }
        return arrTemp.join(_ROW_DELIM);

    };


//Get AMD String Actually
//arrRecs -- record for get string content
//arrCols -- which columns to get
//strAMD -- after get string add what operation signal? (_ADD' _MODIFY or _DELETE )
    /**
     * @return {string}
     */
    this.GetString = function (arrRecs, arrCols, strAMD) {
        var arrRows = [];
        for (i in arrRecs) {
            if (arrRecs[i]) {
                var arrFields = [];
                if (strAMD) arrFields.push(strAMD);
                for (j in arrCols) {
                    arrFields.push(arrRecs[i][arrCols[j]]);
                }
                arrRows.push(arrFields.join(_FIELD_DELIM));
            }
        }
        return arrRows.join(_ROW_DELIM);
    };


//Get the last selected Record's AMD String
//operation --  what operation signal? (_ADD' _MODIFY or _DELETE )
//arrCols -- which columns to get
    /**
     * @return {string}
     */
    this.GetMapString = function (operation, arrCols) {
        var rows = this.Grid.getSelectionModel().getSelection();// 返回值为 Record 数组
        var store = this.Grid.getStore();
        var index;
        var arrRec = [];

        for (var i = 0; i < rows.length; i++) {
            index = store.data.indexOf(rows[i]);
            arrRec.push(this.rs_main.Records[index]);
            break; //Get top 1
        }

        if (arrRec.length > 0) {
            for (i in arrCols) {
                arrCols[i] = this.GetIndex(arrCols[i]);
            }

            var arrAttr = [];
            arrAttr.push(this.rs_main.AttributeNames);
            var arrType = [];
            arrType.push(this.rs_main.FieldTypes);

            var arrTemp = [];
            arrTemp.push(this.GetString(arrAttr, arrCols, "operationType"));
            arrTemp.push(this.GetString(arrType, arrCols, TYPE_STRING));
            arrTemp.push(this.GetString(arrRec, arrCols, operation));
            return arrTemp.join(_ROW_DELIM);
        }
    };

//判断当前Checkbox 列是否 有一个以上 被check
    this.isChecked = function () {

        if (this.outerCheckSelect) {
            var rows = this.Grid.getSelectionModel().getSelection();
            return rows.length > 0;

        } else {
            var index = this.GetIndex("checkbox");
            if (isNaN(index)) return false;

            var store = this.Grid.store;
            var count = store.count();
            for (var i = 0; i < count; i++) {
                if (store.getAt(i).get("checkbox")) {
                    return true;
                }
            }
        }

        return false;

    };


    /**
     * @return {string}
     */
    this.GetCheckString = function (index, opration, strCols) {
        var arrCols = this.GetCols(strCols);
        var arrTemp = [];
        var arrAttr = [];
        var arrType = [];
        var arrData = [];

        arrAttr.push(this.rs_main.AttributeNames);
        arrType.push(this.rs_main.FieldTypes);
        var arrRecord = this.rs_main.Records;

        if (this.outerCheckSelect) {
            var rows = this.Grid.getSelectionModel().getSelection();
            var store = this.Grid.getStore();
            for (var i = 0; i < rows.length; i++) {
                arrData.push(rows[i].raw);
            }
        } else {
            var store = this.Grid.store;
            var count = store.count();
            for (var i = 0; i < count; i++) {
                if (store.getAt(i).get(index)) {
                    arrData.push(arrRecord[i]);
                }
            }
        }

        arrTemp.push(this.GetString(arrAttr, arrCols, "operationType"));
        arrTemp.push(this.GetString(arrType, arrCols, TYPE_STRING));
        arrTemp.push(this.GetString(arrData, arrCols, opration));

        return arrTemp.join(_ROW_DELIM);

    };
    this.GetCols = function (strCols) {
        var arrCols;
        if (!strCols) {
            arrCols = [];
            for (var i = 0; i < this.rs_main.FieldCount; i++) {
                arrCols[i] = i;
            }
        } else {
            arrCols = strCols.split(_FIELD_DELIM);
            for (i in arrCols) {
                arrCols[i] = this.GetIndex(arrCols[i]);
            }
        }
        return arrCols;
    };
//Event
    this.BeforeDeselect = function () {
    };
    this.AfterReset = function () {
    };
    this.SetColumn = function () {
    };
    this.SetGrid = function () {
    };
    this.CheckboxChange = function () {
    };
    this.AfterEdit = function () {
    };


}


/******************************************* RecordSet Object *******************************/
function RecordSet() {
    this.Records = null;
    this.Records_Source = null;
    this.Fields = null;
    this.FieldTypes = null;
    this.AttributeNames = null;
    this.widths = null;
    this.Hides = null;

    this.FieldCount = -1;
    this.RecordCount = -1;
    this.AbsolutePosition = -1;


    this.InitHead = RecordSet_InitHead;
    this.InitRecords = RecordSet_InitRecords;
    this.MoveTo = RecordSet_MoveTo;
    this.Delete = RecordSet_Delete;
    this.Add = RecordSet_Add;
    this.Edit = RecordSet_Edit;

    this.DelRecs = [];
    this.AddRecs = [];
    this.EditRecs = [];

}

function RecordSet_InitHead(arrAttr, arrHead, arrType, arrWidth, arrHide) {
    this.AttributeNames = arrAttr; //Set Attribute index
    this.Fields = arrHead;
    this.widths = arrWidth;
    this.Hides = arrHide;
    this.FieldCount = arrHead.length;
    this.FieldTypes = [];


    for (iCol in arrType) {
        this.AttributeNames[arrAttr[iCol]] = iCol; //Set Attribute Key-Value
        this.FieldTypes[iCol] = (arrType[iCol]);
    }
}

function RecordSet_InitRecords(arrRecord) {
    var iCol;
    var iRow;
    for (iCol = 0; iCol < this.FieldTypes.length; iCol++) {
        if (this.FieldTypes[iCol].indexOf(TYPE_INGEGER) > -1 || this.FieldTypes[iCol].indexOf(TYPE_LONG) > -1) {
            for (iRow = 0; iRow < arrRecord.length; iRow++) {
                arrRecord[iRow][iCol] = parseInt(arrRecord[iRow][iCol]);
            }
        }

        if (this.FieldTypes[iCol].indexOf(TYPE_DOUBLE) > -1) {
            for (iRow = 0; iRow < arrRecord.length; iRow++) {
                arrRecord[iRow][iCol] = parseFloat(arrRecord[iRow][iCol]);
            }
        }

//        if (this.AttributeNames[iCol] == "checkbox") {
//            for (iRow = 0; iRow < arrRecord.length; iRow++) {
//                arrRecord[iRow][iCol] = arrRecord[iRow][iCol]?1:0;
//            }
//        }

    }

    this.Records = arrRecord;
    this.Records_Source = arrRecord;
    this.RecordCount = arrRecord.length;
    if (this.AbsolutePosition >= this.RecordCount) {
        this.AbsolutePosition = this.RecordCount - 1;
    }
}

function RecordSet_MoveTo(index) {
    if (index >= this.RecordCount) {

        this.AbsolutePosition = -1;
    } else {
        this.AbsolutePosition = index;
    }
}

function RecordSet_Delete() {
    if (this.AbsolutePosition >= 0 && this.AbsolutePosition < this.RecordCount && this.Records != null) {
        var delRec = this.Records[this.AbsolutePosition];
        this.Records.splice(this.AbsolutePosition, 1);
        if (this.Records != this.Records_Source) {
            _ArrayRemove(this.Records_Source, delRec);
        }
        //Del Records
        this.DelRecs.push(delRec);
        //Add Records
        for (i in this.AddRecs) {
            if (delRec == this.AddRecs[i]) {
                this.AddRecs[i] = null;
                this.DelRecs.pop();
                break;
            }
        }

        //Edit Records
        for (i in this.EditRecs) {
            if (delRec == this.EditRecs[i]) {
                this.EditRecs[i] = null;
                break;
            }
        }
        //update record_source
        if (this.Records != this.Records_Source) {
            for (iRow in this.Records_Source) {
                if (this.Records_Source[iRow] == delRec) {
                    this.Records.splice(iRow, 1)
                }
            }
        }

        this.RecordCount = this.Records.length;
        this.AbsolutePosition = -1;
    }
}

function RecordSet_Add(arrRecord) {

    if (this.Records != null) {
        if (!arrRecord) {
            arrRecord = new Array(this.FieldCount);
            for (var i = 0; i < this.FieldCount; i++) {
                arrRecord[i] = "";
            }
        }

        if (this.AbsolutePosition < 0) {
            this.AbsolutePosition = this.RecordCount;
            this.Records.push(arrRecord);
        } else {
            this.Records.splice(this.AbsolutePosition, 0, arrRecord);
        }

        if (this.Records != this.Records_Source) {
            _ArrayInsert(this.Records_Source, this.Records[this.AbsolutePosition], arrRecord);
        }
        this.AddRecs.push(arrRecord);
        this.RecordCount = this.Records.length;


    }
}

function RecordSet_Edit(index) {
    //Add Records
    for (i in this.AddRecs) {
        if (this.Records[index] == this.AddRecs[i]) {
            return;
        }

    }

    for (iE in this.EditRecs) {
        if (this.EditRecs[iE] == this.Records[index]) {
            return;
        }
    }

    this.EditRecs.push(this.Records[index]);
}

/********************************************* Combobox DS ********************************************/
//var COMBO_ORDER_TYPE = "cus_order_type";
//
//var COMBO_STATE = "org_state";
//var COMBO_CITY = "org_city";
//var COMBO_DISTRICT = "org_district";


function ComboBoxDS(objName, comboType, condition) {
    this.objName = objName;
    var urlPara = "?type=";
    if (comboType) {
        urlPara += comboType;
    }

    if (condition) {
        if (condition && typeof(condition) == "string" && condition.indexOf("'") != 0) {
            condition = "'" + condition + "'";
        }
        urlPara += "&condition=" + condition;
    }

    // Define the model for a State
    Ext.define('comboBoxFixModel', {
        extend: 'Ext.data.Model',
        fields: [
            {type: 'string', name: 'value'},
            {type: 'string', name: 'name'}
        ]
    });

    this.states = [];

    this.Reload = function (comboType, condi, combo) {

        if (typeof(condi) == "string") {
            condi = "'" + condi + "'";
        }
        var urlPara = "?type=";
        if (comboType) urlPara += comboType;
        if (condi) urlPara += "&condition=" + condi;
        this.Store.proxy.url = encodeURI(URL_PATH + "/common/ComboDataAction.do" + urlPara);


        if (combo) {
            this.refCombo = combo;
            this.value = combo.getValue();
            combo.clearValue();

        } else {
            this.refCombo = null;
            this.value = "";
        }
        this.Store.load();
    };

    this.Store = Ext.create('Ext.data.Store', {
        autoDestroy: true,
        storeId: this.objName,
        model: 'comboBoxFixModel',
        proxy: {
            type: 'ajax',
            url: URL_PATH + "/common/ComboDataAction.do" + urlPara
        },
        autoLoad: true
    });

    this.value = "";
    this.refCombo = null;
    this.Store.load();
    this.Store.addListener("load", function (st) {
        var obj;
        eval("obj = " + st.storeId);
        if (obj && obj.refCombo && obj.value) obj.refCombo.setValue(obj.value);
    });

    this.GetStore = function () {

        return this.Store;
    };

}

/********************************************* End Combobox DS ***************************************/


/****************************************** Save Function **********************************************/

function SaveData(objName) {
    if (!objName) {
        alert("Missing object name when you create a save data object!");
    }
    //
    this.name = objName;
    this.Action = "";
    this.agent = "";
    if (navigator.userAgent.toLowerCase().indexOf("firefox") > 0) {
        this.agent = "firefox";
    }

    /**
     * @return {string}
     */
    this.GetUrl = function () {
        var url;
        var condition = "mapstring=" + this.mapstring;
        condition += "&collectionstring=" + this.strCollection;
        condition += "&reserve1=" + this.strReserve1;
        condition += "&reserve2=" + this.strReserve2;
        if (this.Action.indexOf("?") > 0) {
            url = this.Action + "&" + condition
        } else {
            url = this.Action + "?" + condition
        }
        return encodeURI(url);
    };

    this.Open = function () {

        open(this.GetUrl(), "_self");
    };

    this.PrintHideFrame = function () {
        if (window.ActiveXObject) {
            //IE
            window.open(this.GetUrl(), null, "left=3000,top=0,height=600,width=1010", false);
        } else {
            //other
            var hideiframe = $("#hide_iframe");
            if (!(hideiframe.is("iframe"))) {
                $("body").append('<iframe id="hide_iframe" style="position:absolute;z-index:999; top:0; left:0; height:0;width:0;"></iframe>');
            }

            hideiframe.attr("src", this.GetUrl());
        }
//        if (printHidden) {
//            printHidden(this.GetUrl());
//        }
    };
    this.Save = function () {

        Ext.MessageBox.wait("数据加载中", "请等待");
        var url;
        if (this.Action.indexOf("?") > 0) {
            url = this.Action + "&" + "callObj=" + this.name;
        } else {
            url = this.Action + "?" + "callObj=" + this.name;
        }
//        SaveData_CallBackObj = this.name;
        $.post(url, {
                mapstring: this.strMap,
                collectionstring: this.strCollection,
                reserve1: this.strReserve1,
                reserve2: this.strReserve2,
                agent: this.agent
            },
            function (data, textStatus) {
                var obj;
                eval("obj = " + this.url.substring(this.url.indexOf("callObj") + 8));

                var arrRetMsg = data.split(_FIELD_DELIM);
                obj.strUUID = arrRetMsg[0];
                obj.strCode = arrRetMsg[1];
                obj.strErrorMsg = arrRetMsg[2];
                obj.strBackMsg = arrRetMsg[3];
                var arrRec = arrRetMsg[3].split(_ROW_DELIM);

                obj.records = [];
                for (var irec = 0; irec < arrRec.length; irec++) {
                    obj.records.push(arrRec[irec].split(_COL_DELIM_BAK))
                }

                obj.AfterSave();
                obj.strMap = "";
                obj.strCollection = "";
                obj.strReserve1 = "";
                obj.strReserve2 = "";
                Ext.MessageBox.hide();
            })
    };
    this.AfterSave = function () {
    };

    //Set value
    this.strMap = "";
    this.strCollection = "";
    this.strReserve1 = "";
    this.strReserve2 = "";


    //return value
    this.strUUID = "";
    this.strCode = "";
    this.strBackMsg = "";
    this.strErrorMsg = "";
    this.records = null;
}


/****************************************** Get Form Data **********************************************/
//rawVal- comboBox text show is the value.
function FormData(objName) {
    if (!objName) {
        return;
    }

    this.name = objName;
    this.Action = URL_PATH + '/common/FormDataAction.do';
    this.Form = null;
    //entity . null : the first find entity
    //         val:  the fixed entity
    this.Get = function (form, uuid, entity) {
        this.Form = form;

        //Get All from data
        var allFields = form.getFields();
        var arrHead = [];
        for (var i = 0; i < allFields.getCount(); i++) {
            var field = allFields.getAt(i);
            var arrId = field.id.split("-");
            if ((arrId[0].indexOf(FORM_RW_SIG) == 0 || arrId[0].indexOf(FORM_RO_SIG) == 0 ) && arrId[1] != 'operationType') {
                arrHead.push(arrId[1]);
            }
        }

        Ext.MessageBox.wait("数据加载中", "请等待");
        var url;
        if (this.Action.indexOf("?") > 0) {
            url = this.Action + "&" + "callObj=" + this.name;
        } else {
            url = this.Action + "?" + "callObj=" + this.name;
        }

        $.post(url, {head: arrHead.join(_FIELD_DELIM), UUID: uuid, entityType: entity},
            function (data, textStatus) {
                var obj;
                eval("obj = " + this.url.substring(this.url.indexOf("callObj") + 8));

                var arrRecord = data.split(_ROW_DELIM);
                for (var i = 0; i < arrRecord.length; i++) {
                    var rec = arrRecord[i].split(_FIELD_DELIM);
                    if (rec.length > 1) {
                        var field = obj.Form.findField(PRE_RW_ID + rec[0]);
                        if (!field) {
                            field = obj.Form.findField(PRE_RW_ID1 + rec[0]);
                        }
                        if (!field) {
                            field = obj.Form.findField(PRE_RW_ID2 + rec[0]);
                        }
                        if (!field) {
                            field = obj.Form.findField(PRE_RW_ID3 + rec[0]);
                        }
                        if (!field) {
                            field = obj.Form.findField(PRE_RO_ID + rec[0]);
                        }
                        if (!field) {
                            field = obj.Form.findField(PRE_RO_ID1 + rec[0]);
                        }
                        if (!field) {
                            field = obj.Form.findField(PRE_RO_ID2 + rec[0]);
                        }
                        if (!field) {
                            field = obj.Form.findField(PRE_RO_ID3 + rec[0]);
                        }
                        if (field) {
                            if (field.format == 'Y-m-d') {
                                field.setValue(rec[1].substring(0, 10))
                            } else if (field.format == 'Y-m') {
                                field.setValue(rec[1].substring(0, 7))
                            } else {
                                field.setValue(rec[1]);
                            }

                        }
                    }
                }
                Ext.MessageBox.hide();
                obj.AfterGet();
            })
    };
    this.AfterGet = function () {
    };

    //Set value
    this.strMap = "";
    this.strCollection = "";
    this.strReserve1 = "";
    this.strReserve2 = "";


    //return value
    this.strUUID = "";
    this.strCode = "";
    this.strBackMsg = "";
    this.strErrorMsg = "";
}


/****************************************** open new dialog (iframe) *************************************/

function _OpenHideFrame(url) {
    var hideiframe = $("#hide_iframe");
    if (!(hideiframe.is("iframe"))) {
        $("body").append('<iframe id="hide_iframe" style="position:absolute;z-index:999; top:0; left:0; height:180px;width:180px;display: block;"></iframe>');
    }
    hideiframe.attr("src", url);
}


/****************************************** Normal Function *********************************************
 * @return {string}
 */
function TData_ToRe(key) {
    if (key == undefined) key = "";
    key = key + "";

    if (this.UseHTML)  return key;
    key = key.replace(/&/g, "&amp;");
    key = key.replace(/</g, "&lt;");
    key = key.replace(/>/g, "&gt;");
    key = key.replace(/  /g, " &nbsp;");
    key = key.replace(/"/g, "&quot;");
    return key;
}

function _IsNumberType(type) {
    return type == TYPE_INGEGER || type == TYPE_LONG || type == TYPE_DOUBLE;
}

function _IsRightAlign(type) {
    return type == TYPE_INGEGER || type == TYPE_LONG || type == TYPE_DOUBLE || type == TYPE_DATE;
}

function _GetURLPath() {
    var documentURL = document.URL;
    pos = documentURL.indexOf("//");
    pos = documentURL.indexOf("/", pos + 2);
    pos = documentURL.indexOf("/", pos + 1);
    return documentURL.substring(0, pos);
}

function _GetPageMapString(form, rawValComp) {
    var _PageMapHead = "";
    var _PageMapType = "";
    var _PageMapData = "";

    var allFields = form.getFields();
    for (var i = 0; i < allFields.getCount(); i++) {
        var field = allFields.getAt(i);
        var arrId = field.id.split("-");
        var val = field.getValue();
        if (val == null) val = "";
        if (arrId[0].indexOf(FORM_RW_SIG) == 0) {
            _PageMapHead += (_FIELD_DELIM + arrId[1]);
            _PageMapType += (_FIELD_DELIM + field.name);

            if (rawValComp && rawValComp.indexOf(arrId[1] + ",") > -1) {
                val = field.getRawValue();
            }
            if (field.name == TYPE_DATE && val && val.length != 10) {
                val = field.getRawValue();
            }

            if ((field.name == TYPE_INGEGER || field.name == TYPE_DOUBLE )) {
                if ((isNaN(val) || val == null)) {
                    val = 0;
                }
                if ((typeof val) == "string") {
                    val.replace(/,/g, "")
                }

            }
//            if (field['xtype'] == 'radiogroup') {
//                var checked = field.getChecked();
//                if (checked.length == 1) {
//                    val = checked[0]['inputValue'];
//                }else{
//                    val = null;
//                }
//            }
            _PageMapData += (_FIELD_DELIM + val);

        }
    }

    return _PageMapHead + _ROW_DELIM + _PageMapType + _ROW_DELIM + _PageMapData;
}


function isNaD(source) {
    var reExp = /^(|[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]))$/;
    return !reExp.exec(source);
}


function _SetCookie(sName, sValue) {
    document.cookie = sName + "=" + escape(sValue) + ";expires=Mon, 31 Dec 9999 23:59:59 UTC;";
}


function _GetCookie(sName) {
    // cookies are separated by semicolons
    var aCookie = document.cookie.split("; ");
    for (var i = 0; i < aCookie.length; i++) {
        // a name/value pair (a crumb) is separated by an equal sign
        var aCrumb = aCookie[i].split("=");
        if (sName == aCrumb[0]) {
            if (aCrumb.length > 1)
                return unescape(aCrumb[1]);
            else
                return unescape("");
        }
    }

    // a cookie with the requested name does not exist
    return null;
}

function _ArrayInsert(arrayObjs, oriObj, newObj) {
    var len = arrayObjs.length;
    for (var i = 0; i < len; i++) {
        if (arrayObjs[i] == oriObj) {
            arrayObjs.splice(i + 1, 0, newObj);
            return;
        }
    }
}

function _ArrayRemove(arrayObjs, oriObj) {
    var len = arrayObjs.length;
    for (var i = 0; i < len; i++) {
        if (arrayObjs[i] == oriObj) {
            arrayObjs.splice(i, 1);
            return;
        }
    }
}

function _ArrayFindIndex(arrayObjs, oriObj) {
    var len = arrayObjs.length;
    for (var i = 0; i < len; i++) {
        if (arrayObjs[i] == oriObj) {
            return i;
        }
    }
}

function _excel(url) {
    open(encodeURI(url), "_self");
}

function _getErrorPageMsg(pageData) {
    var start = pageData.indexOf("var strErrorMsg = ");
    var end = pageData.indexOf("var strCode");
    if (start > 0 && end > 0) {
        start += 19;
        end -= 3;
        return pageData.substring(start, end);
    } else {
        return null;
    }
}

function _TrimNumber(number, precision, type) {

    if (number == 0 || isNaN(number)) {
        return "0";
    }

    var arrNum = number.toString().split(".");
    var iIndex = arrNum[0].length;
    var strTemp = "";
    var iBegin = 0;
    var iEnd = iIndex % 3;

    while (iEnd < iIndex) {
        if (iEnd > 0) {
            strTemp += arrNum[0].slice(iBegin, iEnd) + ",";
        }
        iBegin = iEnd;
        iEnd += 3;
    }
    strTemp += arrNum[0].slice(iBegin, iEnd);
    arrNum[0] = strTemp;

    if (type.indexOf(TYPE_INGEGER) > -1 || type.indexOf(TYPE_LONG) > -1) {
        return arrNum[0];
    }

    //
    if (isNaN(arrNum[1])) {
        arrNum[1] = "";
    }
    arrNum[1] += "000000000";
    arrNum[1] = arrNum[1].slice(0, precision);

    return arrNum[0] + "." + arrNum[1];
}

var formatMoney = function (v) {
    return Ext.util.Format.currency(v, '￥', 2);

};

function GetToDoStore(name) {
    return Ext.create('Ext.data.Store', {
        fields: ['name', 'value'],
        proxy: {
            type: 'ajax',
            url: URL_PATH + '/biz/task/GetCustomerToDo.do?comboName=' + name,
            reader: {
                type: "json",
                root: "contents"
            }
        },
        listeners: {
            load: function (s, recs) {
            }
        },
        autoLoad: true

    });

}

function _GetPageMapString_2(arrId, arrField, arrVal) {
    var _PageMapHead = "";
    var _PageMapType = "";
    var _PageMapData = "";

    for (var i = 0; i < arrId.length; i++) {
        if (arrVal == null) arrVal = "";
        _PageMapHead += (_FIELD_DELIM + arrId[i]);
        _PageMapType += (_FIELD_DELIM + arrField[i]);
        _PageMapData += (_FIELD_DELIM + arrVal[i]);
    }
    return _PageMapHead + _ROW_DELIM + _PageMapType + _ROW_DELIM + _PageMapData;
}

function addOrCancelRequired(cmp, isRequired) {
    if (cmp) {
        var oldLabel = cmp.getFieldLabel();
        var newLabel = oldLabel;
        if (oldLabel.indexOf(required) > -1 && !isRequired) {
            newLabel = oldLabel.substr(0, oldLabel.indexOf(required));
            cmp.allowBlank = true;
        } else if (oldLabel.indexOf(required) < 0 && isRequired) {
            newLabel = oldLabel + required;
            cmp.allowBlank = false;
        }
        cmp.setFieldLabel(newLabel);
    }

}
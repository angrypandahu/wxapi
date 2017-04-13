<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="extjs"/>
    <g:set var="entityName" value="${message(code: 'user.label', default: 'Menu')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <script>
        var fields = $.parseJSON('<g:writeWithoutEncoding input="${tableData.getFields()}"/>');
        var columns = $.parseJSON('<g:writeWithoutEncoding input="${tableData.getColumns()}"/>');
        var datas = $.parseJSON('<g:writeWithoutEncoding input="${tableData.getDatas()}"/>');
        console.log(fields, columns, datas);
    </script>
    <asset:javascript src="user/extIndex.js"/>

</head>

<body>

</body>
</html>
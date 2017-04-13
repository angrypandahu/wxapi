<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="extjs"/>
    <g:set var="entityName" value="${message(code: 'user.label', default: 'Menu')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <script>
        var userTable = $.parseJSON('<g:writeWithoutEncoding input="${userTable}"/>');
        var roleTable = $.parseJSON('<g:writeWithoutEncoding input="${roleTable}"/>');
        var privilegeTable = $.parseJSON('<g:writeWithoutEncoding input="${privilegeTable}"/>');
        var treePanel = $.parseJSON('<g:writeWithoutEncoding input="${treePanel}"/>');

    </script>
    <asset:javascript src="extjs/ExtTreePanel.js"/>
    <asset:javascript src="privilege/auth.js"/>

</head>

<body>

</body>
</html>
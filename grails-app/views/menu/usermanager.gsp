<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="extjs"/>
    <g:set var="entityName" value="${message(code: 'user.label', default: 'Menu')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <script>
        var userTable = $.parseJSON('<g:writeWithoutEncoding input="${userTable}"/>');
        var roleTable = $.parseJSON('<g:writeWithoutEncoding input="${roleTable}"/>');

    </script>
    <asset:javascript src="menu/usermanager.js"/>

</head>

<body>

</body>
</html>
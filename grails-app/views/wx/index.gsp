<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="frame"/>
    <g:set var="entityName" value="${message(code: 'wx.label', default: 'WxApi')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>

</head>

<body>

<g:wxSelect field="apiAccount" from="${apiAccountList}" optionKey="id" optionValue="name"/>

</body>
</html>
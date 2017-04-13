<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'userApiOrg.label', default: 'UserApiOrg')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
        <style>
        .container-fluid,.col-md-10{
            padding-left:0
        }
        </style>
    </head>
    <body>
        <a href="#list-userApiOrg" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="col-md-10">
                <div id="list-userApiOrg" class="content scaffold-list" role="main">
                    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
                    <g:if test="${flash.message}">
                        <div class="message" role="status">${flash.message}</div>
                    </g:if>
                    <f:table collection="${userApiOrgList}" />

                    <div class="pagination">
                        <g:paginate total="${userApiOrgCount ?: 0}" />
                    </div>
                </div>

                %{--<ul id="treeDemo" class="ztree"></ul>--}%
            </div>

            <div class="col-md-2">

            </div>
        </div>
    </div>

    </body>
</html>
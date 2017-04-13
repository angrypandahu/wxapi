<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>
        <g:layoutTitle default="IColor Report"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:stylesheet src="ext-theme-classic-all.css"/>
    <asset:javascript src="extjs/build/ext-all.js"/>
    <asset:javascript src="extjs/ext-locale-zh_CN.js"/>
    <asset:javascript src="extjs/build/ext-theme-classic.js"/>

    <asset:javascript src="jquery-2.2.0.min.js"/>
    <asset:javascript src="extjs/Constants.js"/>
    <asset:javascript src="extjs/ExtGrid.js"/>

    <g:layoutHead/>

</head>

<body>
<div id="LoginUserName" style="display: none">
    <sec:username field="username"/>
</div>

<g:layoutBody/>

</body>
</html>

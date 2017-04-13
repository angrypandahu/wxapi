<%@ page import="com.domain.wechat.WxGroup" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'wxUser.label', default: 'WxUser')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:javascript src="fp.js"/>
    <asset:stylesheet src="fp.css"/>
    <asset:javascript src="jquery-2.2.0.min.js"/>

    <script type="text/javascript">
        function sendMsgToWxUser(wxUserId) {
            var data = {};
            data['id'] = wxUserId;
            data['msg'] = $("#msg_" + wxUserId).val();
            if (!data['msg']) {
                alert("请输入内容");
                return
            }
            $.ajax({
                url: '/wxUser/sendMsg', type: 'POST', data: data, success: function (res, status) {
                    var parseJSON = $.parseJSON(res);
                    if (parseJSON['errcode'] == 0) {
                        $("#msg_" + wxUserId).val("");
                        alert("send Success!");
                    }
                }, error: function (res) {
                }
            });

        }
        $(function () {
            $("select[name=wxGroup]").change(function (a, b, c) {
                var othis = $(this);
                var wxGroupId = othis.val();
                var wxUserId = othis.attr("data-wxUserId");
                var data = {};
                data['wxGroupId'] = wxGroupId;
                data['wxUserId'] = wxUserId;
                $.ajax({
                    url: '/wx/wxGroupUserUpdate', type: 'POST', data: data, success: function (res, status) {
                        alert(res);
                    }, error: function (res) {
                    }
                });

            })

        })
    </script>

</head>

<body>
%{--<filterpane:filterPane domain="com.domain.wechat.WxUser" dialog="true"--}%
%{--associatedProperties="wxGroup.id"--}%
%{--filterPropertyValues="${['wxGroup.id':--}%
%{--[values: com.domain.wechat.WxGroup.executeQuery('select t.name from WxGroup t ')]]}"/>--}%

<filterpane:filterPane domain="com.domain.wechat.WxUser" dialog="true" filterProperties="nickname,wxGroup,apiAccount"
                       associatedProperties="wxGroup.name,apiAccount.name"
                       filterPropertyValues="${['wxGroup.name': [values: com.domain.wechat.WxGroup.executeQuery('select t.name from WxGroup t ')], 'apiAccount.name': [values: com.domain.api.ApiAccount.executeQuery('select t.name from ApiAccount t ')]]}"/>
<a href="#list-wxUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><filterpane:filterButton text="Search"/></li>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>

    </ul>
</div>


<div id="list-wxUser" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <table class="table">
                    <thead>
                    <tr>
                        <th>
                            photo
                        </th>
                        <th>
                            nickname
                        </th>
                        <th>
                            address
                        </th>
                        <th>
                            group
                        </th>
                        <th>

                        </th>

                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${wxUserList}">
                        <tr>
                            <td>
                                <img src="${it.headimgurl}" style="height: 75px;width: 75px" class="img-thumbnail">
                            </td>
                            <td>
                                ${it.getStringNickname()}
                            </td>
                            <td>
                                ${it.country + it.getStringProvince() + it.getStringCity()}
                            </td>

                            <td>
                                <g:select name="wxGroup" optionKey="id" optionValue="name" from="${wxGroupList}"
                                          data-wxUserId="${it.id}"
                                          value="${it.getGroup().id}"/>
                            </td>
                            <td>
                                <form role="form">
                                    <div class="form-group">
                                        <input type="email" class="form-control" id="msg_${it.id}"/>
                                    </div>
                                    <button type="button" class="btn btn-success" onclick="sendMsgToWxUser(${it.id})">
                                        send
                                    </button>

                                </form>
                            </td>
                        </tr>

                    </g:each>

                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="pagination">
        <g:paginate total="${wxUserCount ?: 0}" params="${filterParams}"/>
    </div>
</div>
</body>
</html>
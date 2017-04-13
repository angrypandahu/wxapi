<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="frame"/>
    <g:set var="entityName" value="${message(code: 'wx.label', default: 'WxApi')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    <script>
        $(function () {
            var resultHtml = '<div class="row"> <div class="col-md-12"> <div class="alert alert-dismissable alert-info"> <button type="button" class="close" data-dismiss="alert" aria-hidden="true"> × </button> <h4> Result! </h4> RESULT_INFO </div> </div > </div> ';

            $('#wxApiFormId').ajaxForm({
                success: function (data) {
                    if (data) {
                        $('#wxFormContainer').append(resultHtml.replace("RESULT_INFO", data));
                        $wxFormSubmitId.attr("disabled", false);
                    }
                }, error: function (error) {
                    alert(error);
                    $wxFormSubmitId.attr("disabled", false);

                }


            });
            var $wxFormSubmitId = $("#wxFormSubmitId");
            $wxFormSubmitId.click(function () {
                $wxFormSubmitId.attr("disabled", true);
                $('#wxApiFormId').submit();
            });
            $wxFormSubmitId.attr("disabled", false);

        });

    </script>
</head>

<body>
<g:hiddenField name="apiAccount" value="${apiAccount.id}"/>
<g:wxForm apiOrg="${apiOrg}"/>
</body>
</html>

<div class="container-fluid" id="wxFormContainer">
    <div class="row">
        <div class="col-md-12">
            <form role="form" action="${apiOrg.url}" method="${apiOrg.method}" id="wxApiFormId">
                <g:hiddenField name="account" value="${apiAccount.id}"/>
                <div class="form-group">
                    <label>
                        账号
                    </label>

                    ${apiAccount.name}
                </div>

                <div class="form-group">
                    <label>
                        方法
                    </label>
                    ${apiOrg.method}

                </div>

                <div class="form-group">

                    <label for="accessToken">
                        url
                    </label>
                    <input type="text" class="form-control" id="accessToken" value="${apiOrg.url}"
                           readonly/>
                </div>

                <div class="form-group">

                    <label for="api">
                        api
                    </label>
                    <input type="text" class="form-control" id="api" value="${apiOrg.name}"
                           readonly/>
                </div>
                <g:if test="${apiOrg.extraParams}">
                    <g:set var="paramsValue" value=""/>
                    <g:if test="${apiOrg.extraParams == 'secret'}">
                        <g:set var="paramsValue" value="${apiAccount.secret}"/>

                    </g:if>
                    <div class="form-group">

                        <label for="${apiOrg.extraParams}">
                            ${apiOrg.extraParams}
                        </label>
                        <input type="text" class="form-control" id="${apiOrg.extraParams}" value="${paramsValue}"
                               name="${apiOrg.extraParams}"/>
                    </div>
                </g:if>
                <g:if test="${apiOrg.method == 'POST'}">
                    <div class="form-group">

                        <label for="body">
                            body
                        </label>
                        <textarea class="form-control" id="body" name="body" required></textarea>
                    </div>
                </g:if>


                <button type="button" id="wxFormSubmitId" class="btn btn-default" disabled="disabled">
                    Submit
                </button>
            </form>
        </div>

    </div>


</div>
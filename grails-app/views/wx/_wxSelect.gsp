<g:if test="${!notRequired}">
    <div class="fieldcontain required">
        <label for="${field}">${label}
            <span class="required-indicator">*</span>
        </label><g:select id="${field}" name="${field}" from="${from}" required="required" value="${val}" optionKey="${optionKey}" optionValue="${optionValue}"/>
    </div>
</g:if>

<g:if test="${notRequired}">
    <div class="fieldcontain">
        <label for="${field}">${label}
            <span class="required-indicator"></span>
        </label>
        <g:select id="${field}" name="${field}" from="${from}" value="${val}" optionKey="${optionKey}" optionValue="${optionValue}"/>
    </div>
</g:if>

package taglib

import com.utils.MyStringUtils

class CustomizeTagLib {
    static defaultEncodeAs = [taglib: 'raw']
    def report = { attrs, body ->
        def report = attrs.get('report');
        out << render(template: "/report/reportTemplate", model: [report: report])
    }
//    def myList = { attrs, body ->
//        out << render(template: "/template/list")
//    }

    def wxInput = { attrs, body ->
        def field = attrs.get('field');
        def val = attrs.get('val');
        def inputType = attrs.get('inputType');
        def label = attrs.get('label');
        def notRequired = attrs.get('notRequired');
        if (!inputType) {
            inputType = "text"
        }
        if (!label) {
            label = MyStringUtils.getLabelByField(field as String);
        }
        if (!notRequired) {
            notRequired = false;
        }

        out << render(template: "/wx/wxInput", model: [field: field, inputType: inputType, label: label, notRequired: notRequired, val: val])
    }
    def totalCount = { attrs ->
        def total = attrs.get('total');
        out << "共 ${total} 条"
    }

    def writeWithoutEncoding = { attrs ->
        out << attrs.input
    }

    def wxForm = { attrs ->
        def apiOrg = attrs.get('apiOrg');
        out << render(template: "/wx/wxForm", model: [apiOrg: apiOrg])
    }
    def wxSelect = { attrs, body ->
        def field = attrs.get('field');
        def from = attrs.get('from');
        def optionKey = attrs.get('optionKey');
        def optionValue = attrs.get('optionValue');
        def val = attrs.get('val');
        def label = attrs.get('label');
        def notRequired = attrs.get('notRequired');

        if (!label) {
            label = MyStringUtils.getLabelByField(field as String);
        }
        if (!notRequired) {
            notRequired = false;
        }

        out << render(template: "/wx/wxSelect", model: [field: field, from: from, label: label, notRequired: notRequired, val: val, optionValue: optionValue, optionKey: optionKey])
    }

}

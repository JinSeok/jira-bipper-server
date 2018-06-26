package ru.andreymarkelov.atlas.plugins.jirabipperserver.workflow.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.util.I18nHelper;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

public class SendSmsFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
    private final I18nHelper i18nHelper;

    public SendSmsFunctionFactory(I18nHelper i18nHelper) {
        this.i18nHelper = i18nHelper;
    }

    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        velocityParams.put("messageText", "The issue {issue.key} requires your immediate attention! Please, go to {issue.link}");
        velocityParams.put("recipientType", "1");
    }

    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }

        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;
        String recipientType = Objects.toString(functionDescriptor.getArgs().get("recipientType"), "No destination defined.");
        String userFieldValue = (String) functionDescriptor.getArgs().get("userFieldValue");
        String groupFieldValue = (String) functionDescriptor.getArgs().get("groupFieldValue");
        String userValue = (String) functionDescriptor.getArgs().get("userValue");
        String phoneValue = (String) functionDescriptor.getArgs().get("phoneValue");

        String recipientTypeStr = null;
        String recipientTypeValue = null;
        switch (recipientType) {
            case "1": {
                recipientTypeStr = i18nHelper.getText("ru.andreymarkelov.atlas.plugins.jira-bipper-server.postfunction.sendto.type.userfield");
                recipientTypeValue = userFieldValue;
                break;
            } case "2": {
                recipientTypeStr = i18nHelper.getText("ru.andreymarkelov.atlas.plugins.jira-bipper-server.postfunction.sendto.type.groupfield");
                recipientTypeValue = groupFieldValue;
                break;
            } case "3": {
                recipientTypeStr = i18nHelper.getText("ru.andreymarkelov.atlas.plugins.jira-bipper-server.postfunction.sendto.type.user");
                recipientTypeValue = userValue;
                break;
            } case "4": {
                recipientTypeStr = i18nHelper.getText("ru.andreymarkelov.atlas.plugins.jira-bipper-server.postfunction.sendto.type.phone");
                recipientTypeValue = phoneValue;
                break;
            }
        }

        velocityParams.put("messageText", Objects.toString(functionDescriptor.getArgs().get("messageText"), "No Message"));
        velocityParams.put("recipientType", recipientType);
        velocityParams.put("userFieldValue", userFieldValue);
        velocityParams.put("groupFieldValue", groupFieldValue);
        velocityParams.put("userValue", userValue);
        velocityParams.put("phoneValue", phoneValue);
        velocityParams.put("recipientTypeStr", recipientTypeStr);
        velocityParams.put("recipientTypeValue", recipientTypeValue);
    }

    public Map<String, Object> getDescriptorParams(Map<String, Object> formParams) {
        String messageText = extractSingleParam(formParams, "messageText");
        String recipientType = extractSingleParam(formParams, "recipientType");
        String userFieldValue = extractSingleParam(formParams, "userFieldValue");
        String groupFieldValue = extractSingleParam(formParams, "groupFieldValue");
        String userValue = extractSingleParam(formParams, "userValue");
        String phoneValue = extractSingleParam(formParams, "phoneValue");

        switch (recipientType) {
            case "1": {
                groupFieldValue = null;
                userValue = null;
                phoneValue = null;
                break;
            } case "2": {
                userFieldValue = null;
                userValue = null;
                phoneValue = null;
                break;
            } case "3": {
                userFieldValue = null;
                groupFieldValue = null;
                phoneValue = null;
                break;
            } case "4": {
                userFieldValue = null;
                groupFieldValue = null;
                userValue = null;
                break;
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("messageText", messageText);
        params.put("recipientType", recipientType);
        params.put("userFieldValue", userFieldValue);
        params.put("groupFieldValue", groupFieldValue);
        params.put("userValue", userValue);
        params.put("phoneValue", phoneValue);
        return params;
    }
}

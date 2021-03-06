package ru.andreymarkelov.atlas.plugins.jirabipperserver.workflow.function;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.MessageFormatter;
import ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.NumberExtractor;
import ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.SenderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.NumberExtractor.GROUP_FIELD;
import static ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.NumberExtractor.PHONE;
import static ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.NumberExtractor.TEXT_FIELD;
import static ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.NumberExtractor.USER;
import static ru.andreymarkelov.atlas.plugins.jirabipperserver.manager.NumberExtractor.USER_FIELD;

public class SendSmsFunction extends AbstractJiraFunctionProvider {
    private static final Logger log = LoggerFactory.getLogger(SendSmsFunction.class);

    private final MessageFormatter messageFormatter;
    private final NumberExtractor numberExtractor;
    private final SenderService senderService;

    public SendSmsFunction(
            MessageFormatter messageFormatter,
            NumberExtractor numberExtractor,
            SenderService senderService) {
        this.messageFormatter = messageFormatter;
        this.numberExtractor = numberExtractor;
        this.senderService = senderService;
    }

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        Issue issue = getIssue(transientVars);
        ApplicationUser callerUser = getCallerUserFromArgs(transientVars, args);

        String message = (String) args.get("messageText");

        List<String> phones = new ArrayList<>();
        switch ((String) args.get("recipientType")) {
            case USER_FIELD:
                phones.addAll(numberExtractor.getUserFieldPhones(issue, (String) args.get("userFieldValue")));
                break;
            case GROUP_FIELD:
                phones.addAll(numberExtractor.getGroupFieldPhones(issue, (String) args.get("groupFieldValue")));
                break;
            case USER:
                String userPhone = numberExtractor.getUserPhone((String) args.get("userValue"));
                if (isNotBlank(userPhone)) {
                    phones.add(userPhone);
                }
                break;
            case PHONE:
                String phone = (String) args.get("phoneValue");
                if (isNotBlank(phone)) {
                    phones.add(phone);
                }
                break;
            case TEXT_FIELD:
                phones.addAll(numberExtractor.getTextFieldPhones(issue, (String) args.get("textFieldValue")));
                break;
        }

        if (phones.isEmpty()) {
            return;
        }

        try {
            senderService.sendMessage(messageFormatter.formatMessage(message, issue), phones);
        } catch (Exception ex) {
            log.error("Cannot send Infobip message", ex);
        }
    }
}

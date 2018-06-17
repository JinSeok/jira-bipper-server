package ru.andreymarkelov.atlas.plugins.jirabipperserver.rest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "accountSetupKey")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountSetupKeyReqRespModel {
    @XmlElement(name = "sender")
    private String sender;

    @XmlElement(name = "apiKey")
    private String apiKey;

    protected AccountSetupKeyReqRespModel() {
    }

    public AccountSetupKeyReqRespModel(String sender, String apiKey) {
        this.sender = sender;
        this.apiKey = apiKey;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return "AccountSetupKeyReqRespModel{" +
                "sender='" + sender + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
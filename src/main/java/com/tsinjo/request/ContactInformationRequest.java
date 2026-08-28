package com.tsinjo.request;

import com.tsinjo.model.ContactInformation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class ContactInformationRequest {
    @Email @Size(max = 190)
    private String email;
    @Size(max = 40)
    private String mobile;
    @Size(max = 100)
    private String twitter;
    @Size(max = 100)
    private String instagram;

    public ContactInformation toContactInformation() {
        ContactInformation contact = new ContactInformation();
        contact.setEmail(email);
        contact.setMobile(mobile);
        contact.setTwitter(twitter);
        contact.setInstagram(instagram);
        return contact;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getTwitter() { return twitter; }
    public void setTwitter(String twitter) { this.twitter = twitter; }
    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
}

package com.tsinjo.model;

import lombok.Data;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class ContactInformation {
    private String email;

    private String mobile;

    private String twitter;

    private String instagram;
}



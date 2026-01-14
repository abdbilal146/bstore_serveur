package com.mancer.bstore.models;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {
    private String fullName;
    private String street;
    private String city;
    private String postalCode;
    private String country;
}

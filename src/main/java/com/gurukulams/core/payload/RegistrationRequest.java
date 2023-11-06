package com.gurukulams.core.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * The type Registration request.
 */
public class RegistrationRequest {


    /**
     * firstName.
     */
    @NotBlank
    @Pattern(regexp = "^([a-zA-Z]+\\s)*[a-zA-Z]+$")
    private String name;

    /**
     * Date of Birth.
     */
    @Past
    private LocalDate dob;


    /**
     * getPassword.
     *
     * @return password
     */
    public String getName() {
        return name;
    }

    /**
     * setPassword.
     *
     * @param thepassword
     */
    public void setName(final String thepassword) {
        this.name = thepassword;
    }

    /**
     * Gets Dob.
     * @return dob
     */
    public LocalDate getDob() {
        return dob;
    }

    /**
     * sets Dob.
     * @param aDob
     */
    public void setDob(final LocalDate aDob) {
        this.dob = aDob;
    }
}

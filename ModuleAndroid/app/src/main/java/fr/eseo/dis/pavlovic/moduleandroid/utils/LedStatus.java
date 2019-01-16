package fr.eseo.dis.pavlovic.moduleandroid.utils;

public class LedStatus {
    private String identifier;
    private boolean status;

    public String getIdentifier() {
        return identifier;
    }

    public LedStatus setIdentifier(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public boolean getStatus() {
        return status;
    }

    public LedStatus setStatus(final boolean status) {
        this.status = status;
        return this;
    }

    public LedStatus reverseStatus() {
        return setStatus(!status);
    }

    @Override
    public LedStatus clone() {
        return new LedStatus().setIdentifier(getIdentifier()).setStatus(getStatus());
    }
}
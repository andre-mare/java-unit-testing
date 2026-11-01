package com.example.auth;

/** Receives audit events. In production this could write to a SIEM. */
public interface AuditLog {

    void append(AuditEvent event);
}

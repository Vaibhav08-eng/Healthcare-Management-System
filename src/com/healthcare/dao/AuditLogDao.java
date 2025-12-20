package com.healthcare.dao;

import com.healthcare.model.AuditLogEntry;

/**
 * DAO for audit log entries.
 */
public interface AuditLogDao {

    void insert(AuditLogEntry entry);
}



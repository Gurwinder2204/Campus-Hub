package com.campusstudyhub.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TenantContext provides a way to store and retrieve the current tenant's ID
 * in a thread-safe manner using ThreadLocal.
 */
public class TenantContext {
    private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        logger.debug("Setting tenant focus to: {}", tenantId);
        currentTenant.set(tenantId);
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}

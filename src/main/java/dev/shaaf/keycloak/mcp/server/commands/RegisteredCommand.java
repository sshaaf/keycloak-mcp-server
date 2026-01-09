package dev.shaaf.keycloak.mcp.server.commands;

import jakarta.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a registered Keycloak command.
 * All beans with this qualifier are auto-discovered by CommandRegistry.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Documented
public @interface RegisteredCommand {
}


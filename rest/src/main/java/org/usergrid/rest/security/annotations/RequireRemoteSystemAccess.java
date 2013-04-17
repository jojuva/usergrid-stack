package org.usergrid.rest.security.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This is the 'remote super user' annotation used to describe cross-organization
 * access to a limited amount of managment endpoints.
 *
 * @author zznate
 */
@Retention(value = RUNTIME)
@Target(value = { METHOD })
public @interface RequireRemoteSystemAccess {

}

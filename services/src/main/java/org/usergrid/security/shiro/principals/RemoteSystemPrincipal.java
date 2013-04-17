package org.usergrid.security.shiro.principals;

import org.usergrid.management.UserInfo;

import static org.usergrid.persistence.cassandra.CassandraService.MANAGEMENT_APPLICATION_ID;

/**
 * Indentical to AdminUserPrincipal, but used for stronger typing by instance checks
 *
 * @author zznate
 */
public class RemoteSystemPrincipal extends UserPrincipal {

  public RemoteSystemPrincipal(UserInfo user) {
 		super(MANAGEMENT_APPLICATION_ID, user);
 	}
}

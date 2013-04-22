package org.usergrid.security.shiro.credentials;

/**
 * @author zznate
 */
public class RemoteSystemAccessToken extends AbstractAccessTokenCredentials
        implements RemoteSystemCredentials {

  public RemoteSystemAccessToken(String token) {
    super(token);
  }

}

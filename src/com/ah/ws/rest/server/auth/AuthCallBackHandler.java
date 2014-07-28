package com.ah.ws.rest.server.auth;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

public class AuthCallBackHandler implements CallbackHandler {
	private String userName;

	private String secretKey;

	public AuthCallBackHandler(String userName, String secretKey) {
		this.userName = userName;
		this.secretKey = secretKey;
	}

	@Override
	 public void handle(Callback[] callbacks) {
        for (int i = 0; i< callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback)callbacks[i];
                nc.setName(userName);
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback)callbacks[i];
                pc.setPassword(secretKey.toCharArray());
            }
        }
    }
}

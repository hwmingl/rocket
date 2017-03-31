package com.fighter.rocket.socketio.authorize.impl;

import rocket.external.socketio.authorize.Authorize;

/**
 * Created by hanwm on 17/3/29.
 */
public class DefaultAuthorizeImpl implements Authorize {

    private String  authorizeKey;


    public void setAuthorizeKey(String authorizeKey) {
        this.authorizeKey = authorizeKey;
    }
}

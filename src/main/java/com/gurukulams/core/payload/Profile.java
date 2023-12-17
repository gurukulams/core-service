package com.gurukulams.core.payload;

/**
 * Holds Profile of a User Handle.
 * @param userHandle
 * @param displayName
 * @param profilePicture
 */
public record Profile(String userHandle, String displayName,
                      String profilePicture) {
}

package com.gurukulams.core.payload;


import java.time.LocalDateTime;

public record Learner(String userHandle, String email,

                      String password,
                      String imageUrl,
                      AuthProvider provider,
                      LocalDateTime createdAt,
                      LocalDateTime modifiedAt) {
}

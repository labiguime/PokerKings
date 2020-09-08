package com.games.pokerkings.ui.home;

import androidx.annotation.Nullable;

import com.games.pokerkings.data.models.User;

public class JoinGameResult {
    @Nullable
    private Integer error;
    @Nullable
    private User userData;

    private Boolean isDataValid;

    public JoinGameResult(@Nullable Integer error) {
        this.error = error;
        this.userData = null;
        this.isDataValid = false;
    }

    public JoinGameResult(boolean isDataValid, User userData) {
        this.error = null;
        this.userData = userData;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getError() {
        return error;
    }

    @Nullable
    public User getData() { return userData; }

    public boolean isDataValid() {
        return isDataValid;
    }
}

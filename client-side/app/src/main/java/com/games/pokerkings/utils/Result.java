package com.games.pokerkings.utils;

import androidx.annotation.Nullable;

import com.games.pokerkings.R;

public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    // Success sub-class
    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Progress sub-class
    public final static class Progress extends Result {
        private boolean isResultInProgress;

        public Progress(boolean isResultInProgress) {
            this.isResultInProgress = isResultInProgress;
        }

        public boolean getResultInProgress() {
            return this.isResultInProgress;
        }
    }

    // Error sub-class
    public final static class Error extends Result {

        private String error;

        public Error(String error) {
            if(error == null) {
                this.error = Constants.ERROR_UNKNOWN;
            } else {
                this.error = error;
            }
        }

        public String getError() {
            return this.error;
        }
    }
}
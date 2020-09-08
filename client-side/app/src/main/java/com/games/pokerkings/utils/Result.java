package com.games.pokerkings.utils;

import androidx.annotation.Nullable;

import com.games.pokerkings.R;

public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return success.getData().toString();
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return error.getError().toString();
        }
        return "";
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

    // Error sub-class
    public final static class Error extends Result {

        private Integer error;

        public Error(Integer error) {
            if(error == null) {
                this.error = R.string.error_unknown;
            } else {
                this.error = error;
            }

        }

        public Integer getError() {
            return this.error;
        }
    }
}
package models.utils;

import play.Logger;

public class ExceptionFactory {

    public AppException getNewAppException(Exception exception) {
        Logger.error(exception.getMessage());
        AppException app = new AppException();
        app.initCause(exception);
        return app;
    }
}

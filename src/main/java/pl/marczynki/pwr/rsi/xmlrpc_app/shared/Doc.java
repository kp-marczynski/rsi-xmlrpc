package pl.marczynki.pwr.rsi.xmlrpc_app.shared;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Doc {
    String description();

    String[] params() default {};

    String[] paramsType() default {};
}

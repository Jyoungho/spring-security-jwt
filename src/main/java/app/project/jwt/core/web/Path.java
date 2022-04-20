package app.project.jwt.core.web;

public class Path {

    /** API Prefix */
    public static final String API                                     = "/api";

    public static final String VERSION                                 = "/v1";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Auth
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String LOGIN                                   = API + VERSION + "/login";

    public static final String NEW_TOKEN                               = API + VERSION + "/new/token";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | User
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String SIGNUP                                  = API + VERSION + "/signup";

    public static final String ME                                      = API + VERSION + "/me";
}


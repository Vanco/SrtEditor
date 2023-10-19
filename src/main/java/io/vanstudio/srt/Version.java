package io.vanstudio.srt;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 27/10/2016.
 */
public final class Version {
    private static final int major = 1;
    private static final int minor = 1;
    private static final int reversion = 1;
    public static String version() {
        return "" + major + "." + minor + (reversion > 0 ? "." + reversion: "");
    }
}

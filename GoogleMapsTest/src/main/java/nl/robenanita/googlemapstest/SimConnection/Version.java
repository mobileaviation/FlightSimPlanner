package nl.robenanita.googlemapstest.SimConnection;

/**
 * Created by Rob Verhoef on 31-3-2018.
 */

public class Version {
    public Version(String version)
    {
        String[] v = version.split("\\.");
        major = Integer.parseInt(v[0]);
        minor = Integer.parseInt(v[1]);
        revision = Integer.parseInt(v[2]);
        build = Integer.parseInt(v[3]);
    }

    public int major;
    public int minor;
    public int revision;
    public int build;

    public boolean check(int major, int minor, int revision, int build)
    {
        return ((this.major==major) && (this.minor==minor) && (this.revision==revision) && (this.build==build));
    }
}

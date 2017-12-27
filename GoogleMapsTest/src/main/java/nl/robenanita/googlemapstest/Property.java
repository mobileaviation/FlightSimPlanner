package nl.robenanita.googlemapstest;

/**
 * Created by Rob Verhoef on 23-4-14.
 */
public class Property {
    public Integer _id;
    public String name;
    public String value1;
    public String value2;

    public static Property NewProperty(String name, String value1, String value2)
    {
        Property p = new Property();
        p.name = name;
        p.value1 = value1;
        p.value2 = value2;
        return p;
    }
}

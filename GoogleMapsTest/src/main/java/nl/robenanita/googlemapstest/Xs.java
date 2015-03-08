package nl.robenanita.googlemapstest;

/**
 * Created by Rob Verhoef on 9-5-2014.
 */
public class Xs {
    public static String StrIsNull(String Value, String nullValue)
    {
        return (String)isnull(Value, nullValue);
    }
    public static Float FloatIsNull(Float Value, Float nullValue)
    {
        return (Float)isnull(Value, nullValue);
    }
    public static Integer IntegerIsNull(Integer Value, Integer nullValue)
    {
        return (Integer)isnull(Value, nullValue);
    }
    private static Object isnull(Object Value, Object nullValue)
    {
        return (Value == null) ? nullValue : Value;
    }
}

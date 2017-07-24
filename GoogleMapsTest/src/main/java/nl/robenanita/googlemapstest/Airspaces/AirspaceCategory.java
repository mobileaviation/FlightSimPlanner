package nl.robenanita.googlemapstest.Airspaces;

import android.graphics.Color;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public enum AirspaceCategory {
    // outlineColor, outlineWidth, strokeColor, strokeWidth, fillColor, visible
    A (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT, true),
    AWY (Color.GREEN, 2, Color.YELLOW,4, Color.TRANSPARENT, false),
    B (Color.BLACK, 0, 0xBF000000,2, Color.TRANSPARENT, true),
    C (0xFFE4A19E, 1, 0x60E4A19E,2, Color.TRANSPARENT, true),
    CTR (Color.BLACK, 0, 0xFFE4A19E ,4, 0x30E4A19E, true),
    D (Color.BLACK, 0, 0xFFE4A19E ,4, 0x30E4A19, true),
    DANGER (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, true),
    Q (Color.BLACK, 0, 0xFFC06E8E,4, 0x50C06E8E, true),
    E (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, true),
    F (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, true),
    G (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, true),
    GSEC (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false),
    GP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false),
    GLIDING (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false),
    OTH (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false),
    RESTRICTED (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, true),
    R (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, true),
    TMA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, true),
    TMZ (Color.BLACK, 0, 0xFFC3819E,5, Color.TRANSPARENT, true),
    TSA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, true),
    WAVE (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, true),
    W (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false),
    PROHIBITED (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, true),
    P (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, true),
    FIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, true),
    UIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, true),
    RMZ (Color.BLACK, 0, 0xFFC06E8E,4, Color.TRANSPARENT, true),
    Z (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false),
    ZP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false),
    UKN (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, false);

    private Integer outlineColor;
    private Integer outlineWidth;
    private Integer strokeColor;
    private Integer strokeWidth;
    private Integer fillColor;
    private Boolean visible;
    AirspaceCategory(Integer outlineColor,
                     Integer outlineWidth,
                     Integer strokeColor,
                     Integer strokeWidth,
                     Integer fillColor,
                     Boolean visible)
    {
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
        this.visible = visible;
    }

    public Integer getOutlineColor(){return outlineColor;}
    public Integer getOutlineWidth(){return outlineWidth;}
    public Integer getFillColor() {return fillColor;}
    public Integer getStrokeColor(){return strokeColor;}
    public Integer getStrokeWidth(){return strokeWidth;}
    public Boolean getVisible(){return visible;}

    @Override
    public String toString() {
        return super.toString();
    }
}

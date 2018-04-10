package nl.robenanita.googlemapstest.MBTiles;

import java.util.Date;

public class MBTile {
    public Integer _id;
    public String name;
    public String region;
    public MBTileType type;
    public void setType(String type_str)
    {
        if (type_str.equals("ofm")) type = MBTileType.ofm;
        if (type_str.equals("fsp")) type = MBTileType.fsp;
    }
    public String mbtileslink;
    public String xmllink;
    public Integer version;
    public Date startValidity;
    public void setStartValidity(Integer timestamp) { startValidity = getDate(timestamp);}
    public Date endValidity;
    public void setEndValidity(Integer timestamp) { endValidity = getDate(timestamp);}

    private Date getDate(Integer timestamp) { return new Date((long)timestamp*1000); }
}

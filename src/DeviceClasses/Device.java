package DeviceClasses;

import Staff.Varuint;

public class Device {
    Varuint address;
    byte devType;
    String devName;

    public Device(Varuint address, byte devType, String devName) {
        this.address = address;
        this. devType = devType;
        this.devName = devName;
    }
}

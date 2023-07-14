package DeviceClasses;


import Staff.Varuint;

public class SocketLamp extends Device {
    byte state;

    public SocketLamp(Varuint address, byte devType, String devName, byte state) {
        super(address, devType, devName);
        this.state = state;
    }
}

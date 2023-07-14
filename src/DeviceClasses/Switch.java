package DeviceClasses;


import Staff.CmdBody;
import Staff.Varuint;

public class Switch  extends Device  {
    SocketLamp[] devicesUnderControl;
    byte state;

    public Switch(Varuint address, byte devType, String devName, CmdBody cmdBody) {
        super(address, devType, devName);
        if(cmdBody.getDevType() == 0x03) {
            // devicesUnderControl =  cmdBody.getDevice().getDev_props().getBytes();
        }
    }
}

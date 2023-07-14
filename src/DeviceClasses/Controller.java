package DeviceClasses;

import Staff.HttpPostTest;
import Staff.Payload;
import Staff.Varuint;

import java.util.ArrayList;

public class Controller {
    static ArrayList<Device> foundDevices = new ArrayList<>();

    static void processPayloads(ArrayList<Payload> payloads) {
        for (Payload payload : payloads) {
            if(payload.getCmd() == 0x02) {
                foundDevices.add(new Device(payload.getSrc(), payload.getDev_type(), payload.getCmd_body().getDevice().getDev_name()));
            }
        }
    }

    static void collectInfo() {
        for (Device dev : foundDevices) {
            HttpPostTest.getStatus(dev.address);
        }
    }
}

import java.sql.Timestamp;
import java.util.Arrays;

public class CmdBody {
    byte cmd;
    byte devType;
    Timestamp timestamp;
    Device device;
    Varuint[] values; //для сенсора
    byte enabled; // для выключателя, розетки и лампы


    public CmdBody(byte cmd, byte devType, Device device) {
        this.cmd = cmd;
        this.devType = devType;
        this.device = device;
    }
    public CmdBody(byte cmd, byte devType, byte[] cmd_body) {
        this.cmd = cmd;
        this.devType = devType;

        switch (cmd){
            case 0x01:
            case 0x02:
                String dev_name = Decoder.byteArrayToString(cmd_body);
                device = new Device(dev_name, new DevProps(devType, Arrays.copyOfRange(cmd_body, cmd_body[0], cmd_body.length)));
                break;
            case 0x04:
                if(devType == 0x02){
                    values = Varuint.decodeArray(cmd_body);
                }
                if(devType > 0x02 && devType < 0x06) {
                    enabled = cmd_body[0];
                }
                break;
            case 0x06:
                if(devType == 0x06) {
                    timestamp = new Timestamp(Varuint.decode(cmd_body));
                }
                //кинуть ошибку
                break;
            default:
                //игнорировать
                break;
        }
    }

    public int getSize() {
        return device.getSize();
    }

    public byte[] getBytes() {
        return device.getBytes();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(timestamp != null) {
            sb.append(timestamp);
        }
        if(device != null) {
            sb.append(device.getDev_name());
        }
        return sb.toString();
    }
}

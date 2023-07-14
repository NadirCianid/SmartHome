package Staff;

public class DevProps {
    byte devType;
    byte[] devProps;
    public DevProps(byte devType, byte[] devProps) {
        this.devType = devType;
        this.devProps = devProps;
    }

    public byte[] getBytes() {
        return devProps;
    }
}

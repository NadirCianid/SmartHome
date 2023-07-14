package Staff;

public class Device{
    private String dev_name;
    private DevProps dev_props;
    private byte[] bytes;

    Device(String dev_name, DevProps dev_props) {
        this.dev_name = dev_name;
        this.dev_props = dev_props;

        int length = dev_name.getBytes().length + 1 + dev_props.getBytes().length;
        bytes = new byte[length];
        bytes[0] = (byte) dev_name.getBytes().length;
        System.arraycopy(dev_name.getBytes(), 0, bytes, 1, dev_name.getBytes().length);
    }

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public DevProps getDev_props() {
        return dev_props;
    }

    public void setDev_props(DevProps dev_props) {
        this.dev_props = dev_props;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getSize() {
        return bytes.length;
    }
}

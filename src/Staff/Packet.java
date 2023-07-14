package Staff;

public class Packet {
    private byte length;
    private Payload payload;
    private byte crc8;

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public Payload getPayload() {
        return payload;
    }

    public byte[] getPayloadInBytes() {
        return payload.toBytes();
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public byte getCrc8() {
        return crc8;
    }

    public void setCrc8(byte crc8) {
        this.crc8 = crc8;
    }

    public byte[] getPacketInBytes() {
        byte[] bytes = new byte[getPayloadInBytes().length + 2];

        bytes[0] = length;
        System.arraycopy(getPayloadInBytes(), 0, bytes, 1, length);
        bytes[length + 1] = crc8;

        return bytes;
    }
}

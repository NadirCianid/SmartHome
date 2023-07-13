import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Payload {
    private Varuint src;
    private Varuint dst;
    private Varuint serial;
    private byte dev_type;
    private byte cmd;
    private CmdBody cmd_body;

    public Payload() {
    }
    public Payload(byte[] payloadBytes) { //из бинарной формы в объект
        src = new Varuint(Varuint.readULEB128(payloadBytes));
        dst = new Varuint(Varuint.readULEB128(Arrays.copyOfRange(payloadBytes, src.getSize(), payloadBytes.length)));
        serial = new Varuint(Varuint.readULEB128(Arrays.copyOfRange(payloadBytes, src.getSize() + dst.getSize(), payloadBytes.length)));
        dev_type = payloadBytes[src.getSize() + dst.getSize() + serial.getSize()];
        cmd = payloadBytes[src.getSize() + dst.getSize() + serial.getSize() + 1];
        cmd_body =  new CmdBody(cmd, dev_type, Arrays.copyOfRange(payloadBytes, src.getSize() + dst.getSize() + serial.getSize() + 2, payloadBytes.length));


    }

    public Varuint getSrc() {
        return src;
    }

    public void setSrc(Varuint src) {
        this.src = src;
    }

    public Varuint getDst() {
        return dst;
    }

    public void setDst(Varuint dst) {
        this.dst = dst;
    }

    public Varuint getSerial() {
        return serial;
    }

    public void setSerial(Varuint serial) {
        this.serial = serial;
    }

    public byte getDev_type() {
        return dev_type;
    }

    public void setDev_type(byte dev_type) {
        this.dev_type = dev_type;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public CmdBody getCmd_body() {
        return cmd_body;
    }

    public void setCmd_body(CmdBody cmd_body) {
        this.cmd_body = cmd_body;
    }

    public byte[] toBytes() {

        int srcSize = src.getSize();
        int dstSize = dst.getSize();
        int serialSize = serial.getSize();
        int cmdBodySize = cmd_body.getSize();

        int payloadLength = srcSize + dstSize + serialSize + 2 + cmdBodySize;
        byte[] bytes = new byte[payloadLength];

        System.arraycopy(src.getBytes(), 0, bytes, 0, srcSize);
        System.arraycopy(dst.getBytes(), 0, bytes, srcSize, dstSize);
        System.arraycopy(serial.getBytes(), 0, bytes,  srcSize + dstSize, serialSize);
        bytes[srcSize + dstSize + serialSize] = dev_type;
        bytes[srcSize + dstSize + serialSize + 1] = cmd;
        System.arraycopy(cmd_body.getBytes(), 0, bytes,  srcSize + dstSize + serialSize + 2, cmdBodySize);

        return bytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(src.getValue());
        sb.append(" ");
        sb.append(dst.getValue());
        sb.append(" ");
        sb.append(serial.getValue());
        sb.append(" ");
        sb.append(dev_type);
        sb.append(" ");
        sb.append(cmd);
        sb.append(" ");
        sb.append(cmd_body);

        return sb.toString();
    }
}

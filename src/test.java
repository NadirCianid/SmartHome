import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        URLBase64.binaryPrint(Varuint.encode(1689242598900L));
        System.out.println(Varuint.decode(Varuint.encode(1689242598900L)));
    }
//11110100 11111011 11010001 11110101 10010100 110001

}

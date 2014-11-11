public class ConvertUtil {
    public static int converttoLSB(int a, int b) {
        return b*256 + a;
    }

    public static int converttoLSB(int a, int b, int c, int d, int e, int f) {
        return f*256*256*256*256*256 +e*256*256*256*256 + converttoLSB( a,  b,  c,  d);
    }


    public static int converttoLSB(int a, int b, int c, int d) {
        return d*256*256*256 +c*256*256 + converttoLSB( a,  b);
    }

    public static int byteToUnsigned(byte b) {
        return b & 0xFF;
    }

}

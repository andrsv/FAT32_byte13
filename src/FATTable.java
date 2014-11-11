public class FATTable {
    int[] fatTableInput;

    public FATTable(byte[] fatTableInput, int size) {
        this.fatTableInput = new int[size];
        for (int i=0; i<size; i++) {
            this.fatTableInput[i] = ConvertUtil.byteToUnsigned(fatTableInput[i]);
        }
    }

    public String getMediaDescriptor() {
        switch (fatTableInput[0x000]) {
            case 0xF8: return "Fixed disk (i.e., typically a partition on a hard disk).";
            default: return "Unknown Media Descriptor";
        }
    }

    public int getNextCluster(int currentCluster) {
        return ConvertUtil.converttoLSB(fatTableInput[currentCluster*4],fatTableInput[currentCluster*4+1],fatTableInput[currentCluster*4+2],fatTableInput[currentCluster*4+3]);
    }
}

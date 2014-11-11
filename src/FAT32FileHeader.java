public class FAT32FileHeader {
    int[] record;

    public FAT32FileHeader(int[] record) {
        this.record = record;
    }

    public String getFilename() {
        String filename = "";
        for (int i = 0;i<8;i++) {
            filename += (char)record[i];
        }
        return filename;
    }

    public String getFileExtension() {
        String fileextension = "";
        for (int i = 8;i<8+3;i++) {
            fileextension += (char)record[i];
        }
        return fileextension;
    }

    public int getAttributes() {
        return record[0x0b];
    }

    public int getByte13() {
        return record[0x0d];
    }



}

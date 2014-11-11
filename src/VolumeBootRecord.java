
public class VolumeBootRecord {
    int[] vbr;

    public VolumeBootRecord(byte[] volumeBootRecord) {
        this.vbr = new int[512];
        for (int i=0; i<512; i++) {
            this.vbr[i] = ConvertUtil.byteToUnsigned(volumeBootRecord[i]);
        }
    }

    public int getJumpInstruction() {
        if (vbr[0] != 0xeb || vbr[2] != 0x90) {
            throw new RuntimeException("Unknown boot sector");
        }
        return vbr[1];
    }

    public String getOEMName() {
        String OEMName = "";
        for (int i = 3;i<3+8;i++) {
            OEMName += (char)vbr[i];
        }
        return OEMName;
    }

    public int getBytesPerSector() {
        return ConvertUtil.converttoLSB(vbr[0x00b], vbr[0x00c]);
    }

    public int getSectorsPerCluster() {
        return vbr[0x00d];
    }

    public int getCountRerservedLogicalSectors() {
        return ConvertUtil.converttoLSB(vbr[0x00e], vbr[0x00f]);
    }

    public int getCountFileAllocationTables() {
        return vbr[0x010];
    }

    public int getTotalLogicalSectors() {
        return ConvertUtil.converttoLSB(vbr[0x020], vbr[0x021], vbr[0x022], vbr[0x023], vbr[0x013], vbr[0x014]);
    }

    public String getMediaDescriptor() {
        switch (vbr[0x015]) {
            case 0xF8: return "Fixed disk (i.e., typically a partition on a hard disk).";
            default: return "Unknown Media Descriptor";
        }
    }


    public String getVolumeLabel() {
      /*  String volumeLabel = "";
        for (int i = 0x2b;i<0x2b + 11;i++) {
            volumeLabel += (char)vbr[i];
        }
        return volumeLabel;
        */
        return "N/A";
    }

    public int getSectorsPerFAT() {
        return ConvertUtil.converttoLSB(vbr[0x024], vbr[0x025], vbr[0x026], vbr[0x027]);
    }

    public int getRootDirCluster() {
        return ConvertUtil.converttoLSB(vbr[0x02c], vbr[0x02d], vbr[0x02e], vbr[0x02f]);
    }
}

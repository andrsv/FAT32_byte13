import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        FileInputStream fileInput = null;
        try { //Open the input and out files for the streams
            fileInput = new FileInputStream("test.dd");
            int data;
            // Read Volume Boot Record (1st sector)
            byte[] vbr = new byte[512];
            fileInput.read(vbr,0,512);
            VolumeBootRecord volumeBootRecord = new VolumeBootRecord(vbr);
            System.out.println("Volume Boot Record:  " + bytesToHex(vbr));
            System.out.println(".................");
            System.out.println("0x000 Boot record jump to: " + volumeBootRecord.getJumpInstruction());
            System.out.println("0x003 OEM Name:            " + volumeBootRecord.getOEMName());
            System.out.println("..............................");
            System.out.println(".....BIOS Parameter Block.....");
            System.out.println("..............................");
            System.out.println("0x00B Bytes per sector:    " + volumeBootRecord.getBytesPerSector());
            System.out.println("0x00D Sectors per cluster: " + volumeBootRecord.getSectorsPerCluster());
            System.out.println("0x00E #Reserved logical s.:" + volumeBootRecord.getCountRerservedLogicalSectors());
            System.out.println("0x010 #FAT Tables:         " + volumeBootRecord.getCountFileAllocationTables());
            System.out.println("0x011                      N/A");
            System.out.println("0x013,0x020 Tot log. sect.:" + volumeBootRecord.getTotalLogicalSectors());
            System.out.println("0x015 Media Descriptor:    " + volumeBootRecord.getMediaDescriptor());
            System.out.println("0x016                      N/A");
            System.out.println("0x018 - 0x01F              Not evaluated");
            System.out.println("0x024 Sectors per FAT:     " + volumeBootRecord.getSectorsPerFAT());
            System.out.println("0x028 - 0x02A              Not Evaluated");
            System.out.println("0x02C Root dir start cl.:  " + volumeBootRecord.getRootDirCluster());
            System.out.println("0x036 - 0x200              Not Evaluated");

            //Read the reserved sectors (except vbr)
            int countReservedBytes =volumeBootRecord.getBytesPerSector()*(volumeBootRecord.getCountRerservedLogicalSectors()-1);
            byte[] reservedSectors = new byte[countReservedBytes];
            fileInput.read(reservedSectors,0,countReservedBytes);


            //Read the FAT table
            System.out.println("...................");
            System.out.println(".....FAT Table.....");
            System.out.println("...................");
            byte[] FATTableInput = new byte[(volumeBootRecord.getSectorsPerFAT())*volumeBootRecord.getBytesPerSector()];
            fileInput.read(FATTableInput,0,(volumeBootRecord.getSectorsPerFAT())*volumeBootRecord.getBytesPerSector());
            FATTable FatTable = new FATTable(FATTableInput, volumeBootRecord.getSectorsPerFAT()*volumeBootRecord.getBytesPerSector());
            //System.out.println("Fat Table:  " + bytesToHex(FATTableInput));
            System.out.println("0x000 Media Descriptor:    " + FatTable.getMediaDescriptor());
            System.out.println("0x001-0x007:               Not Evaluated");



            System.out.println("..........................");
            System.out.println(".....FAT Backup Table.....");
            System.out.println("..........................");
            byte[] FATTableInput2 = new byte[(volumeBootRecord.getSectorsPerFAT())*volumeBootRecord.getBytesPerSector()];
            fileInput.read(FATTableInput2,0,(volumeBootRecord.getSectorsPerFAT())*volumeBootRecord.getBytesPerSector());
            FATTable FatTable2 = new FATTable(FATTableInput2, volumeBootRecord.getSectorsPerFAT()*volumeBootRecord.getBytesPerSector());

            //System.out.println("Fat Table:  " + bytesToHex(FATTableInput2));
            System.out.println("0x000 Media Descriptor:    " + FatTable2.getMediaDescriptor());
            System.out.println("0x001-0x007:               Not Evaluated");

            List<Cluster> clusters = new ArrayList();

            for (int i=0; i<volumeBootRecord.getTotalLogicalSectors()/volumeBootRecord.getSectorsPerCluster(); i++) {
                byte[] clusterData = new byte[(volumeBootRecord.getSectorsPerCluster()) * volumeBootRecord.getBytesPerSector()];
                fileInput.read(clusterData, 0, (volumeBootRecord.getSectorsPerCluster()) * volumeBootRecord.getBytesPerSector());
                Cluster cluster = new Cluster(clusterData, volumeBootRecord.getBytesPerSector()*volumeBootRecord.getSectorsPerCluster());
                clusters.add(cluster);
/*                if (i>=0 && i<1) {
                    System.out.println("***********Cluster #" + i + "*********");
                    System.out.println(cluster.toString());
                }*/

                //System.out.println("ClusterData:  " + bytesToHex(clusterData));
            }


            System.out.println("........................");
            System.out.println(".....Root Directory.....");
            System.out.println("........................");
            Directory rootDirectory = new Directory();

            int currentCluster = volumeBootRecord.getRootDirCluster();
            while (currentCluster!=0xFFFFFFF) {
                System.out.println("Cluster: " + currentCluster);
                rootDirectory.addCluster(clusters.get(currentCluster-2));
                currentCluster = FatTable.getNextCluster(currentCluster);
            }

            int fileCount = 0;
            int[] valueOfByte13 = new int[256];
            for (int i=0;i<256;i++) {
                valueOfByte13[i] = 0;
            }
            List<FAT32FileHeader> fileHeaders = rootDirectory.getFAT32FileHeaders();
            for (FAT32FileHeader fileHeader:fileHeaders) {
                if (fileHeader.getAttributes()!=15 && fileHeader.getAttributes() != 0) {
                    System.out.println(fileHeader.getFilename() + "." + fileHeader.getFileExtension() + " byte13: " + fileHeader.getByte13() + " Attributes: " + fileHeader.getAttributes());
                    valueOfByte13[fileHeader.getByte13()]++;
                    fileCount++;
                }
            }

            System.out.println("............................");
            System.out.println(".....Statistics byte 13.....");
            System.out.println("............................");
            System.out.println("Filecount: " + fileCount);
            for (int i=0; i<256;i++) {
                if (valueOfByte13[i]>0) {
                    System.out.println("value: " + i + ", count: " + valueOfByte13[i]);
                }
            }

/* //Read Master Boot Record:
            List<Partition> partitions = new ArrayList<Partition>();
            for (int i=0; i<4; i++) {
                byte[] pt1 = new byte[16];
                Partition partition = new Partition(pt1);
                partitions.add(partition);
                fileInput.read(pt1,0,16);
                System.out.println("Partition Table #"+i+": " + bytesToHex(pt1));
                System.out.println(partition);
            }

            byte[] eombr = new byte[2];
            fileInput.read(eombr,0,2);
            System.out.println("End of boot sector: " + bytesToHex(eombr));

            byte[] secondSector = new byte[512];
            fileInput.read(secondSector,0,512);
            System.out.println("Second sector: " + bytesToHex(secondSector));

            */
        } catch (IOException e) {
            //Catch the IO error and print out the message
            System.out.println("Error message: " + e.getMessage());
        } finally {
            //Must remember to close streams
            // Check to see if they are null in case there was an
            // IO error and they are never initialized
            if (fileInput != null) {
                fileInput.close();
            }
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

}

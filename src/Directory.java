import java.util.ArrayList;
import java.util.List;

public class Directory {
    List<Cluster> clusters;

    public Directory() {
        clusters = new ArrayList();
    }


    public void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }

    public List<FAT32FileHeader> getFAT32FileHeaders() {
        List<FAT32FileHeader> fileHeaders = new ArrayList<FAT32FileHeader>();
        for (Cluster cluster:clusters) {
            for (int i=0;i<cluster.getSize(); i+=32) {
                int[] record = new int[32];
                for (int j=0;j<32; j++) {
                    record[j]=cluster.clusterData[i+j];
                }

                FAT32FileHeader fileHeader = new FAT32FileHeader(record);
                fileHeaders.add(fileHeader);
            }
        }
        return fileHeaders;
    }
}

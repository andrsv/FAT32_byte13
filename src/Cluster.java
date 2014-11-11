import java.util.Arrays;

public class Cluster {
    int[] clusterData;

    public Cluster(byte[] clusterData, int size) {
        this.clusterData = new int[size];
        for (int i=0; i<size; i++) {
            this.clusterData[i] = ConvertUtil.byteToUnsigned(clusterData[i]);
        }
    }

    @Override
    public String toString() {
        String output = "";
        for (int i=0; i<clusterData.length;i++) {
            output += (char)clusterData[i];
            if (i%32==31) {
                output += "\n";
            }
        }
        return output;
    }

    public int getSize() {
        return clusterData.length;
    }
}

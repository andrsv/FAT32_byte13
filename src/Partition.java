import java.util.Arrays;

public class Partition {
    private byte[] partitionData;

    public Partition(byte[] partitionData) {
        this.partitionData = partitionData;
    }

    public String getType() {
        if ((partitionData[4] == 0x0b) || (partitionData[4] == 0x0c)) {
            return "FAT";
        } else {
            return "unknown("+partitionData[4]+")";
        }
    }

    @Override
    public String toString() {
        return "Partition{" +
                "partitionData=" + Arrays.toString(partitionData) +
                '}' + " Type: " + getType();
    }
}

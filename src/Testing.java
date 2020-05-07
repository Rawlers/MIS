import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Testing {

    static void testMethod(int degree, String filename) throws IOException {
        long[] timeavg = new long[20];
        long[] stnodesavg = new long[20];
        long[] opsavg = new long[20];

        for (int i = 0; i < 20; i++) {
            int size = (i + 1) * 5;
            int edgecount = 9999;
            long[] times = new long[100];
            long[] stnodes = new long[100];
            long[] ops = new long[100];

            for (int j = 0; j < 100; j++) {
                Graph test = Graph.randomGraph(degree, edgecount, size);
                long start = System.nanoTime();
                Graph.mis3(test);
                long time = System.nanoTime() - start;
                times[j] = time;
                stnodes[j] = test.stnodes;
                ops[j] = test.ops;
            }
            long timesum = 0;
            long stnodessum = 0;
            long opssum = 0;
            for (int j = 0; j < 100; j++) {
                timesum += times[j];
                stnodessum += stnodes[j];
                opssum += ops[j];
            }
            timeavg[i] = timesum / 100;
            stnodesavg[i] = stnodessum / 100;
            opsavg[i] = opssum / 100;

        }
        FileWriter data = new FileWriter(filename + ".csv");
        data.append("Size");
        data.append(",");
        data.append("Time");
        data.append(",");
        data.append("STnodes");
        data.append(",");
        data.append("Ops");
        data.append("\n");

        for (int i = 0; i < 20; i++) {
            data.append(Integer.toString((i + 1) * 5));
            data.append(",");
            data.append(Long.toString(timeavg[i]));
            data.append(",");
            data.append(Long.toString(stnodesavg[i]));
            data.append(",");
            data.append(Long.toString(opsavg[i]));
            data.append("\n");
        }
        data.flush();
        data.close();
    }

    static void boxplot(int degree, int size, String filename) throws IOException {
        int edgecount = 9999;
        long[] times = new long[100];
        long[] stnodes = new long[100];
        long[] ops = new long[100];

        for (int j = 0; j < 100; j++) {
            Graph test = Graph.randomGraph(degree, edgecount, size);
            long start = System.nanoTime();
            Graph.mis3(test);
            long time = System.nanoTime() - start;
            times[j] = time;
            stnodes[j] = test.stnodes;
            ops[j] = test.ops;
        }

        Arrays.sort(times);
        Arrays.sort(stnodes);
        Arrays.sort(ops);

        FileWriter timedata = new FileWriter(filename + "time.csv");
        FileWriter stnodesdata = new FileWriter(filename + "stnodes.csv");
        FileWriter opsdata = new FileWriter(filename + "ops.csv");


        for (int i = 0; i < 100; i++) {
            timedata.append(Long.toString(times[i]));
            timedata.append(",");
            stnodesdata.append(Long.toString(stnodes[i]));
            stnodesdata.append(",");
            opsdata.append(Long.toString(ops[i]));
            opsdata.append(",");
        }
        timedata.flush();
        stnodesdata.flush();
        opsdata.flush();

        timedata.close();
        stnodesdata.close();
        opsdata.close();

    }


    public static void main(String[] args) {
        try {
            //boxplot(6, 100, "degree6");
            for(int i = 3; i <= 6; i++) {
                testMethod(i, "degree" + i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


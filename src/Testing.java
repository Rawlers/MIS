public class Testing {

    public static void main(String[] args) {
        long[] averages3 = new long[40];

        for(int i = 0; i < 40; i++) {
            int degree = 3;
            int size = (i + 1) * 5;
            int edgecount = size * degree;
            long[] times = new long[100];

            for(int j = 0; j < 100; j++) {
                Graph test = Graph.randomGraph(degree, edgecount, size);
                long start = System.nanoTime();
                Graph.mis3(test);
                long time = System.nanoTime() - start;
                times[j] = time;
            }
            long sum = 0;
            for(int j = 0; j < 100; j++) {
                sum += times[j];
            }
            averages3[i] = sum/100;
        }
        System.out.println("MAX DEGREE 3:");
        for(int i = 0; i < 40; i++) {
            System.out.println("Size: " + (i+1)*5 + " | Time: " + averages3[i]);
        }

        long[] averages4 = new long[40];

        for(int i = 0; i < 40; i++) {
            int degree = 4;
            int size = (i + 1) * 5;
            int edgecount = size * degree;
            long[] times = new long[100];

            for(int j = 0; j < 100; j++) {
                Graph test = Graph.randomGraph(degree, edgecount, size);
                long start = System.nanoTime();
                Graph.mis3(test);
                long time = System.nanoTime() - start;
                times[j] = time;
            }
            long sum = 0;
            for(int j = 0; j < 100; j++) {
                sum += times[j];
            }
            averages4[i] = sum/100;
        }
        System.out.println("MAX DEGREE 4:");
        for(int i = 0; i < 40; i++) {
            System.out.println("Size: " + (i+1)*5 + " | Time: " + averages4[i]);
        }

        long[] averages5 = new long[40];

        for(int i = 0; i < 40; i++) {
            int degree = 5;
            int size = (i + 1) * 5;
            int edgecount = size * degree;
            long[] times = new long[100];

            for(int j = 0; j < 100; j++) {
                Graph test = Graph.randomGraph(degree, edgecount, size);
                long start = System.nanoTime();
                Graph.mis3(test);
                long time = System.nanoTime() - start;
                times[j] = time;
            }
            long sum = 0;
            for(int j = 0; j < 100; j++) {
                sum += times[j];
            }
            averages5[i] = sum/100;
        }
        System.out.println("MAX DEGREE 5:");
        for(int i = 0; i < 40; i++) {
            System.out.println("Size: " + (i+1)*5 + " | Time: " + averages5[i]);
        }
    }
}


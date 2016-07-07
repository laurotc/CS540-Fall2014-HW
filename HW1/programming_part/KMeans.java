import java.util.ArrayList;

/**
 * A k-means clustering algorithm implementation.
 *
 */
public class KMeans {

    public KMeansResult cluster(double[][] centroids, double[][] instances, double threshold) {
        /* ... YOUR CODE GOES HERE ... */
        
        int N = instances.length;
        int K = centroids.length;
        int D = centroids[1].length;

        KMeansResult result = new KMeansResult();
        result.clusterAssignment = new int[N];
        ArrayList<Double> distortion = new ArrayList<>(); //Array List for distortion, we don't know how many interactions

        int iteration = 0;

        while (true) {
                       
            /**Get instances centroid**/
            //Loop for each instance
            for (int n = 0; n < N; n++) {
                double minDistance = Double.MAX_VALUE;      
                //Loop for each centroid
                for (int k = 0; k < K; k++) {
                    double dist = getEuclideanDistance(instances[n], centroids[k]);
                    if (dist < minDistance) {                       
                        minDistance = dist;
                        result.clusterAssignment[n] = k;
                    }
                }              
            }
            /**END - Get instances centroid**/

            
            /**Check Orphan**/
            boolean hasOrphan = true;
            int orphan = -1;
            
            //Checking if all centroids have instances
            while (hasOrphan) {
                int orphan_aux = -1;
                //Loop for each centroid
                for (int k = 0; k < K; k++) {
                    orphan = orphan_aux;
                    //Loop for each instance
                    for (int n = 0; n < N; n++) {
                        //Find in all instances the specific centroid
                        if (result.clusterAssignment[n] == k) {   
                            //If it is found then break the loop
                            orphan_aux = -1;
                            break;    
                        } 
                        else {//else, set k as an orphan 
                            orphan_aux = k;
                        }
                    }
                }

                //If the centroid isn't found, then it's orphan
                if (orphan >= 0) {
                    //System.out.println("\nHas orphan:" + orphan +"");
                    double maxDistance = -1;
                    int maxInstance = -1;
                    //Loop for all instances
                    for (int n = 0; n < N; n++) {
                        //Calculate the instance which has the biggest distance from the centroid
                        double dist = getEuclideanDistance(instances[n], centroids[result.clusterAssignment[n]]);
                        if (dist > maxDistance) {
                            maxDistance = dist;
                            maxInstance = n; 
                        }                       
                    }
                    //Set this instance to the orphan
                    result.clusterAssignment[maxInstance] = orphan;
                } else {
                    //System.out.println("No orphan:" + orphan);
                    hasOrphan = false;
                }
            }
            /**END - Check Orphan**/

            /**Update Centroids**/
            //Loop for each centroid
            for (int k = 0; k < K; k++) {
                double[] sum = new double[D];
                int inst = 0;
                //Loop for each instance
                for (int n = 0; n < N; n++) {
                    //Find the instance which has the specific centroid
                    if (result.clusterAssignment[n] == k) {
                        //For each dimension, sum the instance values
                        for (int d = 0; d < D; d++) {
                            sum[d] += instances[n][d];
                        }
                        inst++;
                    }
                }

                //For each dimension update the centroid value
                for (int d = 0; d < D; d++) {
                    centroids[k][d] = sum[d] / inst;
                    //System.out.println("Updated Centroid:" + result.centroids[k][d]);
                }
            }
            /**END - Update Centroids**/

            /**Distortion**/
            double sumDistortions = 0;
            //For each centroid and instance
            for (int k = 0; k< K; k++) {    
                for(int n=0; n < N; n++) { 
                    //Find the instance whisch has the specific centroid
                    if(result.clusterAssignment[n] == k){
                        //Sum the squared distance between them
                        sumDistortions += Math.pow(getEuclideanDistance(instances[n], centroids[k]), 2);
                    }
                }    
                //System.out.print("\nDistortion:" + sumDistortions);
            }
            //Set the value in the array list
            distortion.add(sumDistortions);
            
            /**END - Distortion**/
                
            /**Verify if it is time to stop**/
            if (iteration > 0 && 
                    (Math.abs((distortion.get(iteration) - distortion.get(iteration - 1))/distortion.get(iteration - 1))) 
                    < threshold) 
            {
                //System.out.println("break");
                break;
            }
            else{
                //If not increase the iterations
                iteration++;
            }
        }

        //When it stops, set the final centroids and the distortions
        result.centroids = centroids;
        result.distortionIterations = new double[distortion.size()];
        for (int x = 0; x < distortion.size(); x++) {
            result.distortionIterations[x] = distortion.get(x);
        }

        return result;
    }

    /**Get Euclidean Distance of a instance and a centroid**/
    public double getEuclideanDistance(double[] instance, double[] centroid) {

        double sum = 0;
        for (int x = 0; x < centroid.length; x++) {
            sum += Math.pow(instance[x] - centroid[x], 2);
        }
        return Math.sqrt(sum);
    }
}

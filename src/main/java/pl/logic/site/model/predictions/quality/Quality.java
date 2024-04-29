package pl.logic.site.model.predictions.quality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class with static methods used to assess the quality of classification for an algorithm.
 *
 * @author Kacper
 */
public class Quality {

    /**
     * Calculates the accuracy of a list of results.
     * That is, simply calculating the accuracy for the prediction made on the test set,
     * excluding the results for patients who have null in the disease
     * because there is nothing to refer to and calculating the accuracy would be pointless.
     *
     * @param results a list of results (from predictions made on the testing set)
     * @return the accuracy as a double value between 0 and 1
     */
    public static Double calculateAccuracy(List<Result> results) {
        List<Result> filteredResults = deleteNullExpectedResults(results);
        double accumulator = 0.0;
        for (Label result : filteredResults) {
            if (result.getExpected().getName().equals(result.getResult().getName())) accumulator++;
        }
        if (filteredResults.isEmpty()) return 0.0;
        else return (accumulator / filteredResults.size());
    }

    /**
     * Returns an array of integers where the sum of the elements is equal to the specified size,
     * and the elements are divided into two parts according to the specified proportions.
     *
     * @param input an array of two integers, where the first element is the proportion of elements in the first part,
     * and the second element is the proportion of elements in the second part
     * @param size the total number of elements in the dataset
     * @return an array of two integers, where the first element is the number of elements in the first part,
     * and the second element is the number of elements in the second part
     * @throws IllegalArgumentException if the input is not an array of two integers, or if the value of one of the integers is not a number
     */
    public static int[] countProportions(String[] input,int size){
        if(input.length != 2){
            throw new IllegalArgumentException("Proportions must have exactly two integers");
        }
        int[] k = new int[input.length];
        try{
            for (int i=0; i<input.length; i++){
                k[i] = Integer.parseInt(input[i]);
            }
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Value of proportion is not a number");
        }

        int whole = Arrays.stream(k).sum();
        int part = Math.floorDiv(size,whole);

        k[0] *= part;
        k[1] = size-k[0];
        return k;
    }

    /**
     * Deletes all results from a list that have a null expected value.
     * For null expected values, it would be impossible to calculate precision
     *
     * @param results a list of results (from predictions made on the testing set)
     * @return a list of results without null expected values
     */
    private static List<Result> deleteNullExpectedResults(List<Result> results) {
        return results.stream().filter(r -> r.getExpected() != null).toList();
    }
}

package a3;

import java.util.*;

public class ConsoleHistogram {

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);
		
		int numBins = scan.nextInt(); // number of bins	
		if (numBins <= 0) { // edge case check
			scan.close();
			System.out.println();
			System.out.println("Error: Number of bins must be greater than zero.");
			return;
		}
		
		// Creates list containing data
		List<Integer> data = new ArrayList<Integer>();
		while(scan.hasNextInt()) {
			data.add(scan.nextInt());
		}
		scan.close();
		Collections.sort(data);
		
		if (data.size() == 0) { // edge case check
			System.out.println("Error: No data entered.");
			return;
		}
		
		// Print preliminary, relevant information about data
		System.out.println();
		System.out.println("Number of Bins: " + numBins);
		System.out.println("Number of Player Heights: " + data.size());
		System.out.println("Min: " + data.get(0));
		System.out.println("Max: " + data.get(data.size() - 1));
		System.out.println("Mean: " + intListMean(data));
		System.out.println("Median: " + intListMedian(data));
		System.out.println();
		
		// Set up bins, counts, and histogram
		double bins[] = new double[numBins + 1]; // bin
		int counts[] = new int[numBins]; // count in bins
		
		makeBins(bins, numBins, data);
		fillCounts(counts, bins, data);
		printGraph(makeStringHistogram(bins, counts));
		
	}
	
	/* intListMean
	 * Calculates the mean from a list of integers
	 */
	public static double intListMean (List<Integer> data) {
		double sum = 0;
		for (Integer i : data) {
			sum += i;
		}
		return (sum/data.size());
	}
	
	/* intListMedian
	 * Finds the median from a sorted list of integers
	 */
	public static double intListMedian (List<Integer> data) {
		return (data.size() % 2 == 0) ? 
			(data.get( (data.size()/2) - 1) + data.get( ( data.size()/2) ) )/2 : 
			data.get( (data.size() - 1) /2);
	}
	
	/* makeBins
	 * Builds and calculates appropriate ranges for bins given a number of bins and a data list	 * 
	 * 
	 * Decrements and increments low and high of the bin range to give cleaner rounding for steps  
	 * if possible
	 */
	public static void makeBins (double[] bins, int numBins, List<Integer> data) {
		
		int low = data.get(0); 
		int high = data.get(data.size() - 1);
		double range = high - low;		
		
		low -= (range % numBins > (numBins - 3) && numBins > 4) ? 1 : 0;
		high += (range % numBins == (numBins - 2) && numBins > 4) ? 1 : 0;
		range = high - low;

		Arrays.fill(bins, low);
		for (int i = 1; i < bins.length; i++) {
			bins[i] += i * range/(numBins);
		}
	}
	
	/* fillCounts
	 * Fills an int array holding counts with appropriate count corresponding to each bin from a sorted
	 * data list of integers  
	 */
	public static void fillCounts (int[] counts, double[] bins, List<Integer> data) {
		
		int memo, lastSize = data.size();
		
		for(int i = 0; 0 < data.size() && i < counts.length; i++) {
			
			memo = data.get(0);
			
			if (memo >= bins[i] && memo < bins[i + 1]) {
				data.removeIf(c -> c == data.get(0));
				counts[i] += lastSize - data.size();
				lastSize = data.size();
				i--;
				
			} else if (i == counts.length -1 && memo == data.get(data.size() - 1)) {
				counts[i] += data.size();			
			} 
			
		}
		
	}
	
	/* scaleCounts
	 * Scales counts appropriately to set bar heights that fit in viewing range of histogram using the ratio 
	 * in proportion to mode * 30. In other words, scales around a max "count" of 30.
	 * 
 	 * Input: int array of counts per bin, the counts in the mode of the data (highest frequency)
	 * 
	 * Output: int array of the counts after scaling
	 */
	public static int[] scaleCounts (int[] counts, int mode) {
		int[] countsScaled = new int[counts.length];
		for (int i = 0; i < countsScaled.length; i++) {
			countsScaled[i] = counts[i]*30/mode; // scales to fit graph around mode (rounds down)
		}
		return countsScaled;
	}

	/* makeStringHistogram
	 * Builds a histogram of ASCII characters inside a 2D String array
	 * 
	 * Input: the bin ranges as a double array, and the counts per bin as an int array
	 * 
	 * Output: a 2D string array containing ASCII "image" of the histogram, showing the bin ranges, 
	 * x axis and titles, along with the actual counts within each bin
	 */
	public static String[][] makeStringHistogram (double[] bins, int[] counts) {
		
		int mode = -1; // establish highest frequency
		for (int i: counts) {
			mode = Math.max(i, mode);
		}	
		
		int x_axis = bins.length * 2;
		int y_axis = (mode > 30) ? 36 : mode + 6; // allocates 6 spaces for titles, marks, spaces, the x axis, and counts
		int[] countsScaled = (mode > 30) ? scaleCounts(counts, mode) : counts.clone();
		
		String[][] graph = new String[x_axis][y_axis];
		for (String[] row : graph) {
			Arrays.fill(row, "     ");
		}
		
		for(int x = 1; x < x_axis; x++) {	
			
			if (x % 2 == 1) {
				for (int y = y_axis - 4; y >= 0; y--) { // fills column
					graph[x][y] = "   "; // 3 spaces at every odd
				}
				
			} else if (x % 2 == 0) { // 5 spaces at every even
				
				for (int y = y_axis - 4, barHeight = countsScaled[(x-2)/2]; 
						y >= 0 && barHeight >= 0; 
						y--, barHeight--) {
					
					if (barHeight > 1) {
						graph[x][y] = "| ^ |";
						
					} else if (barHeight == 1) {
						graph[x][y] = "+---+"; 
						
					} else if (barHeight == 0) {
						graph[x][y] = " " + counts[(x-2)/2] + " ";
						
						for (int k = graph[x][y].length(); k < 5; k++) { 
							graph[x][y] = " " + graph[x][y]; // fills spaces if count digits < 3
						}
						
					}
					
				}
				
			}	
			
			graph[x][y_axis - 3] = "____" + ((x == x_axis - 1) ? "_" : "" ); 
			graph[x][y_axis - 2] = (x % 2 != 0) ? String.format("%.2f", bins[(x-1)/2]) : " - ";
			graph[x][y_axis - 1] = "    "; // 4 spaces for all bottom row to align x_axis label

		}
		
		graph[(x_axis - 4)/2][0] = " NBA PLAYER HEIGHTS";  // Places title in center view
		graph[(x_axis - 4)/2 - ((counts.length < 2) ? 0 : 1)][y_axis-1] = "  Player Height (in Inches)";
		
		return graph;
	} 
	
	/* printGraph
	 * Takes a 2d array of any printable type and prints it line by line (across x axis before going down y)
	 */
	public static <T> void printGraph (T[][] graph) {
		for (int y = 0; y < graph[0].length; y++) {
			for (int x = 0; x < graph.length; x++) { // print x's on one line before going down a line per y
				System.out.print(graph[x][y]);
			}
			System.out.println();
		}
	}
	
}
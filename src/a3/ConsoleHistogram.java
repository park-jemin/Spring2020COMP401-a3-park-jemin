package a3;

import java.util.*;

public class ConsoleHistogram {

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);
		
		int numBins = scan.nextInt(); // number of bins		
		
		List<Integer> data = new ArrayList<Integer>();
		double bins[] = new double[numBins + 1]; // bin
		int counts[] = new int[numBins]; // count in bins
		
		fillData (data, scan);
		printData(numBins, data);	
		makeBins(bins, numBins, data);
		fillCounts(counts, bins, data);
		
		int mode = -1;
		for (int c: counts) {
			mode = Math.max(c, mode);
		}		
		
		int[] countsScaled = scaleCounts(counts, mode);

		int x_axis = numBins * 2 + 2,  y_axis = (mode <= 30) ? mode + 6 : 36;
		
		String[][] graph = makeHistogram(x_axis, y_axis, bins, countsScaled, counts);//new String[graphX][graphY];
		
		graph[x_axis/2 - 2][0] = "NBA PLAYER HEIGHTS";
		graph[x_axis/2 - 3][y_axis-1] = "Player Height (in Inches)";

		printHistogram(graph, x_axis, y_axis);
		
	}
	
	static void fillData(List<Integer> data, Scanner scan) {
		while(scan.hasNextInt()) {
			data.add(scan.nextInt());
			
		}
		scan.close();
//		if(scan.next().equals("end")) {
//			scan.close();
//		}
		
		Collections.sort(data);
	}
	
	// The following methods that require List<Integer> also require the list to be sorted first
	
	
	public static double intListMean (List<Integer> list) {
		double sum = 0;
		for (Integer i : list) {
			sum += i;
		}
		return (sum/list.size());
	}
	
	public static double intListMedian (List<Integer> list) {
		return (list.size() % 2 == 0) ? 
			(list.get( (list.size()/2) - 1) + list.get( (list.size()/2)) ) /2 : 
			list.get( (list.size() - 1) /2);
	}
	
	public static void printData (int numBins, List<Integer> data) {
		System.out.println();
		System.out.println("Number of Bins: " + numBins);
		System.out.println("Number of Player Heights: " + data.size());
		System.out.println("Min: " + data.get(0));
		System.out.println("Max: " + data.get(data.size() - 1));
		System.out.println("Mean: " + intListMean(data));
		System.out.println("Median: " + intListMedian(data));
		System.out.println();
	}
	
	public static void makeBins (double[] bins, int numBins, List<Integer> data) {
		int low = data.get(0), high = data.get(data.size() - 1);
		double range = high - low;
		if (range % numBins == (numBins - 1) && numBins > 4) {
			low--;
		} else if ((range % numBins == (numBins - 2) ) && numBins > 4) {
			low--;
			high++;
		} 
		range = high - low;

		Arrays.fill(bins, low);
		for (int i = 1; i < bins.length; i++) {
			bins[i] += i * range/(numBins);
		}
	}
	
	public static void fillCounts (int[] counts, double[] bins, List<Integer> data) {


		
		int memo, lastSize = data.size(), i = 0;
		
		while(0 < data.size() && i < counts.length) {
			
			memo = data.get(0);
			
			if (memo >= bins[i] && memo < bins[i + 1]) {
				data.removeIf(x -> x == data.get(0));
				counts[i] += lastSize - data.size();
				lastSize = data.size();
				
			} else if (i == counts.length -1 && memo == data.get(data.size()-1)) {
				counts[i] += data.size();
				i++;
				
			} else {
				i++;
			}
			
		}
	}
	
	public static int[] scaleCounts (int[] counts, int mode) {
		int[] countsScaled = new int[counts.length];
		if (mode <= 30) {
			for (int i = 0; i < counts.length; i++) {
				countsScaled[i] = counts[i];
			}
			
		} else {
			for (int i = 0; i < countsScaled.length; i++) {
				countsScaled[i] = Math.round(counts[i]*30/mode); // scales to fit graph
			}
		}
		return countsScaled;
	}

	public static String[][] makeHistogram (int x_axis, int y_axis, double[] bins, int[] countsScaled, int[] counts) {
		
		String[][] graph = new String[x_axis][y_axis];
		
		for (String[] col : graph) {
			Arrays.fill(col, "   ");
		}
		
		for(int x = 0; x < x_axis; x++) { // fills rest of graph
			
			if (x < 1) {
				continue;
				
			} else if (x % 2 == 1) {
				graph[x][y_axis - 2] = String.format("%.2f", bins[(x-1)/2]); // 5 spaces at every 3
				graph[x][y_axis - 3] = "_____";
				for (int y = y_axis - 4; y >= 0; y--) { // fills column
					graph[x][y] = "     ";
					
				}
				
			} else if (x % 2 == 0) {
				
				int stars = countsScaled[(x-2)/2]; 
				graph[x][y_axis - 2] = " - "; 
				graph[x][y_axis - 3] = "___";
				
				for (int y = y_axis - 4; y >= 0; y--) {
					if (stars < 0) {
						break;
					} else if (stars > 1) {
						graph[x][y] = "|^|";
						
					} else if (stars == 1) {
						graph[x][y] = "+-+"; 
					} else if (stars == 0) {
						graph[x][y] = "" + counts[(x-2)/2];
						
						for (int k = graph[x][y].length(); k < 3; k++) {
							graph[x][y] = " " + graph[x][y];
						}
						
					}
					stars--;
				}
				
			}	
			
			graph[x][y_axis - 1] = "    ";

		}
		return graph;
	}
	
	public static void printHistogram (String[][] graph, int x_axis, int y_axis) {
		for (int y = 0; y < y_axis; y++) {
			for (int x = 0; x < x_axis; x++) {
				System.out.print(graph[x][y]);
			}
			System.out.println();
		}
	}
	
	
}
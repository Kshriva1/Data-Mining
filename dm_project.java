import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class dm_project {
	private Scanner fileinput;
	private final int noOfUser = 943;
	private final int noOfItems = 1682;
	private int[][] mMat = new int[noOfUser+1][noOfItems+1];
	private double[][] simiMat = new double[noOfUser+1][noOfItems+1];
	private int[] rowSum = new int[noOfUser+1];
	private int[] ratingCount = new int[noOfUser+1];
	private int[][] hm1 = new int[noOfUser+1][noOfUser+1];
	private double[][] hm = new double[noOfUser+1][noOfUser+1];
	private int[][] predarray = new int[noOfUser+1][noOfItems+1]; 
	private static FileWriter filewriter;
	private static File file;
	
	
    
	
	//This method recommends the user to give ratings to the items
	private void predictMissingValues(){
		for(int i=1;i<=noOfUser;i++){
			for(int j=1;j<=noOfItems;j++){
				if(mMat[i][j] == 0){
					double pred_nume = 0.0;
					double pred_denom = 0.0;
					for(int k=1;k<=hm.length-1;k++){
						if(mMat[k][j] != 0){
							pred_nume += mMat[k][j] * hm[i][k];
							pred_denom += Math.abs(hm[i][k]);
						
						}
					}
					
					double predmissingvalue = 0.0;
					predmissingvalue = Math.round(pred_nume/pred_denom);
			
					if(predmissingvalue < 1)
						predmissingvalue = 1;
					else if(predmissingvalue > 5)
						predmissingvalue = 5;	
					predarray[i][j] = (int)predmissingvalue;
					
				}
				else
					predarray[i][j] = mMat[i][j];
			}
		}
	}
	
	 //This method writes to Output.txt
		private void writeOutputFile(){
			try {
				file = new File("/home/kundan/Desktop/Output.txt");
				filewriter = new FileWriter(file);
				for(int i=1;i<=noOfUser;i++){
					for(int j=1;j<=noOfItems;j++){
						filewriter.write(i + " " + j + " " + predarray[i][j] + "\n");
					}
				}
				filewriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 		
		}
		
		// This method reads input data from file and puts into the 2D matrix.
		private void setMatrix() {
			try {
				fileinput = new Scanner(new FileInputStream("/home/kundan/Desktop/train_all_txt.txt"));
				while(fileinput.hasNext()){
					int user = fileinput.nextInt();
					int item = fileinput.nextInt();
					int rating = fileinput.nextInt();
					mMat[user][item]=rating;
					ratingCount[user]++;
					rowSum[user] += rating;
				}
			} catch (FileNotFoundException e) {
				System.err.println("Error: File Not found.");
				System.exit(1);
			}
		}
	
	
	 // This method calculates the Pearson coefficient based on the normalized matrix
	 
	private void calculatePearsonCoefficient(){
		for(int i=1;i<=noOfUser;i++){
			for(int j=1;j<=noOfUser;j++){
				if(i != j)
				{	
					double pearsonCoefficient = 0.0;
					pearsonCoefficient = pearsonCorrelation(simiMat[i],simiMat[j], ratingCount[i], ratingCount[j]);
					hm[i][j] = pearsonCoefficient;
					hm1[i][j] = j;
				}
			}
		}
	}
	
	//This method returns the pearson coefficient returned by the pearson correlation formula
		private double pearsonCorrelation(double[] X, double[] Y, int countX, int countY) {
			double sXY1 = 0.0;
			double sX1 = 0.0;
			double sY1 = 0.0;
			double sqX1 = 0.0;
			double sqY1 = 0.0;
			double sqSumX1 = 0.0;
			double sqSumY1 = 0.0;
			
			double Coeff = 0.0;
			
			for(int i=1;i<X.length;i++){
				sXY1 += X[i] * Y[i];
				sX1 += X[i];
				sY1 += Y[i];
				sqX1 += Math.pow(X[i], 2);
				sqY1 += Math.pow(Y[i], 2);
			}
			
			sqSumX1 = Math.pow(sX1, 2);
			sqSumY1 = Math.pow(sY1, 2);
			
			Coeff = ((noOfItems * sXY1) - (sX1 * sY1))/(Math.sqrt(((noOfItems * sqX1) - sqSumX1)*((noOfItems * sqY1) - sqSumY1)));

			return Coeff;
		}
	
	
	 // This method normalizes the given matrix by calculating the mean and subtracting mean from each rating
	 
	private void normalizeMatrix(){

	for(int i=1;i<=noOfUser;i++){
			for(int j=1;j<=noOfItems;j++){
				if(mMat[i][j] != 0){
					simiMat[i][j] = (double) (mMat[i][j]) - ((double)rowSum[i]/noOfItems);
				}
				else
					simiMat[i][j] = (double) (mMat[i][j]) - ((double)rowSum[i]/noOfItems);
			}
		}
	}
	
	
	

	public static void main(String[] args) {
		dm_project DM = new dm_project();
		DM.setMatrix();
		DM.normalizeMatrix();
		DM.calculatePearsonCoefficient();
		DM.predictMissingValues();
		DM.writeOutputFile();
	}
}

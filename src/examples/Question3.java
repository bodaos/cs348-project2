package examples;

import java.util.Random;

public class Question3 {
	public static int elementN =131072; 
	public static int threadN = 10; 
	/**
	 * @param args
	 */
	static class CoarseAdd extends Thread{
		public CoarseHashSet<Integer> set; 
		public int work; 
		public CoarseAdd(CoarseHashSet<Integer> set, int work){
			this.set =set; 
			this.work = work; 
		}
		@Override
		public void run() {
			Random generator = new Random();
			for (int i = 0; i < work ; i++ ){
				Integer integer = generator.nextInt(); 
				set.add(integer); 
				set.contains(generator.nextInt()); 
			}
			
		}
	}
	static class FineAdd extends Thread{
		public FineHashSet<Integer> set; 
		public int work; 
		public FineAdd(FineHashSet<Integer> set, int work){
			this.work = work; 
			this.set =set; 
		}
		@Override
		public void run() {
			Random generator = new Random();
			for (int i = 0; i < work ; i++ ){
				Integer integer = generator.nextInt(); 
				set.add(integer); 
				set.contains(generator.nextInt()); 
			}
			
		}
	}
	static class TransAdd extends Thread{
		public CarlSTMHashSet<Integer> set; 
		public int work; 
		public TransAdd( CarlSTMHashSet<Integer> set, int work){
			this.work = work;
			this.set =set; 
		}
		@Override
		public void run() {
			Random generator = new Random();
			for (int i = 0; i < work; i++ ){
				Integer integer = generator.nextInt(); 
				set.add(integer); 
				set.contains(generator.nextInt()); 
			}
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int[] threadNArray = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192,16384, 32768, 65536, 131072 }; 
		long[][] timeArray = new long[3][threadNArray.length];
		for (int j = 0; j < threadNArray.length; j ++ ){
			threadN = threadNArray[j]; 
			int capacity = 2000;
			CoarseHashSet.SetCapacity(capacity); 
			FineHashSet.SetCapacity(capacity);
			CarlSTMHashSet.SetCapacity(capacity); 
			/**
			 *CoarseHashSet Experiment
			 */
			long startTime =System.currentTimeMillis();
			CoarseHashSet<Integer> coarse = new CoarseHashSet<Integer>(); 
			CoarseAdd[] threadsArray = new CoarseAdd[threadN]; 
			for (int i = 0; i < threadN ; i ++ ){
				threadsArray[i] = new CoarseAdd(coarse, elementN/threadN); 
				threadsArray[i].start(); 
			}
			for (int i = 0; i < threadN ; i ++ ){
				try {
					threadsArray[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("hash add interrupted"); 
					e.printStackTrace();
				} 
			}
			long duration = System.currentTimeMillis() - startTime;
			//System.out.println("the coarse hash took " + duration + " miliseconds");
			timeArray[0][j] = duration; 
			System.out.print(duration+ "\t");
			/**
			 *FineHashSet Experiment
			 */
			startTime =System.currentTimeMillis();
			FineHashSet<Integer> fine = new FineHashSet<Integer>(); 
			FineAdd[] fineThreadsArray = new FineAdd[threadN]; 
			for (int i = 0; i < threadN ; i ++ ){
				fineThreadsArray[i] = new FineAdd(fine, elementN/threadN); 
				fineThreadsArray[i].start(); 
			}
			for (int i = 0; i < threadN ; i ++ ){
				try {
					fineThreadsArray[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("fine add interrupted"); 
					e.printStackTrace();
				} 
			}
			duration = System.currentTimeMillis() - startTime;
			//System.out.println("the Fine hash took " + duration + " miliseconds");
			timeArray[1][j] = duration; 
			System.out.print(duration+ "\t");
			/**
			 *TransHashSet Experiment
			 */
			startTime =System.currentTimeMillis();
			CarlSTMHashSet<Integer> trans = new CarlSTMHashSet<Integer>(); 
			TransAdd[] transThreadsArray = new TransAdd[threadN]; 
			for (int i = 0; i < threadN ; i ++ ){
				transThreadsArray[i] = new TransAdd(trans, elementN/threadN); 
				transThreadsArray[i].start(); 
			}
			for (int i = 0; i < threadN ; i ++ ){
				try {
					transThreadsArray[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.print("trans add interrupted"); 
					e.printStackTrace();
				} 
			}
			duration = System.currentTimeMillis() - startTime;
			//System.out.println("the trans hash took " + duration + " miliseconds");
			
			timeArray[2][j] = duration; 
			System.out.print(duration+ "\n");
		}
		
	}

}

package examples;

import java.util.Random;

import carlstm.CarlSTM; 


public class Question5 {

	/**
	 * @param args
	 */
	static class TransAdd extends Thread{
		public CarlSTMHashSet<Integer> set; 
		public int work; 
		public int failure; 
		public TransAdd( CarlSTMHashSet<Integer> set, int work){
			this.work = work;
			this.set =set; 
		}
		@Override
		public void run() {
			Random generator = new Random();
			for (int i = 0; i < work; i++ ){
				int integer = generator.nextInt(); 
				set.add(integer); 
				set.contains(generator.nextInt()); 
			}
			failure = CarlSTM.printCount(); 		}
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long[] backoffArray = {0, 1, 2, 4, 6, 8, 10, 12, 14, 16,18, 20, 22, 24, 26, 28, 30,  32}; 
		long[] durationArray  = new long[backoffArray.length];
		long startTime =System.currentTimeMillis();
		long duration; 
		int failCount = 0; 
		for (int i = 0; i < backoffArray.length; i++){
			failCount = 0; 
			CarlSTM.SetBackoff(backoffArray[i]); 
			CarlSTMHashSet.SetCapacity(10); 
			startTime =System.currentTimeMillis();
			CarlSTMHashSet<Integer> trans = new CarlSTMHashSet<Integer>(); 
			TransAdd[] transThreadsArray = new TransAdd[100]; 
			for (int j = 0; j < 5 ; j ++ ){
				transThreadsArray[i] = new TransAdd(trans, 1000); 
				transThreadsArray[i].start(); 
			}
			for (int j = 0;j < 5 ; j ++ ){
				try {
					transThreadsArray[i].join();
					failCount += transThreadsArray[i].failure; 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.print("trans add interrupted"); 
					e.printStackTrace();
				} 
			}
			duration = System.currentTimeMillis() - startTime;
			durationArray[i] = duration; 
			System.out.println(backoffArray[i] + "\t" + failCount); 
		}
		
		System.out.println("done"); 
	}

}

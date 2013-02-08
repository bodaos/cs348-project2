package examples;

import java.util.Random;

import carlstm.CarlSTM;

import examples.Question3.TransAdd;

public class Question4 {

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
		long[] backoffArray = {0, 1, 10, 20, 30, 50, 60, 100, 200, 500, 1000, 2000, 5000, 10000,20000, 40000,  100000 }; 
		long[] durationArray  = new long[backoffArray.length];
		long startTime =System.currentTimeMillis();
		long duration; 
		int failCount = 0; 
		for (int i = 0; i < backoffArray.length; i++){
			failCount = 0; 
			CarlSTM.SetBackoff(backoffArray[i]); 
			CarlSTMHashSet.SetCapacity(1024); 
			startTime =System.currentTimeMillis();
			CarlSTMHashSet<Integer> trans = new CarlSTMHashSet<Integer>(); 
			TransAdd[] transThreadsArray = new TransAdd[100]; 
			for (int j = 0; j < 100 ; j ++ ){
				transThreadsArray[i] = new TransAdd(trans, 1000); 
				transThreadsArray[i].start(); 
			}
			for (int j = 0;j < 100 ; j ++ ){
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
			System.out.println(backoffArray[i] + "\t" + failCount + "\t" + duration); 
		}
		
		System.out.println("done"); 
	}

}

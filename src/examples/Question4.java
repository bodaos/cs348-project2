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

		public TransAdd( CarlSTMHashSet<Integer> set){
			this.set =set; 
		}
		@Override
		public void run() {
			Random generator = new Random();
			for (int i = 0; i < Question3.elementN ; i++ ){
				Integer integer = generator.nextInt(); 
				set.add(integer); 
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long[] backoffArray = {0, 1, 10, 20, 30, 50, 60, 100, 200, 500, 1000, 2000, 5000, 10000,20000, 40000,  100000 }; 
		long[] durationArray  = new long[backoffArray.length];
		long startTime =System.currentTimeMillis();
		long duration; 
		for (int i = 0; i < backoffArray.length; i++){
			CarlSTM.SetBackoff(backoffArray[i]); 
			CarlSTMHashSet.SetCapacity(10); 
			startTime =System.currentTimeMillis();
			CarlSTMHashSet<Integer> trans = new CarlSTMHashSet<Integer>(); 
			TransAdd[] transThreadsArray = new TransAdd[100]; 
			for (int j = 0; j < 100 ; j ++ ){
				transThreadsArray[i] = new TransAdd(trans); 
				transThreadsArray[i].start(); 
			}
			for (int j = 0;j < 100 ; j ++ ){
				try {
					transThreadsArray[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.print("trans add interrupted"); 
					e.printStackTrace();
				} 
			}
			duration = System.currentTimeMillis() - startTime;
			durationArray[i] = duration; 
			System.out.println(duration); 
		}
		
		System.out.println("done"); 
	}

}

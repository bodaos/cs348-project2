package examples;

import java.util.Random;

import examples.Question3.TransAdd;

public class AccuracyTest {

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
			for (int i = 0; i < 100 ; i++ ){
				Integer integer = i*3; 
				set.add(integer); 
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CarlSTMHashSet.SetCapacity(100);
		CarlSTMHashSet<Integer> trans = new CarlSTMHashSet<Integer>(); 
		TransAdd[] transThreadsArray = new TransAdd[100]; 
		for (int i = 0; i < 100 ; i ++ ){
			transThreadsArray[i] = new TransAdd(trans); 
			transThreadsArray[i].start(); 
		}
		for (int i = 0; i < 100 ; i ++ ){
			try {
				transThreadsArray[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.print("trans add interrupted"); 
				e.printStackTrace();
			} 
		}
		for (int i = 0; i < 100 ; i ++ ){
			System.out.println(trans.contains((Integer) i)); 
		}
		
	}

}

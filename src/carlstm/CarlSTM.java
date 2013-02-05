package carlstm;

/**
 * This class coordinates transaction execution. You can execute a transaction
 * using {@link #execute}. For example:
 * 
 * <pre>
 * class MyTransaction implements Transaction&lt;Integer&gt; {
 * 	TxObject&lt;Integer&gt; x;
 * 
 * 	MyTransaction(TxObject&lt;Integer&gt; x) {
 * 		this.x = x;
 * 	}
 * 
 * 	public Integer run() throws NoActiveTransactionException,
 * 			TransactionAbortedException {
 * 		int value = x.read();
 * 		x.write(value + 1);
 * 		return value;
 * 	}
 * 
 * 	public static void main(String[] args) {
 * 		TxObject&lt;Integer&gt; x = new TxObject&lt;Integer&gt;(0);
 * 		int result = CarlSTM.execute(new MyTransaction(x));
 * 		System.out.println(result);
 * 	}
 * }
 * </pre>
 */


public class CarlSTM {
	/**
	 * backoff time
	 *
	 */
	long backoff = 1;
    @SuppressWarnings("rawtypes")
	public static final ThreadLocal<TxInfo> threadLocal =
            new ThreadLocal<TxInfo>() {
                @Override protected TxInfo initialValue() {
                    return new TxInfo() ;
            }
        };
	/**
	 * Execute a transaction and return its result. This method needs to
	 * repeatedly start, execute, and commit the transaction until it
	 * successfully commits.
	 * 
	 * @param <T> return type of the transaction
	 * @param tx transaction to be executed
	 * @return result of the transaction
	 */
	public static <T> T execute(Transaction<T> tx) {
		long wait = 1;
		while(true){
		try {
			threadLocal.get().start();
			T result =  tx.run();
			threadLocal.get().commit();
			return result;
		} catch (NoActiveTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//Clears all the existing updates and re-run the transaction;
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("sleep interrupted");
				return null;
			}
			threadLocal.set(new TxInfo<T>());
			wait = wait*2;
		}
		}
	}
}

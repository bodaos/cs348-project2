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
		// TODO implement me
		
		try {
			threadLocal.get().start();
			T result =  tx.run();
			if(threadLocal.get().commit()){
				return result;
			}
			threadLocal.remove();
			return execute(tx);
		} catch (NoActiveTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			threadLocal.remove();
			//execute(tx);
			e.printStackTrace();
			return null;
		}
	}
}

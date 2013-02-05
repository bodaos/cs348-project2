package carlstm;

public class Update<T> {

	/**
	 * Class to hold the object, old value, and new value
	 */
	public TxObject<T> object;
	public T newValue;
	public T oldValue;
	//Constructor of Updates
	public  Update(TxObject<T> object, T oldValue, T newValue){
		this.object = object;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

}

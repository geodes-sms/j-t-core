package tcore;

/**
 * Class implementing the concept of pairs for the VF2 Algorithm
 *
 * @author Sebastien EHouan
 * @param <K> 
 * @param <V> 
 * @since 2020-04-25
 */

public class Pair <K, V> {
	private K key;
	private V value;
	
	/**
	 * @param key
	 * @param value
	 */
	public Pair(K key, V value){
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Get key.
	 * 
	 * @return
	 */
	public K getKey() {
		return key;
	}
	
	/**
	 * Set key.
	 * 
	 * @param key
	 */
	public void setKey(K key) {
		this.key = key;
	}
	
	/**
	 * Get value.
	 * 
	 * @return
	 */
	public V getValue() {
		return value;
	}
	
	/**
	 * Set value.
	 * 
	 * @param value
	 */
	public void setValue(V value) {
		this.value = value;
	}
}
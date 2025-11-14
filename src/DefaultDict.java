import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultDict<K, V> extends HashMap<K, V> {

    Class<V> amal;
    public DefaultDict(Class amal) {
        this.amal = amal;
    }

    @Override
    public V get(Object key) {
        V returnValue = super.get(key);
        if (returnValue == null) {
            try {
                returnValue = amal.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.put((K)key, returnValue);
        }
        return returnValue;
    }
}
package org.jkiss.dbeaver.model.sourcecode.template;


import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extension of the KeyValue reader to make it an Iterable.
 * This allows for the KV String to be streamed
 */
public class KeyValueReaderIterable extends KeyValueReader implements Iterable<Map.Entry<String, String>> {
	private char keyTag='=';
    /**
     * Default constructor
     * @param l line
     */

    public KeyValueReaderIterable(String l) {
        super(l);
    }
    
    public KeyValueReaderIterable(String l,char tag) {
        this(l);
        this.keyTag=tag;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return new Iterator<Map.Entry<String, String>>() {
            @Override
            public boolean hasNext() {
                return available();
            }

            @Override
            public Map.Entry<String, String> next() {
                return nextEntry();
            }
        };
    }

    private String getKey() { 
        if (is('\"')) {
            getc();
        }
        mark();
        skipUntil(keyTag);
        return getMarkedSegment();
    }


    private String getValue() {
    	System.out.println("开始VALUE，index="+this.getIndex());
    	if(is(keyTag))
    	{
    		 getc();
    	}
    	skipEmpty(); 
    	if(is('"'))
    	{ 
	        skipTo('"');
	        mark();
	        skipUntil('"');
	        return getMarkedSegment();
    	}else {
 	        mark();
 	        System.out.println("值开始：index="+getIndex());
 	       skipEmptyUntil(',');
 	        return getMarkedSegment();
    	}
    }

    private Map.Entry<String, String> nextEntry() {
        return new AbstractMap.SimpleEntry<>(getKey().trim(), getValue());
    }

    /**
     * Stream a KVString
     * @param data kv data string
     * @return {@link Stream} of {@link Map.Entry}
     */
    public static Stream<Map.Entry<String, String>> stream(final String data) {
        return StreamSupport.stream(new KeyValueReaderIterable(data).spliterator(), true);
    }
    public static Stream<Map.Entry<String, String>> stream(final String data,final char tag) {
    	return StreamSupport.stream(new KeyValueReaderIterable(data,tag).spliterator(), true);
    }
}

package org.jkiss.dbeaver.model.sourcecode.template;


/**
 * Reader for a KeyValue string
 * The format should be a key=\"value\" and spaces between kv's
 */
class KeyValueReader {

    private final String line;
    private int idx;
    private int mark;

    /**
     * Constructor
     * @param l data
     */
    public KeyValueReader(String l) {
        this.line = l;
    }

    /**
     * Mark for later reference
     */
    public void mark() {
        this.mark = this.idx;
    }

    /**
     * Copy from mark until current position
     * @return data
     */
    public String getMarkedSegment() {
        correctState(this.mark <= this.idx, "mark is greater than this.idx");
        return this.line.substring(this.mark, this.idx);
    }

    /**
     * Read current character and advance to next position
     * @return char value
     */
    public int getc() {
        correctState(this.idx + 1 < this.line.length(), "Attempt to read past end");
        return this.line.charAt(this.idx++);
    }

    /**
     * Can read next
     * @return true when available
     */
    public boolean available() {
        return idx < (line.length() - 1);
    }


    /**
     * Compare char at current position
     * @param c compare with
     * @return boolean
     */
    public boolean is(char c) {
        return idx < line.length() && this.line.charAt(this.idx) == c;
    }
    
    public boolean isSpace()
    {
    	if(idx<line.length())
    	{
    		int c=this.line.charAt(this.idx);
//    		System.out.println("isSpace ="+(32==c?"空字符":"不是空字符")+",index="+this.idx);
    		return   32 == c;
    	}
    	return false;
    }

    /**
     * compare with previous read character
     * @param c char to check
     * @return boolean
     */
    private boolean was(char c) {
        correctState(this.idx >= 1, "Reading before beginning of data");
        return this.line.charAt(this.idx - 1) == c;
    }


    /**
     * Skip to search char, index is set past this char
     * @param searchChar char to search
     */
    public void skipTo(char searchChar) {
        while (!is(searchChar) || was('\\')) {
            getc();
        }
        getc();
    }
    
    public void skipEmpty() {
        while (isSpace() ) {
            getc();
        }
    }

    /**
     * Skip until search char, current position is char
     * @param searchChar char
     */
    public void skipUntil(char searchChar) {
        while (!is(searchChar) || was('\\')) {
            getc();
        }
    }
    
    public void skipEmptyUntil(char searchChar) {
        while ( was('\\') || !(isSpace() || is(searchChar) )) {
          int c=  getc();
//          System.out.println("skipEmptyUntil:"+c+", index="+this.idx);
        }
    }

    /**
     * Short cut for asserts, when expression is false exception is thrown
     * @param expression false to throw
     * @param message message
     */
    private static void correctState(boolean expression, String message) {
        if (expression) {
            return;
        }
        throw new IllegalStateException(message);
    }
    
    protected int getIndex()
    {
    	return this.idx;
    }
}
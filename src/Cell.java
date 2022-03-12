/**
 * Cells represents in Conway's game of life, contains value, represents its status,
 * and generation number that the duration of this cell
 * @author Chuyu Liu
 */
public class Cell {
    private int value, genNum;

    /**
     * create a empty cell with 0 initial values.
     */
    public Cell(){
        value = 0;
        genNum = 0;
    }

    /**
     * set cell with given generation number.
     * @param gen generation number assigned to this cell
     */
    public void setValue(int gen) {
        if (gen == 0) {
        	died();
        } else {
        	value = 1;
        	genNum = gen;
        }
    }

    /**
     * get cell's status
     * @return 1 if the cell is alive; 0 otherwise
     */
    public int getValue() {
        return value;
    }

    /**
     * increment the cell's generation number
     */
    public void increment() {
    	value = 1;
        genNum++;
    }

    /**
     * get cell's generation number
     * @return cell's generation number
     */
    public int getGenNum() {
        return genNum;
    }

    /**
     * set this cell to died
     */
    public void died(){
    	value = 0;
        genNum = 0;
    }

    /**
     * decrement the cell's generation number
     */
    public void decrement() {
    	if (genNum <= 1) {
    		died();
    	} else {
        	genNum--;
    	}
    }
}

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This is Conway's game of life, which is cellular automaton that models the evolution of a colony of some organisms.
 * @author Chuyu Liu
 */
public class GameOfLife {
    private String inputFileName, outputFileName;
    private int stepNumber, row, column, curGen;
    private BufferedReader input;
    private Cell[][] squareCells;

    /**
     * create an instance of the game, with either three command line arguments or user inputs from console, and run it
     * (fist one is input file name,
     * second one is output file name or a path ended with '/',
     * and third one is number of steps)
     * @param args command line arguments
     */
    public static void main(String[] args){
        String inputFileName = "", outputFileName = "";
        int stepNumber = 0;

        if (args.length == 0){
            // if user doesn't put three information in command line arguments, use Scanner to get user inputs.
            Scanner sc = new Scanner(System.in);
            System.out.println("Input file name:");
            inputFileName = sc.nextLine();
            System.out.println("Output file name:");
            outputFileName = sc.nextLine();
            System.out.println("Number of steps:");
            try {
                stepNumber = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e){
                System.out.println("invalid step number");
                System.exit(1);
            } finally {
                sc.close();
            }
        } else {
            // get inputs from command-line argument
            try {
                inputFileName = args[0];
                outputFileName = args[1];
                stepNumber = Integer.parseInt(args[2]);
            } catch (NumberFormatException e){
                System.out.println("invalid step number");
                System.exit(1);
            } catch (IndexOutOfBoundsException e){
                System.out.println("less inputs");
                System.exit(2);
            }
        }

        if (outputFileName.endsWith("/")){ outputFileName += "Generation"; }    // if user passes a path,
                                                                                // then add a file name to it

        new GameOfLife(inputFileName, outputFileName, stepNumber).run();    // run the game of life

    }

    /**
     * to set all elements a game need into their places, set the first generation as generation 0,
     * and extract data from the file the user input
     * @param inputFileName input file name
     * @param outputFileName output file name (if end
     * @param stepNumber current generation number
     */
    public GameOfLife(String inputFileName, String outputFileName, int stepNumber){
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.stepNumber = stepNumber;
        curGen = 0;
        extractData();
    }

    /**
     * create a game from the given file
     * @param input input file name with path
     */
    public GameOfLife(BufferedReader input){
        this.input = input;
        curGen = 0;
        extractData();
    }

    /**
     * create a game with given row and column
     * @param row the row of the board
     * @param col the column of the board
     */
    public GameOfLife(int row, int col){
        this.row = row;
        this.column = col;
        curGen = 0;
        squareCells = new Cell[row][col];
        for (int i = 0; i < row; i ++) {
        	for (int j = 0; j < col; j ++) {
        		squareCells[i][j] = new Cell();
        	}
        }
    }

    /**
     * create a game with given cells, generation number, and output file name
     * @param squareCells the cells the board has
     * @param curGen the current generation number
     * @param filename the output file name with path
     */
    public GameOfLife(Cell[][] squareCells, int curGen, String filename) {
    	this.squareCells = squareCells;
    	this.row = squareCells.length;
    	this.column = squareCells[0].length;
    	this.curGen = curGen;
    	this.outputFileName = filename;
    }

    /**
     * run a game of life
     */
    public void run(){
        for (int i = 0; i < stepNumber; i++) {
            nextGeneration();
            output();
        }
    }

    /**
     * compute the result of the next generation
     */
    public void nextGeneration(){
        curGen ++;
        Cell[][] next = new Cell[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
            	next[i][j] = new Cell();
            	next[i][j].setValue(squareCells[i][j].getGenNum());
                int neighbors = squareCells[(i + row - 1)%row][(j + column - 1)%column].getValue() +
                                squareCells[(i + row - 1)%row][j].getValue() +
                                squareCells[(i + row - 1)%row][(j + column + 1)%column].getValue() +
                                squareCells[i][(j + column - 1)%column].getValue() +
                                squareCells[i][(j + column + 1)%column].getValue() +
                                squareCells[(i + row + 1)%row][(j + column - 1)%column].getValue() +
                                squareCells[(i + row + 1)%row][j].getValue() +
                                squareCells[(i + row + 1)%row][(j + column + 1)%column].getValue();
                if (squareCells[i][j].getValue() == 1){
                    if (neighbors < 2 || neighbors > 3){
                        next[i][j].died();
                    } else {
                        next[i][j].increment();
                    }
                } else {
                    if (neighbors == 3){
                        next[i][j].increment();
                    } else {
                        next[i][j].died();
                    }
                }
            }
        }
        squareCells = next;
    }

    /**
     * create a file with given output file name and append tick number, and write the output grid in it
     */
    public void output(){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outputFileName + curGen + ".txt"));
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    out.write(squareCells[i][j].getValue() + (j != column - 1 ? ", " : "\n"));
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("cannot write into the output file");
            System.exit(3);
        }
    }

    /**
     * create a file with given output file name, and write the output grid in it
     */
    public void outputNoPrint(){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outputFileName));
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    out.write(squareCells[i][j].getValue() + (j != column - 1 ? ", " : "\n"));
                }
            }
            out.close();
        } catch (IOException ignored) { }
    }

    /**
     * to read a seed file
     */
    private void extractData(){
        try {
        	if (input == null) { input = new BufferedReader(new FileReader(inputFileName)); }
            String size = input.readLine();
            row = Integer.parseInt(size.substring(0, size.indexOf(",")));   // to read the row number
            column = Integer.parseInt(size.substring(size.indexOf(" ") + 1));   //// to read the column number
            if (row < 0 || column < 0){
                System.out.println("row or column cannot be negative");
                System.exit(6);
            }
        } catch (IOException e) {
            System.out.println("cannot read input file");
            System.exit(3);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            System.out.println("cannot convert row or column");
            System.exit(6);
        } catch (NullPointerException e){
            System.out.println("seed file is empty");
            System.exit(8);
        }
        fillDataFromFile();
    }

    /**
     * create grid from given input file
     */
    public void fillDataFromFile(){
        squareCells = new Cell[row][column];
        for (int i = 0; i < row; i++) { //to read data of each cell
            try {
                String[] cells = input.readLine().split(", ");
                if (cells.length != column){
                    System.out.println("grid dimensions does not match user specified grid dimensions");
                    System.exit(5);
                }
                for (int j = 0; j < column; j++) {
                	squareCells[i][j] = new Cell();
                    squareCells[i][j].setValue(Integer.parseInt(cells[j]));
                    if (squareCells[i][j].getValue() != 0 & squareCells[i][j].getValue() != 1){
                        System.out.println("cell's state should be 0 or 1 only");
                        System.exit(4);
                    }
                }
            } catch (IOException e) {
                System.out.println("cannot read input file");
                System.exit(3);
            } catch (NumberFormatException e) {
                System.out.println("cell's state is not a number or wrong format for cells");
                System.exit(4);
            } catch (NullPointerException e){
                System.out.println("cells' states are empty");
                System.exit(7);
            }
        }
        try {
            if (input.read() != -1){
                System.out.println("grid dimensions does not match user specified grid dimensions");
                System.exit(5);
            }
        } catch (IOException ignored) { }
    }

    /**
     * get cell at [row][col]
     * @param row row of target cell located
     * @param col col of target cell located
     * @return cell at [row][col]
     */
    public Cell getCell(int row, int col) { return squareCells[row][col]; }

    /**
     * get number of rows
     * @return number of rows
     */
    public int getRow() { return row; }

    /**
     * get number of columns
     * @return number of columns
     */
    public int getCol() { return column; }

    /**
     * get whole cells grid
     * @return 2d array contains cells
     */
    public Cell[][] getCells() { return squareCells; }

    /**
     * set cells grid with given input cells and specify tick number
     * @param cells cells to be sat
     * @param gen generation number to be sat
     */
    public void setCells(Cell[][] cells, int gen) { squareCells = cells; curGen = gen; }

    /**
     * get current tick number
     * @return current tick number
     */
    public int getCurGen() { return curGen; }

    /**
     * get total number of alive cells with given cells grid
     * @param squareCells cells to be count
     * @return number of alive cells on given grid
     */
    public static int getCurAlive(Cell[][] squareCells) { return Arrays.stream(squareCells).flatMapToInt(o -> Arrays.stream(o).mapToInt(Cell::getValue)).sum(); }

    /**
     * get total number of died cells with given cells grid
     * @param squareCells cells to be count
     * @return number of died cells on given grid
     */
    public static int getCurDied(Cell[][] squareCells) { return squareCells.length * squareCells[0].length - getCurAlive(squareCells); }
}

package tk.xhuoffice.sessbilinfo.ui;

import java.util.Arrays;


public class Terminal {
    
    // variables with default 80x24
    public int lns = 24; // lines
    public int cols = 80; // columns
    public String[] screen = new String[24]; // virtual screen
    
    // <init> without arg
    public Terminal() {
        try {
            int[] size = Size.getSize();
            this.lns = size[1];
            this.cols = size[0];
            this.screen = new String[size[1]];
        } catch(java.io.IOException e) {
            // failed to get Terminal size
        }
        Arrays.fill(this.screen,null);
    }
    
    // <init> with lns
    public Terminal(int lns) {
        if(lns>0) {
            this.lns = lns;
            this.screen = new String[lns];
            Arrays.fill(this.screen,null);
        } else {
            throw new IllegalArgumentException("Terminal lines cannot be "+lns);
        }
    }
    
    // <init> with lns & cols
    public Terminal(int lns, int cols) {
        if(lns>0&&cols>0) {
            this.lns = lns;
            this.cols = cols;
            this.screen = new String[lns];
            Arrays.fill(this.screen,null);
        } else {
            throw new IllegalArgumentException("Terminal size cannot be "+cols+"x"+lns);
        }
    }
    
    // get a String for clear a line
    public String emptyLine() {
        char[] line = new char[this.cols];
        Arrays.fill(line,' ');
        return new String(line);
    }
    
    // set text of a line
    public void setLine(int ln, String text) {
        this.screen[ln-1] = text;
        System.out.printf("\033[%d;0f%s%n",ln,text);
    }
    
    // get text of a line
    public String getLine(int ln) {
        return this.screen[ln-1];
    }
    
    // clear a line
    public void clearLine(int ln) {
        this.screen[ln-1] = null;
        System.out.printf("\033[%d;0f%s%n",ln,emptyLine());
    }
    
    // clear virtual screen and Terminal
    public void clear() {
        // clear virtual screen
        Arrays.fill(this.screen,null);
        // clear Terminal
        String emptyLine = emptyLine(); // get empty line
        System.out.print("\033[1;0f"); // 移动光标至第1行第0列
        for(int i = 0;  i < this.lns; i++) { // clear lines
            System.out.println(emptyLine);
        }
    }
    
    // update virtual Terminal size
    public void updateSize() {
        int[] size = Size.get();
        this.lns = size[1];
        this.cols = size[0];
    }
    
    // update virtual Terminal size with lns
    public void updateSize(int lns) {
        this.lns = lns;
    }
    
    // update virtual Terminal size with lns & cols
    public void updateSize(int lns, int cols) {
        this.lns = lns;
        this.cols = cols;
    }
    
    // draw virtual screen to Terminal directly
    public void draw() {
        System.out.print("\033[1;0f");
        for(String text : this.screen) {
            if(text!=null) {
                System.out.println(text);
            } else {
                System.out.println();
            }
        }
    }
    
    // redraw virtual screen and Terminal
    public void redraw() {
        updateSize();
        redraw(this.lns,this.cols);
    }
    
    // redraw with lns
    public void redraw(int lns) {
        updateSize();
        redraw(lns,this.cols);
    }
    
    // redraw with lns & cols
    public void redraw(int lns, int cols) {
        // create a new virtual screen
        updateSize(lns,cols);
        String[] screen= new String[this.lns];
        Arrays.fill(screen,null);
        // fill new virtual screen
        if(screen.length>=this.screen.length) {
            for(int i = 0; i < this.screen.length; i++) {
                screen[i] = this.screen[i];
            }
        } else {
            int newScreenIndex = screen.length - 1;
            for(int i = this.screen.length - 1; i > -1; i--) {
                screen[newScreenIndex--] = this.screen[i];
                if(newScreenIndex<0) {
                    break;
                }
            }
        }
        // clear
        clear();
        // draw
        this.screen = screen;
        draw();
    }
    
    // get index of first empty(null) line
    public int getEmptyLineIndex() {
        int index = 0;
        while(index<this.screen.length) {
            if(this.screen[index]==null) {
                return index;
            }
            index++;
        }
        // could not find empty(null) line
        return -1;
    }
    
    // add text to first empty(null) line in virtual screen & Terminal
    public synchronized void addLine(String text) {
        // get index of empty(null) line
        int index = getEmptyLineIndex();
        // verify index
        if(index>-1) { // normal add
            // set & print empty(null) line to text
            setLine(index+1,text);
        } else { // no empty(null) line
            // create new virtual screen
            String[] screen = new String[this.lns];
            Arrays.fill(screen,null);
            // put old screen lines into new screen
            for(int i = 1; i < this.screen.length; i++) {
                screen[i-1] = this.screen[i];
            }
            // set text to last line of screen
            screen[screen.length-1] = text;
            // set new screen to this.screen
            this.screen = screen;
            // redraw
            redraw(this.lns,this.cols);
        }
    }
     
}

import java.io.*;
import java.util.*;
class Tag {
    public final static int
    EOF = -1, NUM = 256, ID = 257, RELOP = 258, ASSIGN = 259, TO = 260, COND = 261, OPTION = 262,
    DO = 263, ELSE = 264, WHILE = 265, BEGIN = 266, END = 267, PRINT = 268, READ = 269, OR = 270,
    AND = 271;
}

class Token {
    public final int tag;
    public Token(int t) {
        tag = t;
    }
    public String toString() {
        return "<" + tag + ">";
    }
    public static final Token
    not = new Token('!'),
    lpt = new Token('('),
    rpt = new Token(')'),
    lpq = new Token('['),
    rpq = new Token(']'),
    lpg = new Token('{'),
    rpg = new Token('}'),
    plus = new Token('+'),
    minus = new Token('-'),
    mult = new Token('*'),
    div = new Token('/'),
    semicolon = new Token(';'),
    comma = new Token(',');
}

class Word extends Token {
    public String lexeme = "";
    public Word(int tag, String s) {
        super(tag);
        lexeme = s;
    }
    public String toString() {
        return "<" + tag + "," + lexeme + ">";
    }
    public static final Word
    or = new Word(Tag.OR, "||"),
    and = new Word(Tag.AND, "&&"),
    lt = new Word(Tag.RELOP, "<"),
    gt = new Word(Tag.RELOP, ">"),
    eq = new Word(Tag.RELOP, "=="),
    le = new Word(Tag.RELOP, "<="),
    ne = new Word(Tag.RELOP, "<>"),
    ge = new Word(Tag.RELOP, ">=");
}

class NumberTok extends Token {
    public final int num;
    public NumberTok(int tag, int number) {
        super(Tag.NUM);
        num = number;
    }
    public String toString() {
        return "<" + tag + "," + num + ">";
    }
}
public class Lexer {
    public static int line = 1;
    private char peek = ' ';
    Hashtable words = new Hashtable();
    void reserve (Word w) {
        words.put(w.lexeme, w);
    }
    private void readch (BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; //Errore
        }
    }
    public Lexer() {
        reserve (new Word(Tag.ASSIGN, "assign"));
        reserve (new Word(Tag.TO, "to"));
        reserve (new Word(Tag.COND, "conditional"));
        reserve (new Word(Tag.OPTION, "option"));
        reserve (new Word(Tag.DO, "do"));
        reserve (new Word(Tag.ELSE, "else"));
        reserve (new Word(Tag.WHILE, "while"));
        reserve (new Word(Tag.BEGIN, "begin"));
        reserve (new Word(Tag.END, "end"));
        reserve (new Word(Tag.PRINT, "print"));
        reserve (new Word(Tag.READ, "read"));
    }

    public Token lexical_scan(BufferedReader br) throws IOException {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if(peek == '\n') line++;
            readch(br);
        }
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            //gestire i casi di ( ) [ ] { } + - * / ; , ...//
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '[':
                peek = ' ';
                return Token.lpq;
            case ']':
                peek = ' ';
                return Token.rpq;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                readch(br);
                if (peek == '/') {
                    peek = ' ';
                    try {
                        br.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return lexical_scan(br);
                } else if(peek == '*') {
                    readch(br);
                    while(peek != (char) -1) {
                        if (peek == '*') {
                            readch(br);
                            if (peek == '/') {
                                peek = ' ';
                                return lexical_scan(br);
                            }
                        }
                        else readch(br);
                    }
                    return null;
                } else {
                    return Token.div;
                }
            case ';':
                peek = ' ';
                return Token.semicolon;
            case ',':
                peek = ' ';
                return Token.comma;
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Carattere erroneo" + " dopo & :" + peek);
                    return null;
                }
                //gestire i casi di || < > <= >= == <> ...//
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Carattere erroneo" + " dopo | :" + peek);
                    return null;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Carattere erroneo" + " dopo | :" + peek);
                    return null;
                }
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else {
                    peek = ' ';
                    return Word.lt;
                }
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    peek = ' ';
                    return Word.gt;
                }
            case (char) -1:
                return new Token(Tag.EOF);
            default:
                if (Character.isLetter(peek) || peek == '_') {
                    //gestire il caso degli identificatori e delle parole chiave//
                    String b = "";
                    boolean state = true;
                    while (Character.isLetterOrDigit(peek) || peek == '_') {
                        b += peek;
                        if (state) {
                            if (peek != '_') {
                                state = false;
                            }
                        }
                        readch(br);
                    }
                    if (!state) {
                        Word w = (Word) words.get(b);
                        if (w != null) return w;
                        w = new Word(Tag.ID, b);
                        words.put(b, w);
                        return w;
                    } else {
                        return null;
                    }
                } else if (Character.isDigit(peek)) {
                    // gestire il caso dei numeri //
                    int v = 0;
                    int num;
                    do {
                        v = 10 * v + Character.digit(peek, 10);
                        readch(br);
                        num = v;
                    } while (Character.isDigit(peek));
                    return new NumberTok(v, num);
                } else {
                    System.err.println("Carattere erroneo: " + peek);
                    return null;
                }
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./hello";  //il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
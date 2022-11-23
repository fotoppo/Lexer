import java.io.*;
public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    public Valutatore(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }
    void move() {
// come in Esercizio 3.1
        try {
            look = lex.lexical_scan(pbr);
            System.out.println("token = " + look);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    void error(String s) {
// come in Esercizio 3.1
        throw new Error("near line " + lex.line + ": " + s);
    }
    void match(int t) {
// come in Esercizio 3.1
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }
    public void start() {
        int expr_val;
// ... completare ...
        expr_val = expr();
        match(Tag.EOF);
        System.out.println(expr_val);
// ... completare ...
    }
    private int expr() {
        int term_val, exprp_val;
// ... completare ...
        term_val = term();
        exprp_val = exprp(term_val);
// ... completare ...
        return exprp_val;
    }
    private int exprp(int exprp_i) {
        int term_val, exprp_val = exprp_i;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
// ... completare ...
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            case ')', Tag.EOF:
                break;
            default:
                error("Sintax error on exprp");
        }
        return exprp_val;
    }
    private int term() {
// ... completare ...
        int fact_val, termp_val;
        fact_val = fact();
        termp_val = termp(fact_val);
        return termp_val;
    }
    private int termp(int termp_i) {
// ... completare ...
        int termp_val = 1,fact_val;
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i/fact_val);
                break;
            case ')', Tag.EOF, '+', '-':
                termp_val = termp_i;
                break;
            default:
                error("Sintax error on termp");
        }
        return termp_val;
    }
    private int fact() {
// ... completare ...
        int NUM_value = 0;
        switch (look.tag) {
            case '(':
                match('(');
                NUM_value = expr();
                match(')');
                break;
            case Tag.NUM:
                NUM_value = ((NumberTok) look).num;
                match(Tag.NUM);
                break;
            default:
                error("Sintax error on fact");
        }
        return NUM_value;
    }
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./hello"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
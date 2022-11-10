import java.io.*;
public class NewParser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    public NewParser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }
    void move()  {
        try {
            look = lex.lexical_scan(pbr);
            System.out.println("token = " + look);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }
    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }
    public void prog() {
        statlist();
        match(Tag.EOF);
    }
    public void statlist() {
                stat();
                statlistp();

    }
    public void statlistp() {
        if(look.tag == ';') {
            match(';');
            stat();
            statlistp();
        }
    }
    public void stat() {
        switch (look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('[');
                exprlist();
                match(']');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('[');
                idlist();
                match(']');
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
                break;
            case Tag.COND:
                match(Tag.COND);
                match('[');
                optlist();
                match(']');
                if(look.tag == Tag.END) {
                    match(Tag.END);
                } else {
                    match(Tag.ELSE);
                    stat();
                    match(Tag.END);
                }
                break;
            case '{':
                match('{');
                statlist();
                match('}');
                break;
            default:
                error("Sintax error in stat()");
        }
    }
    public void idlist() {
                match(Tag.ID);
                idlistp();

    }
    public void idlistp() {
        if(look.tag == ',') {
            match(',');
            match(Tag.ID);
            idlistp();
        }
    }
    public void optlist() {

                optitem();
                optlistp();
    }
    public void optlistp() {
        if(look.tag == Tag.OPTION) {
            optitem();
            optlistp();
        }


    }
    public void optitem() {
        switch (look.tag) {
            case Tag.OPTION:
                match(Tag.OPTION);
                match('(');
                bexpr();
                match(')');
                match(Tag.DO);
                stat();
                break;
            default:
                error("Sintax error in optitem()");
        }
    }
    public void bexpr() {
        switch (look.tag) {
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
            default:
                error("Sintax error in bexpr()");
        }
    }
    public void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                break;
            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("Sintax error in expr()");
        }
    }
    public void exprlist() {
                expr();
                exprlistp();

    }
    public void exprlistp() {
        if(look.tag == ',') {
            match(',');
            expr();
            exprlistp();
        }
    }
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./hello"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            NewParser parser = new NewParser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
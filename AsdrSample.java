/**
 * @author Anderson Sprenger, Gabriel Zurawski
 */

import java.io.*;
import java.lang.annotation.ElementType;

public class AsdrSample {

  private static final int BASE_TOKEN_NUM = 301;
  
  public static final int IDENT  = 301;
  public static final int NUM 	 = 302;
  public static final int WHILE  = 303;
  public static final int IF	 = 304;
  public static final int FI	 = 305;
  public static final int ELSE = 306; 
  public static final int INT = 307;            // Adicionado pro trabalho             
  public static final int DOUBLE = 308;         // Adicionado pro trabalho 
  public static final int BOOLEAN = 309;        // Adicionado pro trabalho 
  public static final int FUNC = 310;           // Adicionado pro trabalho
  public static final int VOID = 311;           // Adicionado pro trabalho

    public static final String tokenList[] = 
      {"IDENT",
		 "NUM", 
		 "WHILE", 
		 "IF", 
		 "FI",
		 "ELSE", 
       "INT",              // Adicionado pro trabalho
       "DOUBLE",           // Adicionado pro trabalho
       "BOOLEAN",          // Adicionado pro trabalho
       "FUNC",             // Adicionado pro trabalho
       "VOID"              // Adicionado pro trabalho
       };
                                      
  /* referencia ao objeto Scanner gerado pelo JFLEX */
  private Yylex lexer;

  public ParserVal yylval;

  private static int laToken;
  private boolean debug;

  
  /* construtor da classe */
  public AsdrSample (Reader r) {
      lexer = new Yylex (r, this);
  }

  private void Prog() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN || laToken == '{' ) {
         if (debug) System.out.println("Prog --> ListaDeclVar Bloco");
         ListaDecl();
      }
      else 
        yyerror("esperado int, double, boolean, ou '{'");
   }

   private void ListaDecl() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN || laToken == FUNC) {
         if (debug) System.out.println("ListaDecl --> Decl ListaDecl");
         Decl();
         ListaDecl();
      } else {
         if (debug) System.out.println("ListaDecl --> * vazio * " + laToken);
      }
   }

   private void Decl() {
      // Tipo ListaIdent ';'  
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         if (debug) System.out.println("Decl --> Tipo ListaIdent ';'");
         Tipo();
         ListaIdent();
         verifica(';');
      }
      else if (laToken == FUNC) {
         if (debug) System.out.println("Decl --> FUNC TipoOuVoid IDENT '(' FormalPar ')' '{' ListaDeclVarLocal ListaCmd '}'");
         verifica(FUNC);
         TipoOuVoid();
         verifica(IDENT);
         verifica('(');
         FormalPar();
         verifica('(');
         verifica('{');
         ListaDeclVarLocal();
         ListaCmd();
      }
      else 
         yyerror("esperado Decl --> ...");
   }

   private void ListaDeclVarLocal() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         if (debug) System.out.println("ListaDeclVarLocal --> Tipo ListaIdent ';' ListaDeclVarLocal");
         Tipo();
         ListaIdent();
         verifica(';');
         ListaDeclVarLocal();
      } else { // vazio
         if (debug) System.out.println("ListaDeclVarLocal --> * vazio *");
      }
   }

   private void Tipo() {
      if (laToken == INT) {
         if (debug) System.out.println("Tipo --> int");
         verifica(INT);
      }

      else if (laToken == DOUBLE) {
         if (debug) System.out.println("Tipo --> double");
         verifica(DOUBLE);
      }

      else if (laToken == BOOLEAN) {
         if (debug) System.out.println("Tipo --> boolean");
         verifica(BOOLEAN);
      }

      else {
         yyerror("esperado Tipo --> [INT|DOUBLE|BOOLEAN]");
      }
   }

   private void ListaIdent() {
      if (debug) System.out.println("ListaIdent --> IDENT ListaIdent'");
      verifica(IDENT);
      ListaIdent_();
   }

   private void ListaIdent_() {
      // ListaIdent' --> , ListaIdent | /* vazio */
      if (laToken == IDENT) {
         verifica(',');
         ListaIdent();
      } else { // vazio
         if (debug) System.out.println("ListaIdent' --> * vazio *");
      }
   }

   private void TipoOuVoid() {
      // TipoOuVoid --> Tipo | VOID
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         if (debug) System.out.println("TipoOuVoid --> Tipo");
         Tipo();
      } else if (laToken == VOID) {
         if (debug) System.out.println("TipoOuVoid --> VOID");
         verifica(VOID);
      } else {
         yyerror("esperado Tipo --> ...");
      }
   }

   // FormalPar --> ParamList |  /* vazio */\
   private void FormalPar() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         ParamList();
      }
      else { // vazio
         if (debug) System.out.println("FormalPar --> *vazio*");
      }
   }

   
   private void ParamList() {
      if (debug) System.out.println("ParamList --> Tipo IDENT ParamListTail");
      Tipo();
      verifica(IDENT);
      ParamListTail();
   }

   // ParamListTail --> , ParamList | /* vazio */

   private void ParamListTail() {
      if (laToken == ',') {
         verifica(',');
         ParamList();
      } else { // vazio
         if (debug) System.out.println("ParamListTail --> *vazio*");
      }
   }

   private void Bloco() {
      if (debug) System.out.println("Bloco --> '{' ListaCmd '}'");
      verifica('{');
      ListaCmd();
      verifica('}');
   }

   // ListaCmd --> Cmd ListaCmd | /* vazio */
   private void ListaCmd() {
      if (laToken == '{' || laToken == WHILE || laToken == IDENT || laToken == IF) {
         if (debug) System.out.println("ListaCmd --> Cmd ListaCmd");
         Cmd();
         ListaCmd();
      } else {
         if (debug) System.out.println("ListaCmd -> *vazio*");
      }
   }

   private void Cmd() {
      if (laToken == '{') {
         if (debug) System.out.println("Cmd -> Bloco");
         Bloco();
      }

      else if(laToken == WHILE) {
         if (debug) System.out.println("Cmd -> WHILE '(' E ')' Cmd");
         verifica(WHILE);
         verifica('(');
         E();
         verifica(')');
         Cmd();
      }

      else if (laToken == IDENT) {
         if (debug) System.out.println("Cmd -> IDENT '=' E ';'");
         verifica(IDENT);
         verifica('=');
         E();
         verifica(';');
      }

      else if (laToken == IF) {
         if (debug) System.out.println("Cmd -> IF '(' E ')' Cmd RestoIf");
         verifica(IF);
         verifica('(');
         E();
         verifica(')');
         Cmd();
         RestoIf();
      } else {
         yyerror("Esperado Cmd -> ...");
      }
   }

   private void RestoIf() {
      if (laToken == ELSE) {
         if (debug) System.out.println("RestoIf --> ELSE Cmd");
         verifica(ELSE);
         Cmd();
      } else {
         if (debug) System.out.println("RestoIf -> *vazio*");
      }
   }

   // E --> T E'
   private void E() {
      if (debug) System.out.println("E --> T E'");
      T();
      E_();
   }


   // E' --> '+' T E' | '-' T E' | /* vazio */
   private void E_() {
      if (laToken == '+') {
         if (debug) System.out.println("E' --> '+' T E'");
         verifica('+');
         T();
         E_();
      }

      else if (laToken == '-') {
         if (debug) System.out.println("E' --> '-' T E'");
         verifica('-');
         T();
         E_();
      } else {
         if (debug) System.out.println("E' --> *vazio*");
      }
   }

   // T --> F T'
   private void T() {
      if (debug) System.out.println("T --> F T'");
      F();
      T_();
   }

   // T' --> '*' F T' | '/' F T' | /* vazio */
   private void T_() {
      if (laToken == '*') {
         if (debug) System.out.println("T' --> '*' F T'");
         verifica('*');
         F();
         T_();
      }

      else if (laToken == '/') {
         if (debug) System.out.println("T' --> '/' F T'");
         verifica('/');
         F();
         T_();
   } 
      
      else { // vazio
         if (debug) System.out.println("T --> *vazio*");
      }
   }

   // F --> IDENT | NUM | '(' E ')'

   private void F() {
      if (laToken == IDENT) {
         if (debug) System.out.println("F --> IDENT");
         verifica(IDENT);
      }

      else if (laToken == NUM) {
         if (debug) System.out.println("F --> NUM");
         verifica(NUM);
      }
      else if (laToken == '(') {
         if (debug) System.out.println("F --> '(' E ')'");
         verifica('(');
         E();
         verifica(')');
      } else {
         yyerror("Esperado F -> ...");
      }
   }

  private void verifica(int expected) {
      if (laToken == expected)
         laToken = this.yylex();
      else {
         String expStr, laStr;       

		expStr = ((expected < BASE_TOKEN_NUM )
                ? ""+(char)expected
			     : tokenList[expected-BASE_TOKEN_NUM]);
         
		laStr = ((laToken < BASE_TOKEN_NUM )
                ? Character.toString(laToken)
                : tokenList[laToken-BASE_TOKEN_NUM]);

          yyerror( "esperado token: " + expStr +
                   " na entrada: " + laStr);
     }
   }

   /* metodo de acesso ao Scanner gerado pelo JFLEX */
   private int yylex() {
       int retVal = -1;
       try {
           yylval = new ParserVal(0); //zera o valor do token
           retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
       } catch (IOException e) {
           System.err.println("IO Error:" + e);
          }
       return retVal; //retorna o token para o Parser 
   }

  /* metodo de manipulacao de erros de sintaxe */
  public void yyerror (String error) {
     System.err.println("Erro: " + error);
     System.err.println("Entrada rejeitada");
     System.out.println("\n\nFalhou!!!");
     System.exit(1);
     
  }

  public void setDebug(boolean trace) {
      debug = true;
  }


  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param args   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String[] args) {
     AsdrSample parser = null;
     try {
         if (args.length == 0)
            parser = new AsdrSample(new InputStreamReader(System.in));
         else 
            parser = new  AsdrSample( new java.io.FileReader(args[0]));

          parser.setDebug(false);
          laToken = parser.yylex();          

          parser.Prog();
     
          if (laToken== Yylex.YYEOF)
             System.out.println("\n\nSucesso!");
          else     
             System.out.println("\n\nFalhou - esperado EOF.");               

        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+args[0]+"\"");
        }
//        catch (java.io.IOException e) {
//          System.out.println("IO error scanning file \""+args[0]+"\"");
//          System.out.println(e);
//        }
//        catch (Exception e) {
//          System.out.println("Unexpected exception:");
//          e.printStackTrace();
//      }
    
  }
  
}



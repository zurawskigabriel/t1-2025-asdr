
/*
  Prog -->  ListaDecl

  ListaDecl -->  DeclVar  ListaDecl
              |  DeclFun  ListaDecl
              |  (*vazio*)

  DeclVar --> Tipo ListaIdent ';' DeclVar
            | (*vazio*)

  Tipo --> int 
         | double 
         | boolean

  ListaIdent --> IDENT , ListaIdent  
               | IDENT      

  DeclFun --> FUNC tipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun
            | (*vazio*)

  TipoOuVoid --> Tipo | VOID

  FormalPar -> paramList | (*vazio*)

  paramList --> Tipo IDENT , ParamList
              | Tipo IDENT 

  Bloco --> { ListaCmd }

  ListaCmd --> Cmd ListaCmd
    |    (*vazio*)

  Cmd --> Bloco
      | while ( E ) Cmd
      | IDENT = E ;
      | if ( E ) Cmd RestoIf

  RestoIf -> else Cmd
        |    (*vazio*)

  E --> E + T
      | E - T
      | T

  T --> T * F
      | T / F
      | F    
      
  F -->  IDENT
      | NUM
      | ( E )

*/


import java.io.*;

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

  /***** Gramática original 
  Prog -->  Bloco

  Bloco --> { Cmd }

  Cmd --> Bloco
      | while ( E ) Cmd
      | ident = E ;
      | if ( E ) Cmd 
      | if ( E ) Cmd else Cmd 

  E --> IDENT
   | NUM
   | ( E )
***/  

  /***** Gramática 'fatorada' 
  Prog -->  Bloco

  Bloco --> { Cmd }

  Cmd --> Bloco
      | while ( E ) Cmd
      | ident = E ;
      | if ( E ) Cmd RestoIf   // 'fatorada à esquerda'
      
   RestoIf --> else Cmd 
            | 

  E --> E + T
      | T

  T --> IDENT
   | NUM
   | ( E )
***/ 

  private void Prog() {
      if (laToken == INT || laToken == DOUBLE || laTOken == BOOLEAN || '{' ) {
         if (debug) System.out.println("Prog --> ListaDeclVar Bloco");
         ListaDeclVar();
         Bloco();
      }
      else 
        yyerror("esperado int, double, boolean, ou '{'");
   }

   private ListaDeclVar(){
      if (laToken == INT || laToken  == DOUBLE || laToken == BOOLEAN){
         DeclVar();
         ListaDeclVar();
      }
   }

   private void DeclVar(){
      Tipo();
      ListaIdent();
      verifica(';');
   }

  private void Bloco() {
      if (debug) System.out.println("Bloco --> { Cmd }");
      //if (laToken == '{') {
         verifica('{');
         Cmd();
         verifica('}');
      //}
  }

  private void Cmd() {
      if (laToken == '{') {
         if (debug) System.out.println("Cmd --> Bloco");
         Bloco();
	   }    
      else if (laToken == WHILE) {
         if (debug) System.out.println("Cmd --> WHILE ( E ) Cmd");
         verifica(WHILE);    // laToken = this.yylex(); 
  		   verifica('(');
  		   E();
         verifica(')');
         Cmd();
	   }
      else if (laToken == IDENT ) {
         if (debug) System.out.println("Cmd --> IDENT = E ;");
            verifica(IDENT);  
            verifica('='); 
            E();
		      verifica(';');
	   }
    else if (laToken == IF) {
         if (debug) System.out.println("Cmd --> if (E) Cmd RestoIF");
         verifica(IF);
         verifica('(');
  		   E();
         verifica(')');
         Cmd();
         RestoIF();
	   }
 	else yyerror("Esperado {, if, while ou identificador");
   }


   private void RestoIF() {
       if (laToken == ELSE) {
         if (debug) System.out.println("RestoIF --> else Cmd FI ");
         verifica(ELSE);
         Cmd();
         
    
	   } else {
         if (debug) System.out.println("RestoIF -->  (*vazio*)  ");
         // aceitar como vazio  <-- my way
         // ou testar o follow de RestoIF
         }
     }     

   private void E() {
         if (laToken == IDENT || laToken == NUM || laToken == '(') {
          if (debug) System.out.println("E --> T R");
         T();
         R();
         }
         else yyerror("Esperado operando (, identificador ou numero");
      }
      

   private void R() {
      if (laToken == '+') {
         if (debug) System.out.println("R --> + T R");
         verifica('+');
         T();
         R();
      }
      else   if (laToken == '-') {
         if (debug) System.out.println("R --> - T R");
         verifica('-');
         T();
         R();
      }
      else {
         if (debug) System.out.println("R -->  (*vazio*)  ");
         // aceitar como vazio  <-- my way
         // ou testar o follow de R
         }
   }  




  private void T() {
      if (laToken == IDENT) {
         if (debug) System.out.println("T --> IDENT");
         verifica(IDENT);
	   }
      else if (laToken == NUM) {
         if (debug) System.out.println("T --> NUM");
         verifica(NUM);
	   }
      else if (laToken == '(') {
         if (debug) System.out.println("T --> ( E )");
         verifica('(');
         E();        
		 verifica(')');
	   }
 	else yyerror("Esperado operando (, identificador ou numero");
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



/**
 * @author Anderson Sprenger, Gabriel Zurawski
 */
%%

%{
  private AsdrSample yyparser;

  public Yylex(java.io.Reader r, AsdrSample yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


%} 

%integer
%line
%char

WHITE_SPACE_CHAR=[\n\r\ \t\b\012]

%%

"$TRACE_ON"   { yyparser.setDebug(true); }
"$TRACE_OFF"  { yyparser.setDebug(false); }

"while"	 	{ return AsdrSample.WHILE; }
"if"		{ return AsdrSample.IF; }
"else"		{ return AsdrSample.ELSE; }
"func" { return AsdrSample.FUNC; }
"void" { return AsdrSample.VOID; }

"int" { return AsdrSample.INT; }
"double" { return AsdrSample.DOUBLE; }
"boolean" { return AsdrSample.BOOLEAN; }

[:jletter:][:jletterdigit:]* { return AsdrSample.IDENT; }  

[0-9]+ \. [0-9]+ { return AsdrSample.NUM; }
[0-9]+ 	{ return AsdrSample.NUM; }

"{" |
"}" |
";" |
"(" |
")" |
"+" |
"-" |
"=" |
"," |
"*" |
"/"    	{ return yytext().charAt(0); } 


{WHITE_SPACE_CHAR}+ { }

. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }

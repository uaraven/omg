grammar OmSql;

@header {
   package net.ninjacat.omg.sql.parser;
}

filter
 :  sql_stmt EOF
 ;

sql_stmt
 : select ';'*
 ;

operator
 : '<'
 | '<='
 | '>'
 | '>='
 | '='
 | '!='
 | '<>'
 | '~='
 | K_IN
 | K_MATCH
 ;

list
 : '(' literal_value (',' literal_value)* ')'
 ;

expr
 : field_name operator literal_value #condition
 | expr K_AND expr #andExpr
 | expr K_OR expr #orExpr
 | K_NOT expr #notExpr
 | '(' expr ')' # parensExpr
 | field_name K_IN list # inExpr
 ;

result_field
 : '*'
 | field_name ( K_AS? column_alias )?
 ;

where
 : K_WHERE expr
 ;

select
 : K_SELECT result_field ( ',' result_field )*
   ( K_FROM source_name )?
   where?
 ;

signed_number
 : ( '+' | '-' )? NUMERIC_LITERAL
 ;

literal_value
 : signed_number
 | STRING_LITERAL
 | K_NULL
 ;

column_alias
 : IDENTIFIER
 | STRING_LITERAL
 ;

source_name
 : IDENTIFIER
 ;

field_name
 : IDENTIFIER
 ;

K_AND : A N D;
K_AS : A S;
K_BETWEEN : B E T W E E N;
K_FROM: F R O M;
K_IN: I N;
K_MATCH: M A T C H;
K_NOT: N O T;
K_NULL: N U L L;
K_OR: O R;
K_REGEX: R E G E X;
K_SELECT: S E L E C T;
K_WHERE : W H E R E;

IDENTIFIER
 : [a-zA-Z_$] [a-zA-Z_0-9$]*
 ;

NUMERIC_LITERAL
 : DIGIT+ ( '.' DIGIT* )? ( E [-+]? DIGIT+ )?
 | '.' DIGIT+ ( E [-+]? DIGIT+ )?
 ;

STRING_LITERAL
 : '\'' ( ~'\'' | '\'\'' )* '\''
 | '"' ( ~'"' | '""' )* '"'

 ;

SINGLE_LINE_COMMENT
 : '--' ~[\r\n]* -> channel(HIDDEN)
 ;

MULTILINE_COMMENT
 : '/*' .*? ( '*/' | EOF ) -> channel(HIDDEN)
 ;

SPACES
 : [ \u000B\t\r\n] -> channel(HIDDEN)
 ;

UNEXPECTED_CHAR
 : .
 ;

fragment DIGIT : [0-9];

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

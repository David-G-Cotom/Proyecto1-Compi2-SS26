grammar Zetariano;

archivoZ: claseZ EOF;

claseZ: KW_PUBLIC KW_CLASS ID LLA_ABRE miembroClase* LLA_CIERRA;

miembroClase: constructorZ
            | metodoZ
            | atributoZ
            ;

atributoZ: tipoZ declaradorAtributoZ (COMA declaradorAtributoZ)* PUNTOYCOMA;

declaradorAtributoZ: ID (ASIGNAR (literalCompuestoZ | expresionZ))?;

constructorZ: KW_PUBLIC ID PAR_ABRE listaParametrosZ? PAR_CIERRA LLA_ABRE sentenciaZ* LLA_CIERRA;

metodoZ: KW_PUBLIC tipoRetornoZ ID PAR_ABRE listaParametrosZ? PAR_CIERRA LLA_ABRE sentenciaZ* LLA_CIERRA;

listaParametrosZ: parametroZ (COMA parametroZ)*;

parametroZ: tipoZ ID;

tipoZ: (tipoPrimitivoZ | ID) (COR_ABRE COR_CIERRA)*;

tipoPrimitivoZ: KW_INT | KW_DOUBLE | KW_CHAR | KW_BOOLEAN | KW_STRING;

tipoRetornoZ: tipoZ | KW_VOID;

expresionZ: NEGACION expresionZ                                                             # negacionExprZ
            | MENOS expresionZ                                                              # menosUnarioExprZ
            | expresionZ op=(MULT | DIV | MOD) expresionZ                                   # multDivModExprZ
            | expresionZ op=(MAS | MENOS) expresionZ                                        # sumaRestaExprZ
            | expresionZ op=(MENORQUE | MAYORQUE | MENOREIGUAL | MAYORIGUAL) expresionZ     # relacionalExprZ
            | expresionZ op=(IGUALIGUAL | DISTINTO) expresionZ                              # igualdadExprZ
            | expresionZ Y_LOGICO expresionZ                                                # andExprZ
            | expresionZ O_LOGICO expresionZ                                                # orExprZ
            | expresionZ INTERROGACION expresionZ DOSPUNTOS expresionZ                      # ternarioExprZ
            | primarioZ                                                                     # primarioExprZ
            ;

primarioZ: literalZ
        | nuevoArregloZ
        | nuevoObjetoZ
        | llamadaReadlnZ
        | KW_NULL
        | accesoVariableZ
        | PAR_ABRE expresionZ PAR_CIERRA
        ;

literalZ: ENTERO_LIT | FLOTANTE_LIT | CARACTER_LIT | CADENA_LIT | KW_TRUE | KW_FALSE;

nuevoArregloZ: KW_NEW (tipoPrimitivoZ | ID) (COR_ABRE expresionZ COR_CIERRA)+;

nuevoObjetoZ: KW_NEW ID PAR_ABRE listaArgumentosZ? PAR_CIERRA;

llamadaReadlnZ: KW_READLN PAR_ABRE PAR_CIERRA;

listaArgumentosZ: expresionZ (COMA expresionZ)*;

accesoVariableZ: segmentoAccesoZ (PUNTO segmentoAccesoZ)*;

segmentoAccesoZ: ID (PAR_ABRE listaArgumentosZ? PAR_CIERRA)? (COR_ABRE expresionZ COR_CIERRA)*;

incrementoDecrementoZ: accesoVariableZ op=(INCREMENTO | DECREMENTO) PUNTOYCOMA;

asignacionZ: accesoVariableZ op=(ASIGNAR | MAS_IGUAL | MENOS_IGUAL | MULT_IGUAL | DIV_IGUAL | MOD_IGUAL) expresionZ PUNTOYCOMA;

sentenciaImprimirZ: (KW_PRINTLN | KW_PRINT) PAR_ABRE expresionZ PAR_CIERRA PUNTOYCOMA;

sentenciaLeerZ: llamadaReadlnZ PUNTOYCOMA;

declaracionVariableZ: tipoZ declaradorVariableZ (COMA declaradorVariableZ)* PUNTOYCOMA;

declaradorVariableZ: ID (ASIGNAR (literalCompuestoZ | expresionZ))?;

literalCompuestoZ: LLA_ABRE expresionZ (COMA expresionZ)* LLA_CIERRA;

sentenciaReturnZ: KW_RETURN expresionZ? PUNTOYCOMA;

sentenciaLlamadaZ: accesoVariableZ PUNTOYCOMA;

sentenciaBreakZ: KW_BREAK PUNTOYCOMA;

sentenciaContinueZ: KW_CONTINUE PUNTOYCOMA;

bloqueZ: LLA_ABRE sentenciaZ* LLA_CIERRA
        | sentenciaZ
        ;

sentenciaSiZ: KW_IF PAR_ABRE expresionZ PAR_CIERRA bloqueZ
                (KW_ELSE KW_IF PAR_ABRE expresionZ PAR_CIERRA bloqueZ)*
                (KW_ELSE bloqueZ)?
                ;

sentenciaSwitchZ: KW_SWITCH PAR_ABRE expresionZ PAR_CIERRA LLA_ABRE casoSwitchZ* defaultSwitchZ? LLA_CIERRA;

casoSwitchZ: KW_CASE valorCasoZ DOSPUNTOS sentenciaZ*;

defaultSwitchZ: KW_DEFAULT DOSPUNTOS sentenciaZ*;

valorCasoZ: literalZ;

sentenciaForZ: KW_FOR PAR_ABRE forInitZ? PUNTOYCOMA expresionZ? PUNTOYCOMA forActualizacionZ? PAR_CIERRA bloqueZ;

forInitZ: tipoZ declaradorVariableZ (COMA declaradorVariableZ)*
        | accesoVariableZ ASIGNAR expresionZ
        ;

forActualizacionZ: accesoVariableZ op=(INCREMENTO | DECREMENTO)
                | accesoVariableZ op=(ASIGNAR | MAS_IGUAL | MENOS_IGUAL | MULT_IGUAL | DIV_IGUAL | MOD_IGUAL) expresionZ
                ;

sentenciaWhileZ: KW_WHILE PAR_ABRE expresionZ PAR_CIERRA bloqueZ;

sentenciaDoWhileZ: KW_DO bloqueZ KW_WHILE PAR_ABRE expresionZ PAR_CIERRA PUNTOYCOMA;

sentenciaZ
    : declaracionVariableZ
    | asignacionZ
    | incrementoDecrementoZ
    | sentenciaImprimirZ
    | sentenciaLeerZ
    | sentenciaSiZ
    | sentenciaSwitchZ
    | sentenciaForZ
    | sentenciaWhileZ
    | sentenciaDoWhileZ
    | sentenciaBreakZ
    | sentenciaContinueZ
    | sentenciaReturnZ
    | sentenciaLlamadaZ
    ;

// ---------- Tokens léxicos ----------
KW_PUBLIC : 'public' ;
KW_CLASS  : 'class' ;
KW_VOID   : 'void' ;
KW_INT     : 'int' ;
KW_DOUBLE  : 'double' ;
KW_CHAR    : 'char' ;
KW_BOOLEAN : 'boolean' ;
KW_STRING  : 'String' ;
KW_NEW    : 'new' ;
KW_NULL   : 'null' ;
KW_TRUE   : 'true' ;
KW_FALSE  : 'false' ;
KW_PRINTLN : 'println' ;
KW_PRINT   : 'print' ;
KW_READLN  : 'readln' ;

KW_IF       : 'if' ;
KW_ELSE     : 'else' ;
KW_SWITCH   : 'switch' ;
KW_CASE     : 'case' ;
KW_DEFAULT  : 'default' ;
KW_FOR      : 'for' ;
KW_WHILE    : 'while' ;
KW_DO       : 'do' ;
KW_BREAK    : 'break' ;
KW_CONTINUE : 'continue' ;
KW_RETURN   : 'return' ;

LLA_ABRE   : '{' ;
LLA_CIERRA : '}' ;
PAR_ABRE   : '(' ;
PAR_CIERRA : ')' ;
COR_ABRE   : '[' ;
COR_CIERRA : ']' ;
PUNTOYCOMA : ';' ;
COMA       : ',' ;
PUNTO      : '.' ;
DOSPUNTOS  : ':' ;
INTERROGACION : '?' ;

ASIGNAR     : '=' ;
MAS_IGUAL   : '+=' ;
MENOS_IGUAL : '-=' ;
MULT_IGUAL  : '*=' ;
DIV_IGUAL   : '/=' ;
MOD_IGUAL   : '%=' ;

MAS  : '+' ;
MENOS: '-' ;
MULT : '*' ;
DIV  : '/' ;
MOD  : '%' ;

IGUALIGUAL  : '==' ;
DISTINTO    : '!=' ;
MENOREIGUAL : '<=' ;
MAYORIGUAL  : '>=' ;
MENORQUE    : '<' ;
MAYORQUE    : '>' ;
Y_LOGICO    : '&&' ;
O_LOGICO    : '||' ;
NEGACION    : '!' ;
INCREMENTO  : '++' ;
DECREMENTO  : '--' ;

ID : [a-zA-Z_][a-zA-Z_0-9]* ;
ENTERO_LIT   : [0-9]+ ;
FLOTANTE_LIT : [0-9]+ '.' [0-9]+ ;
CARACTER_LIT : '\'' . '\'' ;
CADENA_LIT   : '"' ~["\r\n]* '"' ;

COMENTARIO_LINEA  : '//' ~[\r\n]* -> channel(HIDDEN) ;
COMENTARIO_BLOQUE : '/*' .*? '*/' -> channel(HIDDEN) ;
WS                : [ \t\r\n]+ -> skip ;

grammar Y;

tokens { INDENT, DEDENT }

archivoY: seccionEstructuras? seccionFunciones EOF;

seccionEstructuras: SEC_ESTRUCTURAS NEWLINE estructura+;

seccionFunciones: SEC_FUNCIONES NEWLINE funcion+;

estructura: KW_ESTRUCTURA ID DOSPUNTOS NEWLINE INDENT campoEstructura+ DEDENT;

campoEstructura: tipoDato ID (COR_ABRE expresionConstante COR_CIERRA)? NEWLINE;

tipoDato: TIPO_ENTERO | TIPO_FLOTANTE | TIPO_CARACTER | TIPO_CADENA | TIPO_BOOL | ID;

expresionConstante: expresionConstante op=('*'|'/') expresionConstante      #multDivConstante
                    | expresionConstante op=('+'|'-') expresionConstante    #sumaRestaConstante
                    | ENTERO_LIT                                            #literalConstante
                    | PAR_ABRE expresionConstante PAR_CIERRA                #parentesisConstante
                    ;

funcion: KW_DEFINIR ID PAR_ABRE listaParametros? PAR_CIERRA (FLECHA tipoDato)? DOSPUNTOS NEWLINE bloqueSentencias;

bloqueSentencias: INDENT sentencia+ DEDENT;

listaParametros: parametro (COMA parametro)*;

parametro: COR_ABRE COR_CIERRA tipoDato ID
            | LLA_ABRE LLA_CIERRA ID ID
            | tipoDato ID;

sentencia: estructura
            | declaracionVariable
            | asignacion
            | incrementoDecremento
            | sentenciaLlamada
            | sentenciaImprimir
            | sentenciaLeer
            | sentenciaSi
            | sentenciaElegir
            | sentenciaPara
            | sentenciaMientras
            | sentenciaHacerMientras
            | sentenciaContinuar
            | sentenciaRomper
            | sentenciaRetornar;

sentenciaLlamada: llamadaFuncion NEWLINE;

sentenciaImprimir: KW_IMPRIMIR PAR_ABRE expresion PAR_CIERRA NEWLINE;

sentenciaLeer: llamadaLeer NEWLINE;

llamadaLeer: KW_LEER PAR_ABRE PAR_CIERRA;

sentenciaSi: KW_SI PAR_ABRE expresion PAR_CIERRA KW_ENTONCES DOSPUNTOS? NEWLINE bloqueSentencias
                (KW_SINO PAR_ABRE expresion PAR_CIERRA KW_ENTONCES DOSPUNTOS? NEWLINE bloqueSentencias)*
                (KW_CONTRARIO DOSPUNTOS? NEWLINE bloqueSentencias)?;

sentenciaElegir: KW_ELEGIR PAR_ABRE expresion PAR_CIERRA DOSPUNTOS NEWLINE INDENT casoElegir+ siempreElegir? DEDENT;

casoElegir: KW_CASO valorCaso DOSPUNTOS NEWLINE bloqueSentencias;

valorCaso: literal | literalCompuesto;

siempreElegir: KW_SIEMPRE DOSPUNTOS NEWLINE bloqueSentencias;

sentenciaPara: KW_PARA PAR_ABRE paraInit PUNTOCOMA expresion PUNTOCOMA paraActualizacion PAR_CIERRA DOSPUNTOS NEWLINE bloqueSentencias;

paraInit: tipoDato ID (ASIGNAR expresion)?
                | accesoVariable ASIGNAR expresion;

paraActualizacion: accesoVariable op=(INCREMENTO | DECREMENTO)
                | accesoVariable ASIGNAR expresion;

sentenciaMientras: KW_MIENTRAS PAR_ABRE expresion PAR_CIERRA KW_HACER DOSPUNTOS? NEWLINE bloqueSentencias;

sentenciaHacerMientras: KW_HACER DOSPUNTOS NEWLINE bloqueSentencias KW_MIENTRAS PAR_ABRE expresion PAR_CIERRA NEWLINE;

sentenciaContinuar: KW_CONTINUAR NEWLINE;

sentenciaRomper: KW_ROMPER NEWLINE;

sentenciaRetornar: KW_RETORNAR expresion? NEWLINE;

declaracionVariable: tipoDato ID (COR_ABRE expresionConstante COR_CIERRA)* (ASIGNAR (literalCompuesto | expresion))? NEWLINE;

literalCompuesto: LLA_ABRE expresion (COMA expresion)* LLA_CIERRA;

asignacion: accesoVariable ASIGNAR expresion NEWLINE;

incrementoDecremento: accesoVariable op=(INCREMENTO | DECREMENTO) NEWLINE;

accesoVariable: ID sufijoAcceso*;

sufijoAcceso: COR_ABRE expresion COR_CIERRA
            | PUNTO ID;

expresion: NEGACION expresion                                                       #negacionExpr
        | expresion op=(MULT | DIV) expresion                                       #multDivExpr
        | expresion op=(MAS | MENOS) expresion                                      #sumaRestaExpr
        | expresion op=(MENORQUE | MAYORQUE | MENORIGUAL | MAYORIGUAL) expresion    #relacionalExpr
        | expresion op=(IGUALIGUAL | DISTINTO) expresion                            #igualdadExpr
        | expresion Y_LOGICO expresion                                              #andExpr
        | expresion O_LOGICO expresion                                              #orExpr
        | primario                                                                  #primarioExpr
        ;

primario: literal
        | llamadaLeer
        | llamadaFuncion
        | accesoVariable
        | PAR_ABRE expresion PAR_CIERRA;

llamadaFuncion: ID PAR_ABRE listaArgumentos? PAR_CIERRA;

listaArgumentos: expresion (COMA expresion)*;

literal: ENTERO_LIT | FLOTANTE_LIT | CARACTER_LIT | CADENA_LIT | KW_VERDADERO | KW_FALSO;
    
SEC_ESTRUCTURAS: '%estructuras';
SEC_FUNCIONES: '%funciones';
KW_ESTRUCTURA: 'estructura';
KW_DEFINIR: 'definir';

TIPO_ENTERO: 'entero';
TIPO_FLOTANTE: 'flotante';
TIPO_CARACTER: 'caracter';
TIPO_CADENA: 'cadena';
TIPO_BOOL: 'bool';

KW_VERDADERO: 'verdadero';
KW_FALSO: 'falso';

KW_SI: 'si';
KW_ENTONCES: 'entonces';
KW_SINO: 'sino';
KW_CONTRARIO: 'contrario';
KW_ELEGIR: 'elegir';
KW_CASO: 'caso';
KW_SIEMPRE: 'siempre';
KW_PARA: 'para';
KW_MIENTRAS: 'mientras';
KW_HACER: 'hacer';
KW_CONTINUAR: 'continuar';
KW_ROMPER: 'romper';
KW_RETORNAR: 'retornar';

KW_IMPRIMIR: 'imprimir';
KW_LEER: 'leer';

DOSPUNTOS: ':';
FLECHA: '->';
PAR_ABRE: '(';
PAR_CIERRA: ')';
COR_ABRE: '[';
COR_CIERRA: ']';
LLA_ABRE: '{';
LLA_CIERRA: '}';
COMA: ',';
PUNTOCOMA: ';';

MAS: '+';
MENOS: '-';
MULT: '*';
DIV: '/';
IGUALIGUAL: '==';
DISTINTO: '!=';
MENORIGUAL: '<=';
MAYORIGUAL: '>=';
MENORQUE: '<';
MAYORQUE: '>';
Y_LOGICO: '&&';
O_LOGICO: '||';
NEGACION: '!';
INCREMENTO: '++';
DECREMENTO: '--';
ASIGNAR: '=';
PUNTO: '.';

ID: [a-zA-Z_][a-zA-Z_0-9]*;
ENTERO_LIT: [0-9]+;
FLOTANTE_LIT: [0-9]+ '.' [0-9]+;
CARACTER_LIT: '\'' . '\'' ;
CADENA_LIT: '"' ~["\r\n]* '"';

NEWLINE: ('\r'? '\n' | '\r') [ \t]*;

WS_ENTRE_TOKENS: [ \t]+ -> channel(HIDDEN);

COMENTARIO_LINEA: '//' ~[\r\n]* -> channel(HIDDEN);
COMENTARIO_BLOQUE: '/*' .*? '*/' -> channel(HIDDEN);

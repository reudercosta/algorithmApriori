/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
/**
 *
 * @author Reuder Cerqueira
 */
public class Foo {
    public static void main( String[] args ) {
        try {
            File inFile = new File( "base200.txt" );
            FileReader fr = new FileReader( inFile );
            long tamanhoTotal = Files.size( inFile.toPath() );
            int quantidade = 20;
            long tamanhoPorArquivo = tamanhoTotal / quantidade;
            long tamanhoUltimoArquivo = tamanhoPorArquivo + (tamanhoTotal % quantidade);
            long maximo;
            for ( int i = 0; i < quantidade; i++ ) {
                if ( i == quantidade - 1 ) {
                    maximo = tamanhoUltimoArquivo;
                } else {
                    maximo = tamanhoPorArquivo;
                }
                File arquivoAtual = new File( "base" + i + ".txt" );
                FileWriter fw = new FileWriter( arquivoAtual );
                for ( int j = 0; j < maximo; j++ ) {
                    fw.write( fr.read() );
                }
                fw.close();
            }
            fr.close();
        } catch ( IOException exc ) {
            exc.printStackTrace();
        }
    }
}
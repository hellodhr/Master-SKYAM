/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AS;

import DS.DgStd;
import DS.DgStd1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Diego
 */
public class AplicacaoASDS {

    File arqAlg = new File("./src/csv/algoritmos.csv");

    public static void main(String[] args) throws IOException, InterruptedException {
        AnnStd objAS = new AnnStd();
        DgStd1 objDS = new DgStd1();

        File[] resultados = new File[23];
        for (int i = 0; i < resultados.length; ++i) {
            int index = i + 1;
            resultados[i] = new File("./src/csv/resultsDedup", "resultado" + index + ".csv");
        }

        //Padronização dos arquivos
        File[] resultadosPadr = new File[23];

        for (int i = 0; i < resultadosPadr.length; ++i) {
            resultadosPadr[i] = objAS.padronizaCsvFile(resultados[i]);
        }

        File gs = new File("./src/csv/datasets", "cd_gold.csv");

        objAS.setGs(gs);
        objAS.setTamBaseOrig(9763); //Necessário!
        objDS.setGs(gs);
        objDS.setTamBaseOrig(9763); //Necessário!

        long seed = 500;
//        int qtdAlg = 10; //n algoritmos
        int[] vQtdAlg = {10, 15, 20, 23};//, 25}; //Adicionado depois
        int qtdObservacoes = 1000;

        File algSort = new File("./src/csv/", "algoritmos.csv");

//        int sohParaTestar = 0;
        for (int qtdAlg : vQtdAlg) { //Adicionado depois

            System.out.println("Quantidade de algoritmos: " + qtdAlg);

            //Gerando observações através de seleção aleatória de n algoritmos de deduplicação
            for (int i = 1; i <= qtdObservacoes; i++) {

                ArrayList<Integer> listaAlg = geraOrdAlg(qtdAlg, seed);

                if (!buscaAlgoritmos(algSort, listaAlg)) {

                    gravaAlgoritmos(algSort, listaAlg);

                    objAS.setPermutacao(i);
                    objAS.setQtdAlg(qtdAlg);
                    objAS.limpaTudo();

                    objDS.setPermutacao(i);
                    objDS.setQtdAlg(qtdAlg);
                    objDS.limpaTudo(); //Acho que não devo limpar, mas sim salvar NAO_DA(número da iteração)
                    System.out.println("Iteração " + i);

                    int alg = 0;

                    for (int index : listaAlg) {

                        alg++;

                        objAS.comparaConjuntos(resultadosPadr[index]);

                        if (alg == listaAlg.size()) { //Gerar estatísticas só na última iteração
//                            System.out.println("último algoritmo: " + alg);
//                            System.out.println("Gerando estatísticas para a última iteração pela " + ++sohParaTestar + " vez!");
                            objDS.setGeraEst(true);
                        }

                        objDS.comparaConjuntos(resultadosPadr[index]);
                    }
                    
                } else {
                    i--;
                }
            seed++;
        }
        //Dar um jeito de excluir os arquivos "diverg" que não contém "_NEW", para poupar espaço em disco
        java.awt.Toolkit.getDefaultToolkit().beep();

    }

}

public int getTamAlg() throws IOException {

        int tamAlg = 0;

        LineNumberReader linhaLeitura1 = null;

        try {
            linhaLeitura1 = new LineNumberReader(new FileReader(arqAlg.getPath()));
            linhaLeitura1.skip(arqAlg.length());
            tamAlg = linhaLeitura1.getLineNumber();

        

} catch (FileNotFoundException ex) {
            Logger.getLogger(AnnStd.class
.getName()).log(Level.SEVERE, null, ex);
        

} catch (IOException ex) {
            Logger.getLogger(AnnStd.class
.getName()).log(Level.SEVERE, null, ex);
        } finally {
            linhaLeitura1.close();
        }

        return tamAlg;

    }

    //Gera ordem aleatória de algoritmos sem repetição dessa
    public static ArrayList<Integer> geraOrdAlg(int qtdAlg, long seed) { //Esse static precisa mesmo?

        ArrayList<Integer> aux = new ArrayList<Integer>();
        Random gerador = new Random(seed);

        while (aux.size() < qtdAlg) {

            int randomNum = gerador.nextInt(23);

            if (!aux.contains(randomNum)) {
                aux.add(randomNum);
            }

        }

        aux.sort(null);

//        for (Integer valor : aux) {
//            System.out.print(valor + " ");
//        }
//        System.out.println("");
        //Busca CSV
//        aux.clear();
//        }
        return aux;

    }

    //Grava uma lista de algoritmos em um arquivo dedicado a manter o histórico de algoritmos selecionados 
    //para compor a amostra para os experimentos
    public static void gravaAlgoritmos(File arqResult, ArrayList<Integer> lista) throws IOException {

        FileWriter escreveAlg = null;
        BufferedWriter bwAlg = null;

        for (Integer valor : lista) {
//            System.out.println("Oi");
            System.out.print(valor + " ");
        }
        System.out.println("");

        try {
            escreveAlg = new FileWriter(arqResult, true);
            bwAlg = new BufferedWriter(escreveAlg);

            bwAlg.write(lista + "\n");
//            for (Integer valor : lista) {
//                bwAlg.write(valor.toString() + ";");
//            }
//            bwAlg.write("\n");

        } catch (FileNotFoundException ex) {
            System.out.println("Não foi possível encontrar o arquivo " + arqResult.getName());
        

} catch (IOException ex) {
            Logger.getLogger(AnnStd.class
.getName()).log(Level.SEVERE, null, ex);
        } finally {
            bwAlg.flush();
            bwAlg.close();

        }
    }

    //Busca se a lista de algoritmos passados já existe no arquivo responsável por armazenar listas de algoritmos anteriormente gerados
    private static boolean buscaAlgoritmos(File busca, ArrayList<Integer> elemento) throws IOException, InterruptedException {

        if (!busca.exists()) {

            busca.createNewFile();
            new Thread().sleep(50);

        }

//O gabarito tem de estar sem aspas
        String Str;
        boolean existe = false;

        BufferedReader brGS = null;
        try {
            brGS = new BufferedReader(new FileReader(busca.getPath()));

            while ((Str = brGS.readLine()) != null) {

                if ((elemento.toString().equals(Str))) {
                    existe = true;
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Não foi possível encontrar o arquivo " + busca.getName() + " em buscaGabarito()");
        

} catch (IOException ex) {
            Logger.getLogger(AnnStd.class
.getName()).log(Level.SEVERE, null, ex);
        } finally {
            brGS.close();
        }

        return existe;
    }

}

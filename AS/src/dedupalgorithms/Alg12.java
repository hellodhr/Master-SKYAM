/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dedupalgorithms;

import dude.algorithm.Algorithm;
import dude.output.CSVOutput;
import dude.output.DuDeOutput;
import dude.output.statisticoutput.CSVStatisticOutput;
import dude.output.statisticoutput.SimpleStatisticOutput;
import dude.output.statisticoutput.StatisticOutput;
import dude.postprocessor.NaiveTransitiveClosureGenerator;
import dude.postprocessor.StatisticComponent;
import dude.similarityfunction.aggregators.Average;
import dude.similarityfunction.aggregators.Maximum;
import dude.similarityfunction.aggregators.Minimum;
import dude.similarityfunction.contentbased.impl.SoundExFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.JaroDistanceFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.LevenshteinDistanceFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.MongeElkanFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.SmithWatermanFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.SmithWatermanFunction;
import dude.util.GoldStandard;
import dude.util.data.DuDeObjectPair;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simmetrics.metrics.functions.AffineGap;

/**
 *
 * @author Diego
 */
public class Alg12 extends DedupAlg {

    FileWriter escreveResult;
    File estatisticasCSV;
    File estatisticasTXT;

    public Alg12(String baseDados1, String chavePrimaria, String gold, String goldId1, String goldId2, String result, int ordem) {
        super(baseDados1, chavePrimaria, gold, goldId1, goldId2, result);

        estatisticasCSV = new File("./src/csv/resultsDedup/estatisticas", "estatisticasDedup" + ordem + ".csv");
        estatisticasTXT = new File("./src/csv/resultsDedup/estatisticas", "estatisticasDedup" + ordem + ".txt");

        if (estatisticasTXT.exists() | estatisticasCSV.exists()) {
            System.out.println("Já existem resultados para esse algoritmo!");
            java.awt.Toolkit.getDefaultToolkit().beep();
            System.exit(0);
        }

        try {
            this.escreveResult = new FileWriter(new File("./src/csv/resultsDedup", "resultado" + ordem + ".csv"));

        } catch (IOException ex) {
            Logger.getLogger(Alg12.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    File resultado = new File("./src/csv/datasets", "resultado.csv");
    @Override
    public void executaDedupAlg() throws IOException {

        GoldStandard goldStandard = getGS();

        Algorithm algorithm = getAlg();
        algorithm.enableInMemoryProcessing();

        MongeElkanFunction similarityFunc = new MongeElkanFunction("title");
        SoundExFunction similarityFunc2 = new SoundExFunction("artist");
        LevenshteinDistanceFunction similarityFunc3 = new LevenshteinDistanceFunction("track01");
        LevenshteinDistanceFunction similarityFunc4 = new LevenshteinDistanceFunction("track02");

        Average avg = new Average();
        avg.add(similarityFunc3);
        avg.add(similarityFunc4);

//        DuDeOutput output = new JsonOutput(System.out);
        DuDeOutput output = new CSVOutput(escreveResult);

        StatisticComponent statistic = new StatisticComponent(goldStandard, algorithm);

        StatisticOutput statisticOutputCSV;
        StatisticOutput statisticOutputTXT;

        statisticOutputCSV = new CSVStatisticOutput(estatisticasCSV, statistic, ';');
        statisticOutputTXT = new SimpleStatisticOutput(estatisticasTXT, statistic);

//        statisticOutput = new SimpleStatisticOutput(System.out, statistic);
        statistic.setStartTime();

        NaiveTransitiveClosureGenerator fechoTrans = new NaiveTransitiveClosureGenerator();

        //Gerando o fecho transitivo
        for (DuDeObjectPair pair : algorithm) {

            final double similarity = similarityFunc.getSimilarity(pair);
            final double similarity2 = similarityFunc2.getSimilarity(pair);
            final double similarity3 = avg.getSimilarity(pair);

            if ((similarity >= 0.8) && (similarity2 >= 0.8) && (similarity3 >= 0.6)) {
                fechoTrans.add(pair);

            } else {
                statistic.addNonDuplicate(pair);
            }

        }

        for (DuDeObjectPair pair : fechoTrans) {

            statistic.addDuplicate(pair);
            output.write(pair);

        }

        statistic.setEndTime();

        statisticOutputCSV.writeStatistics();
        statisticOutputTXT.writeStatistics();

        algorithm.cleanUp();
        goldStandard.close();

    }

    public static void main(String[] args) {
        Alg12 obj1 = new Alg12("cd", "pk", "cd_gold", "disc1_id", "disc2_id", "cd_result", 12);
        try {
            obj1.executaDedupAlg();
        } catch (IOException ex) {
            Logger.getLogger(Alg12.class.getName()).log(Level.SEVERE, null, ex);
        }
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

}

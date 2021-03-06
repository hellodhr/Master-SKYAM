/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dedupalgorithms;

import dude.algorithm.Algorithm;
import dude.algorithm.SortingDuplicateDetection;
import dude.algorithm.duplicatedetection.NaiveDuplicateDetection;
import dude.algorithm.recordlinkage.NaiveRecordLinkage;
import dude.datasource.CSVSource;
import dude.output.CSVOutput;
import dude.output.DuDeOutput;
import dude.output.JsonOutput;
import dude.output.statisticoutput.CSVStatisticOutput;
import dude.output.statisticoutput.SimpleStatisticOutput;
import dude.output.statisticoutput.StatisticOutput;
import dude.postprocessor.StatisticComponent;
import dude.similarityfunction.contentbased.impl.simmetrics.LevenshteinDistanceFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.SimmetricsFunction;
import dude.util.GoldStandard;
import dude.util.data.DuDeObjectPair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Diego
 */
public class DedupAlg {

    private String result; //O modificador protected permite que as subclasses tenham acesso aos membros da superclasse
    private String baseDados1;
    private String baseDados2;
    private String chavePrimaria;
    private String gold;
    private String goldId1;
    private String goldId2;
    private String idBaseDados;
    private Algorithm algorithm;
    private CSVSource source1;
    private CSVSource source2;
    private GoldStandard goldStandard;
    
    public DedupAlg(String baseDados1, String chavePrimaria, String gold, String goldId1, String goldId2, String result) {
//    public DedupAlg(String baseDados1, String gold, String goldId1, String goldId2, String result) {

        this.result = baseDados1;
        this.baseDados1 = baseDados1;
        this.chavePrimaria = chavePrimaria;
        this.gold = gold;
        this.goldId1 = goldId1;
        this.goldId2 = goldId2;
        this.idBaseDados = idBaseDados;
        deduplication();
    }

    public DedupAlg(String baseDados1, String baseDados2, String chavePrimaria, String gold, String goldId1, String goldId2, String result) {
//    public DedupAlg(String baseDados1, String baseDados2, String gold, String goldId1, String goldId2, String result) {

        System.out.println("baseDados2= " + baseDados2);
        System.out.println("Entrei no 2");

        this.baseDados1 = baseDados1;
        this.baseDados2 = baseDados2;
//        this.chavePrimaria = chavePrimaria;
        this.gold = gold;
        this.goldId1 = goldId1;
        this.goldId2 = goldId2;
        this.idBaseDados = idBaseDados;
        this.result = baseDados1 + baseDados2;
        recordLinkage();
    }
    
    
    public void deduplication() {
        String literalGS = baseDados1;
        try {
            source1 = new CSVSource(baseDados1, new File("./src/csv/datasets", baseDados1 + ".csv"));
    
            //Vejamos se funcionam essas 2 linhas:
            source1.withQuoteCharacter('"');
            source1.withSeparatorCharacter(';');
            
            source1.enableHeader();
            
            source1.addIdAttributes(chavePrimaria);
            
            File file = new File("./src/csv/datasets", baseDados1 + ".csv");
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getPath());
            System.out.println(file.getName());
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DedupAlg.class.getName()).log(Level.SEVERE, null, ex);
        }

        algorithm = new NaiveDuplicateDetection();

        algorithm.addDataSource(source1);
        
        setGoldStandard(literalGS);

    }

    public void recordLinkage() {
        String literalGS = baseDados1 + baseDados2;
        try {

            source1 = new CSVSource(baseDados1, new File("./src/csv/datasets", baseDados1 + ".csv"));
            source2 = new CSVSource(baseDados2, new File("./src/csv/datasets", baseDados2 + ".csv"));

            source1.enableHeader();
            source2.enableHeader();
            
            File file = new File("./src/csv/datasets", baseDados1 + ".csv");
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getPath());
            System.out.println(file.getName());
            
            File file2 = new File("./src/csv/datasets", baseDados2 + ".csv");
            System.out.println(file2.getAbsolutePath());
            System.out.println(file2.getPath());
            System.out.println(file2.getName());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DedupAlg.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
                
        algorithm = new NaiveRecordLinkage();

        algorithm.addDataSource(source1);
        algorithm.addDataSource(source2);
        
        setGoldStandard(literalGS);

    }

    public void setGoldStandard(String literalGS) {
        CSVSource goldStandardSource = null;
        try {
            goldStandardSource = new CSVSource("goldstandard", new File("./src/csv/datasets", gold + ".csv"));
            goldStandardSource.enableHeader();
            
            File file = new File("./src/csv/datasets", gold + ".csv");
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getPath());
            System.out.println(file.getName());
            
            
            goldStandard = new GoldStandard(goldStandardSource);
            
            goldStandardSource.enableHeader();

            goldStandard.setSourceIdLiteral(literalGS);
            goldStandard.setFirstElementsObjectIdAttributes(goldId1);
            goldStandard.setSecondElementsObjectIdAttributes(goldId2);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DedupAlg.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Algorithm getAlg() {
        return algorithm;
    }
    
    public GoldStandard getGS(){
        return goldStandard;
    }
    
    public String getResult() {
        return result;
    }

    public String getGold() {
        return gold;
    }

    public String getGoldId1() {
        return goldId1;
    }

    public String getGoldId2() {
        return goldId2;
    }

    public String getIdBaseDados() {
        return idBaseDados;
    }
    
    

    public void executaDedupAlg() throws Exception {

    }
    
}

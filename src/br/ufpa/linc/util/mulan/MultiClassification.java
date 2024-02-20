package br.ufpa.linc.util.mulan;

/***********************************************************************

	This file is part of EvoImp: Multiple Imputation of Multi-label 
        Classification data with a genetic algorithm.

	Copyright (C) 2019-2024
	
        Antonio Jacob Jr. (antoniojunior@professor.uema.br)
        Fabrício Almeida (fabrycio30@hotmail.com)
        Adamo Santana (adamo-santana@fujielectric.com)
        Ewaldo Santana (ewaldoeder@gmail.com)
        Fábio Lobato (fabio.lobato@ufopa.edu.br)
        
        If you use any of the resources available here, to cite this work, 
        please use:
        Jacob Junior, A. F. L., do Carmo, F. A., de Santana, A. L., 
        Santana, E. E. C., & Lobato, F. M. F. (2024). EvoImp: Multiple 
        Imputation of Multi-label Classification data with a genetic algorithm.
        Plos one, 19(1), e0297147.
        
        
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

import br.ufpa.linc.util.arff.InformacoesArff;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class MultiClassification {
/**
 * Class MultiClassification
 * 
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */

    public static Double[] BR(InformacoesArff info, ArrayList<String> imputedData, int classIndex) {
                   
          Instances   data = BuildInsteces(info, imputedData);
             data.setClassIndex(classIndex);
              return evaluateBR(data,info.getnomeBase());
           
       
    }
    
    private static void writeData(Instances data) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("summary/tmp.arff"))) {
            writer.write(data.toString());
            writer.flush();
            writer.close();
        }
    }
    
    private static Double[] evaluateBR(Instances data, String name)
    {
        Double [] result = new Double[3];
        try {
            writeData(data);
        } catch (IOException ex) {
            Logger.getLogger(MultiClassification.class.getName()).log(Level.SEVERE, null, ex);
        }
        String arffFilename = "summary/tmp.arff";
        String xmlFilename = "bases-amp-xml-files/" + name.trim()+  ".xml";
        
        try {
            MultiLabelInstances dataset = new MultiLabelInstances(arffFilename, xmlFilename);
            
            BinaryRelevance model = new BinaryRelevance(new J48());
            
            Evaluator eval = new Evaluator();
            MultipleEvaluation results = eval.crossValidate(model, dataset, 10);
            
            double sum_hl = 0.0;
            double sum_em = 0.0;
            double sum_acc = 0.0;
            
            for(int i = 0; i < results.getEvaluations().size(); i++)
            {
                sum_hl += results.getEvaluations().get(i).getMeasures().get(0).getValue();//0
                sum_acc += results.getEvaluations().get(i).getMeasures().get(2).getValue(); //5
                sum_em += results.getEvaluations().get(i).getMeasures().get(i).getValue();//2
      System.out.println("HL ["+sum_hl+"] ACC ["+sum_acc+"] EM ["+sum_em+"]");
                
            }
          
            
            sum_hl /= results.getEvaluations().size();
            sum_em /= results.getEvaluations().size();
            sum_acc /= results.getEvaluations().size();
            
            result[0] = sum_hl;
            result[1] = sum_acc;
            result[2] = sum_em;
            
        } catch (InvalidDataFormatException ex) {
            Logger.getLogger(MultiClassification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MultiClassification.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
 
    public static Instances BuildInsteces(InformacoesArff info, ArrayList<String> data) {
        ArrayList<Attribute> fvWekaAttributes = new ArrayList<>(info.getTipoAtributo().size());
        for (int i = 0; i < info.getTipoAtributo().size(); i++) {
            if (!info.getTipoAtributo().get(i).toUpperCase().trim().equals("NUMERIC")
                    && !info.getTipoAtributo().get(i).toUpperCase().trim().equals("REAL")
                    && !info.getTipoAtributo().get(i).toUpperCase().trim().equals("INTEGER")
                   ) {
                String line = info.getTipoAtributo().get(i);
                line = line.replace("{", "");
                line = line.replace("}", "");
                String token[] = line.split(",");
                ArrayList<String> fvNominalVal = new ArrayList<>(token.length);
                for (int j = 0; j < token.length; j++) {
                    fvNominalVal.add(token[j].trim());
                }
                Attribute Attribute = new Attribute(info.getNomeAtributo().get(i), fvNominalVal);
                fvWekaAttributes.add(Attribute);
            } else {
                Attribute Attribute = new Attribute(info.getNomeAtributo().get(i));
                fvWekaAttributes.add(Attribute);
            }
        }
        Instances instance = new Instances(info.getnomeBase(), fvWekaAttributes, data.size());
        //==============================DenseInstance==================================
        for (int i = 0; i < data.size(); i++) {
            Instance iExample = new DenseInstance(info.getTipoAtributo().size());
         
            String token[] = data.get(i).split(",");
            for (int j = 0; j < info.getTipoAtributo().size(); j++) {
                switch (info.getTipoAtributo().get(j).toUpperCase().trim()) {
                    case "NUMERIC":
                    case "REAL":
                        if(token[j].trim().compareTo("<null>") == 0){
                            
                            iExample.setValue((Attribute) fvWekaAttributes.get(j), Double.NaN);
                            
                            
                            
                            System.out.println("real: "+ fvWekaAttributes.get(j)+ ", "+Double.NaN);
                            
                        
                        } else{
                        try{
                        
                        double real = Double.parseDouble(token[j].trim());
                        iExample.setValue((Attribute) fvWekaAttributes.get(j), real);
                        
                        }catch(NumberFormatException e)
                        {
                            System.out.println(data.get(i));
                            System.out.println("j="+j);
                            System.out.println("i="+i);
                            System.exit(1);
                        }
                        }
                        break;
                    case "INTEGER":
                        double real2 = 0;
                        try{
                        real2 = Double.parseDouble(token[j].trim());
                        }catch(NumberFormatException e)
                        {
                            System.out.println(token[j].trim());
                        }
                        int integer = (int) Math.round(real2);
                        iExample.setValue((Attribute) fvWekaAttributes.get(j), integer);
                        
                          System.out.println("inteiro: "+ fvWekaAttributes.get(j)+ ", "+integer);
                        
                        break;

                     
                    default:
                       
                          if (fvWekaAttributes.get(j).isNominal()) {
                              try {
                                  if ("<null>".equals(token[j])) {
                                       System.err.println("aqui NULL");
                                      token[j]="0";
                                  }
                                  iExample.setValue(fvWekaAttributes.get(j), token[j]);
                              } catch (Exception e) {
                                 System.err.println("ERRO AQUI: "+ fvWekaAttributes.get(j)+ ", "+token[j]);  
                                 e.getStackTrace();
                              }
                              
                          System.out.println("nominal: "+ fvWekaAttributes.get(j)+ ", "+token[j]);
                        
                          }
                           if ("<null>".equals(token[j])) {
                                       System.err.println("aqui NULL");
                                      token[j]="0";
                                  }
                          try {
                            iExample.setValue((Attribute) fvWekaAttributes.get(j), token[j]); 
                        } catch (Exception e) {
                        }
            
                     }
            }
            // add the instance
            instance.add(iExample);
        }
//        ====================end DenseInstance================================
        return instance;
        
    }    
}

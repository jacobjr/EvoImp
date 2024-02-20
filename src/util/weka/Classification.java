package util.weka;

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

/**
 * Class Classification
 * Contains classifiers using the WEKA library
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */

import util.arff.InformacoesArff;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import main.Properties;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class Classification {

    public static Double[] J48(InformacoesArff info, ArrayList<String> imputedData, int classIndex) {
        Instances data = BuildInsteces(info, imputedData);
        data.setClassIndex(classIndex);
        return evaluateJ48(data);
    }

    public static Double[] NB(InformacoesArff info, ArrayList<String> imputedData, int classIndex) {
        Instances data = BuildInsteces(info, imputedData);
        data.setClassIndex(classIndex);
        return evaluateNB(data);
    }
    
    public static Double[] J48(InformacoesArff info, int classIndex) {
        Instances data = BuildInsteces(info, info.getData());
        data.setClassIndex(classIndex);
        return evaluateJ48(data);
    }

    public static Double[] J48(FileReader completeData, int classIndex) {
        Instances data = null;
        try {
            data = new Instances(completeData);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        data.setClassIndex(classIndex);
        return evaluateJ48(data);
    }

    
    private static Double[] evaluateJ48(Instances data) {
        J48 j48 = new J48();

        int fold = 10;
        if (data.numInstances() < 10) {
            fold = data.numInstances();
        }

        //Classifier Evaluation
        Evaluation minhaEvaluation = null;
        try {
            minhaEvaluation = new Evaluation(data);
            minhaEvaluation.crossValidateModel(j48, data, fold, new Random(Properties.Weka_Seed));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        Double values[] = new Double[2];

        Double acuracia = minhaEvaluation.pctCorrect();
        values[0] = acuracia;
        Double rmse = minhaEvaluation.rootMeanSquaredError();
        values[1] = rmse;

        return values;
    }

    private static Double[] evaluateNB(Instances data) {
        NaiveBayes nb = new NaiveBayes();

        int fold = 10;
        if (data.numInstances() < 10) {
            fold = data.numInstances();
        }

        //Classifier Evaluation
        Evaluation minhaEvaluation = null;
        try {
            minhaEvaluation = new Evaluation(data);
            minhaEvaluation.crossValidateModel(nb, data, fold, new Random(Properties.Weka_Seed));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        Double values[] = new Double[2];

        Double acuracia = minhaEvaluation.pctCorrect();
        values[0] = acuracia;
        Double rmse = minhaEvaluation.rootMeanSquaredError();
        values[1] = rmse;

        return values;
    }
    
    private static Instances BuildInsteces(InformacoesArff info, ArrayList<String> data) {
        ArrayList<Attribute> fvWekaAttributes = new ArrayList<>(info.getTipoAtributo().size());
        //FastVector fvWekaAttributes = new FastVector(info.getTipoAtributo().size());
        for (int i = 0; i < info.getTipoAtributo().size(); i++) {
            if (!info.getTipoAtributo().get(i).toUpperCase().trim().equals("NUMERIC")
                    && !info.getTipoAtributo().get(i).toUpperCase().trim().equals("REAL")
                    && !info.getTipoAtributo().get(i).toUpperCase().trim().equals("INTEGER")) {
                String line = info.getTipoAtributo().get(i);
                line = line.replace("{", "");
                line = line.replace("}", "");
                String token[] = line.split(",");
                ArrayList<String> fvNominalVal = new ArrayList<>(token.length);
                //FastVector fvNominalVal = new FastVector(token.length);
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

        for (int i = 0; i < data.size(); i++) {
            Instance iExample = new DenseInstance(info.getTipoAtributo().size());
            String token[] = data.get(i).split(",");
            for (int j = 0; j < info.getTipoAtributo().size(); j++) {
                switch (info.getTipoAtributo().get(j).toUpperCase().trim()) {
                    case "NUMERIC":
                    case "REAL":
                        double real = Double.parseDouble(token[j].trim());
                        iExample.setValue((Attribute) fvWekaAttributes.get(j), real);
                        break;
                    case "INTEGER":
                        double real2 = Double.parseDouble(token[j].trim());
                        int integer = (int) Math.round(real2);
                        iExample.setValue((Attribute) fvWekaAttributes.get(j), integer);
                        break;
                    default:
                        iExample.setValue((Attribute) fvWekaAttributes.get(j), token[j]);
                }
            }
            // add the instance
            instance.add(iExample);
        }
        return instance;
    }

}

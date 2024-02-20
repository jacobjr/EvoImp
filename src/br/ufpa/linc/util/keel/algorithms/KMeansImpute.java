package br.ufpa.linc.util.keel.algorithms;

/**
 * *********************************************************************
 *
 * This file is part of KEEL-software, the Data Mining tool for regression,
 * classification, clustering, pattern mining and so on.
 *
 * Copyright (C) 2004-2010
 *
 * F. Herrera (herrera@decsai.ugr.es) L. Sánchez (luciano@uniovi.es) J.
 * Alcalá-Fdez (jalcala@decsai.ugr.es) S. García (sglopez@ujaen.es) A. Fernández
 * (alberto.fernandez@ujaen.es) J. Luengo (julianlm@decsai.ugr.es)
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/
 *
 *********************************************************************
 */
/**
 * <p>
 * @author Written by Julián Luengo Martín 29/11/2006
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
import br.ufpa.linc.util.arff.InformacoesArff;
import java.util.ArrayList;
import org.core.*;
import br.ufpa.linc.util.keel.util.FreqList;
import br.ufpa.linc.util.keel.util.gCenter;

/**
 * <p>
 * This class imputes the missing values by means of the K-means clustering
 * algorithm. It creates a set of K clusters, and the missing values are filled
 * in with the required values of the nearest centroid.
 * </p>
 */
public class KMeansImpute {

    FreqList[] timesSeen = null; //matrix with frequences of attribute values
    String[] mostCommon;

    int ndatos = 0;
    int nvariables = 0;
    int K; //number of clusters
    long semilla = 12345678;
    double minError;
    int maxIter;
    InformacoesArff input_arff;

    /**
     * Creates a new instance of kmeansImpute
     *
     * @param in input arff
     * @param seed seed
     * @param k number of clusters
     * @param error minimum error
     * @param maxIter number of maximum iterations
     */
    public KMeansImpute(InformacoesArff in, long seed, int k, double error, int maxIter) {
        this.input_arff = in;
        this.K = k;
        this.semilla = seed;
        this.minError = error;
    }

    /**
     * <p>
     * Process the training and test files provided in the parameters file to
     * the constructor.
     * </p>
     */
    public ArrayList<String> process() {
        //declarations
        int actual;
        Randomize.setSeed(semilla);
        String ex;
        gCenter kmeans;
        int iterations;
        double E;
        double prevE;
        int totalMissing;
        boolean allMissing;

        //PROCESS
        try {

            ndatos = input_arff.getData().size();
            nvariables = input_arff.getNomeAtributo().size();

            kmeans = new gCenter(K, input_arff);

            timesSeen = new FreqList[nvariables];
            mostCommon = new String[nvariables];

            //first, we choose k 'means' randomly from all
            //instances
            totalMissing = 0;
            for (int i = 0; i < ndatos; i++) {
                String inst = input_arff.getData().get(i);
                if (inst.contains("?")) {
                    totalMissing++;
                }
            }
            allMissing = totalMissing == ndatos;
            for (int numMeans = 0; numMeans < K; numMeans++) {
                do {
                    actual = (int) (ndatos * Randomize.Rand());
                    ex = input_arff.getData().get(actual);
                } while (ex.contains("?") && !allMissing);

                kmeans.copyCenter(ex, numMeans);
            }

            //now, iterate adjusting clusters' centers and
            //instances to them
            prevE = 0;
            iterations = 0;
            do {
                for (int i = 0; i < ndatos; i++) {
                    String inst = input_arff.getData().get(i);

                    kmeans.setClusterOf(inst, i);

                }
                //set new centers
                kmeans.recalculateCenters();
                //compute RMSE
                E = 0;
                for (int i = 0; i < ndatos; i++) {
                    String inst = input_arff.getData().get(i);
                    E += kmeans.distance(inst, kmeans.getClusterOf(i));
                }
                iterations++;
                //System.out.println(iterations+"\t"+E);
                if (Math.abs(prevE - E) == 0) {
                    iterations = maxIter;
                } else {
                    prevE = E;
                }
            } while (E > minError && iterations < maxIter);
            
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < ndatos; i++) {
                String inst = input_arff.getData().get(i);
                
                if(inst.contains("?")){
                    String attibuteValues[] = inst.split(",");
                    String line = "";
                    for (int j = 0; j < nvariables; j++) {
                        String value = attibuteValues[j].trim();
                        String tipo = input_arff.getTipoAtributo().get(j).trim().toUpperCase();
                        if(value.equals("?")){
                            actual = kmeans.getClusterOf(i);
                            value = kmeans.valueAt(actual, j);
                        }
                        line += ","+value;
                    }
                    data.add(line.substring(1));
                } else {
                    data.add(inst);
                }
            }
          
            return data;
        } catch (Exception e) {
            System.out.println("Dataset exception = " + e);
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

}

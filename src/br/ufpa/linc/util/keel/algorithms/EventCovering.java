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
 * @author Written by Julián Luengo Martín 06/03/2006
 * @version 0.5
 * @since JDK 1.5
 * </p>
 */
import br.ufpa.linc.util.arff.InformacoesArff;
import java.util.*;
import br.ufpa.linc.util.keel.util.Cluster;
import br.ufpa.linc.util.keel.util.FreqList;
import br.ufpa.linc.util.keel.util.FreqListPair;
import br.ufpa.linc.util.keel.util.Pair;
import br.ufpa.linc.util.keel.util.StatFunc;
import br.ufpa.linc.util.keel.util.ValueFreq;
import br.ufpa.linc.util.keel.util.ValuesFreq;

/**
 * <p>
 * Based on the work of Wong et al., a mixed-mode probability model is
 * approximated by a discrete one. First, they discretize the continuous
 * components using a minimum loss of information criterion. Treating a
 * mixed-mode feature n-tuple as a discrete-valued one, the authors propose a
 * new statistical approach for synthesis of knowledge based on cluster
 * analysis. As main advantage, this method does not require neither scale
 * normalization nor ordering of discrete values. By synthesis of the data into
 * statistical knowledge, they refer to the following processes: 1) synthesize
 * and detect from data inherent patterns which indicate statistical
 * interdependency; 2) group the given data into inherent clusters based on
 * these detected interdependency; and 3) interpret the underlying patterns for
 * each clusters identified.
 * </p>
 * The method of synthesis is based on author's eventcovering approach. With the
 * developed inference method, we are able to estimate the MVs in the data. This
 * method assumes the data is DISCRETIZED (but won't throw any error with
 * continuous data).
 */
public class EventCovering {

    int ndatos = 0;
    int nvariables = 0;
    int totalMissing = 0;

    //METHOD PARAMS
    double T ;
    int min_change_num ;
    double Cfactor;

    InformacoesArff input_arff;
    StatFunc chi;

    /**
     * Creates a new instance of EventCovering
     *
     * @param in
     * @param T
     * @param min_change_num
     * @param Cfactor
     */
    public EventCovering(InformacoesArff in, double T, int min_change_num, double Cfactor) {
        this.input_arff = in;
        this.T = T;
        this.min_change_num = min_change_num;
        this.Cfactor = Cfactor;
    }

    /**
     * <p>
     * Computes the Hamming distance between 2 instances
     * </p>
     *
     * @param i1 First Instance
     * @param i2 Second instance
     * @return The Hamming distance
     */
    protected double dist(String i1, String i2) {
        double d = 0;
        String[] a;
        String[] b;

        //Hamming distance
        a = i1.split(",");
        b = i2.split(",");
        for (int i = 0; i < nvariables; i++) {
            if (!a[i].equals(b[i])) {
                d++;
            }
        }
        return d;
    }

    /**
     * <p>
     * Estimates the mutual information between the instances in the data set
     * </p>
     *
     * @return The mutual information for all possible combinations of 2
     * instances
     */
    protected double[][] computeMutualInformation() {
        double[][] I;
        String inst;
        FreqListPair[][] frec;
        ValuesFreq F;
        String u, v;
        double f_uv, f_u, f_v;
        String[] ent;
        String[] all;
        String e1, e2;

        frec = new FreqListPair[nvariables][nvariables];
        for (int i = 0; i < nvariables; i++) {
            for (int j = 0; j < nvariables; j++) {
                frec[i][j] = new FreqListPair();
            }
        }
        //matrix of mutual summed information
        I = new double[nvariables][nvariables];
        all = new String[nvariables];

        for (int k = 0; k < ndatos; k++) {
            inst = input_arff.getData().get(k);

            ent = inst.split(",");
            for (int m = 0; m < nvariables; m++) {
                if (!ent[m].trim().equals("?")) {
                    all[m] = ent[m].trim();
                } else {
                    all[m] = Double.MIN_VALUE + "";
                }
            }

            for (int i = 0; i < nvariables; i++) {
                for (int j = i + 1; j < nvariables; j++) {
                    u = all[i];
                    v = all[j];
                    frec[i][j].AddElement(String.valueOf(u), String.valueOf(v));
                }
            }
        }
        for (int i = 0; i < nvariables; i++) {
            for (int j = i + 1; j < nvariables; j++) {
                frec[i][j].reset();
                I[i][j] = 0;
                while (!frec[i][j].outOfBounds()) {
                    F = frec[i][j].getCurrent();
                    e1 = F.getValue1();
                    e2 = F.getValue2();
                    f_u = (double) frec[i][j].elem1SumFreq(e1) / ndatos;
                    f_v = (double) frec[i][j].elem2SumFreq(e2) / ndatos;
                    f_uv = (double) F.getFreq() / ndatos;
                    I[i][j] += f_uv * Math.log(f_uv / (f_u * f_v)) / Math.log(2);
                    frec[i][j].iterate();
                }
            }
        }

        return I;
    }

    /**
     * <p>
     * Computes the dependece Tree using Dijkstra algorithm
     * </p>
     *
     * @param I The paired-mutual information of this data set
     * @return
     */
    protected Vector computeTree(double[][] I) {
        double[] nodo;
        int ik = -1, jk = -1;
        int k, m;
        double max;
        nodo = new double[nvariables];
        Vector tree = new Vector();
        Pair par;

        for (int i = 0; i < nvariables; i++) {
            nodo[i] = i;
        }
        k = 1;
        while (k - nvariables < 0) {
            //search for maximum I
            //since I[i][j] is always non-negative, use initial MAX value as a negative one
            //if we don't want consider zero-Information values, use max = 0 as initial value
            max = -1;
            for (int i = 0; i < nvariables - 1; i++) {
                for (int j = i + 1; j < nvariables; j++) {
                    if (I[i][j] > max) {
                        ik = i;
                        jk = j;
                        max = I[i][j];
                    }
                }
            }
            if (nodo[ik] == nodo[jk]) {
                I[ik][jk] = -1;
            } else {
                par = new Pair(ik, jk);
                tree.addElement(par);
                I[ik][jk] = -1;
                m = 0;
                while (m < nvariables) {
                    if (nodo[m] == nodo[jk]) {
                        nodo[m] = nodo[jk];
                    }
                    m++;
                }
                k++;
            }
        }
        return tree;
    }

    /**
     * <p>
     * Computes the conjunctive probabilities using the second order
     * probabilities.
     * </p>
     *
     * @param tree The dependence tree of this data set
     * @return An array of probabilites for each instance (in the same order)
     */
    protected double[] computePx(Vector tree) {
        double[] Px;
        String inst, e;
        String a, b;
        String x1, x2;
        int count, total;
        Pair p;

        Px = new double[ndatos];
        for (int i = 0; i < ndatos; i++) {
            inst = input_arff.getData().get(i);
            if (!inst.contains("?")) {
                a = inst.split(",")[0].trim();
                count = 0;
                for (int j = 0; j < ndatos; j++) {
                    e = input_arff.getData().get(j);
                    if (a.equals(e.split(",")[0].trim())) {
                        count++;
                    }
                }
                Px[i] = (double) count / ndatos;
                for (int j = 0; j < tree.size(); j++) {
                    p = (Pair) tree.elementAt(j);
                    String attributeValues[] = inst.split(",");
                    a = attributeValues[p.e1].trim();
                    b = attributeValues[p.e2].trim();
                    count = 0;
                    total = 0;
                    for (int k = 0; k < ndatos; k++) {
                        e = input_arff.getData().get(k);
                        x1 = e.split(",")[p.e1].trim();
                        x2 = e.split(",")[p.e2].trim();

                        if (x1.equals(a)) {
                            total++;
                            if (x2.equals(b)) {
                                count++;
                            }
                        }
                    }
                    Px[i] *= (double) count / total;
                }
            } else {
                Px[i] = -1; //instance with missing data, do not count for cluster making!
                totalMissing++;
            }
        }
        return Px;
    }

    /**
     * <p>
     * Initializes the set of clusters using information of the data set
     * </p>
     *
     * @param Px The second order probablity estimation
     * @return a initinal set of clusters
     */
    protected Vector clusterInitation(double[] Px) {
        int k, t;
        double muMean;
        double Dst;
        double P_;
        double d, dmax, dtop, p;
        int max, tmax, choosenV;
        int alreadyTaken;
        boolean Dfound, found;
        Vector L = new Vector();
        Vector Dist = new Vector();
        Vector Ps = new Vector();
        Vector Index = new Vector();
        String x;
        Vector Clusters = new Vector();
        Cluster cluster;
        //InstanceSet IS ;
        k = 0;
        t = 0;

        //IS.readSet(input_train_name,true);
        muMean = 0;
        max = 0;
        for (int i = 0; i < ndatos; i++) {
            if (Px[i] >= 0) {
                muMean += Px[i];
                if (Px[i] > Px[max]) {
                    max = i;
                }
            }
        }
        muMean = muMean / ndatos;

        tmax = 0;
        for (int j = 0; j < nvariables; j++) {
            String tipo = input_arff.getTipoAtributo().get(j).trim().toUpperCase();
            if (tipo.equals("NUMERIC") || tipo.equals("REAL") || tipo.equals("INTEGER")) {
                double attributeMin = Double.MAX_VALUE;
                double attributeMax = Double.MIN_VALUE;
                for (int i = 0; i < ndatos; i++) {
                    String reg = input_arff.getData().get(i);
                    String value = reg.split(",")[j].trim();
                    if (!value.equals("?")) {
                        attributeMax = Math.max(attributeMax, Double.parseDouble(value));
                        attributeMin = Math.min(attributeMin, Double.parseDouble(value));
                    }
                }
                if (attributeMax - attributeMin > tmax) {
                    tmax = (int) (attributeMax - attributeMin);
                }
            } else {
                ArrayList<String> attributes = new ArrayList<>();
                for (int i = 0; i < ndatos; i++) {
                    String reg = input_arff.getData().get(i);
                    String value = reg.split(",")[j].trim();
                    if (!value.equals("?")) {
                        if (!attributes.contains(value)) {
                            attributes.add(value);
                        }
                    }
                }
                if (attributes.size() > tmax) {
                    tmax = attributes.size();
                }
            }
        }
//        System.out.println("->"+tmax);
        Cluster C0 = new Cluster();

        alreadyTaken = 0;

        while (alreadyTaken < ndatos - totalMissing) {
            if (ndatos - alreadyTaken > T) {
                P_ = muMean;
            } else {
                P_ = 0;
            }

            for (int i = 0; i < ndatos; i++) {
                if (Px[i] > P_) {
                    L.addElement(input_arff.getData().get(i));
                    Ps.addElement(Px[i]);
                    Index.addElement(i);
                }
            }
            //compute D for each x
            Dist.clear();
            for (int i = 0; i < L.size(); i++) {
                x = (String) L.elementAt(i);
                Dist.addElement(D(x, L));
            }
            //get D*
            dtop = Double.MAX_VALUE;
            do {
                dmax = 0;
                for (int i = 0; i < Dist.size(); i++) {
                    d = (double) Dist.elementAt(i);
                    if (dmax < d && d < dtop) {
                        dmax = d;
                    }
                }
                //avoid isolated values, making D* such exist at least
                //one 'x' with D*-1
                Dfound = false;
                for (int i = 0; i < Dist.size() && !Dfound; i++) {
                    d = (double) Dist.elementAt(i);
                    if ((int) (dmax - 1) <= d) {
                        Dfound = true;
                    }
                }
                if (!Dfound) {
                    dtop = dmax;
                }
            } while (!Dfound && dtop > 1);
            Dst = dmax; //D* found
            do {
                dmax = 0;
                //locate the x with maximum P(x)
                for (int i = 0; i < L.size(); i++) {
                    p = (double) Ps.elementAt(i);
                    if (p > dmax) {
                        max = i;
                        dmax = p;
                    }
                }
                x = (String) L.elementAt(max);
                found = false;
                Vector cv = new Vector();
                for (int i = 0; i < Clusters.size(); i++) {
                    cluster = (Cluster) Clusters.elementAt(i);
                    d = D(x, cluster.C);
                    if (d < Dst) {
                        cv.addElement(i);
                    }
                }
                if (cv.size() == 1) {
                    cluster = (Cluster) Clusters.elementAt((int) cv.firstElement());
                    cluster.C.addElement(x);
                } else {
                    if (cv.size() > 1) {
                        found = false;
                        for (int i = 0; i < cv.size() && !found; i++) {
                            if ((int) cv.elementAt(i) < k) {
                                C0.C.addElement(x);
                                found = true;
                            }
                        }
                        //merge all clusters
                        if (!found) {
                            cluster = (Cluster) Clusters.elementAt((int) cv.firstElement());
                            for (int i = 1; i < cv.size(); i++) {
                                choosenV = (int) cv.elementAt(i);
                                cluster.C.addAll(((Cluster) Clusters.elementAt((int) cv.elementAt(i))).C);
                                Clusters.removeElementAt((int) cv.elementAt(i));
                                t--;
                                //shift left remaining cluster index
                                for (int j = i + 1; j < cv.size(); j++) {
                                    if ((int) cv.elementAt(j) > choosenV) {
                                        cv.set(j, ((int) cv.elementAt(j)) - 1);
                                    }
                                }
                                //cv.removeElementAt(i);
                                //i--; //compensate the shift left
                            }
                        }
                    } //x will form a new cluster by himself
                    else {
                        cluster = new Cluster();
                        cluster.addInstance(x);
                        Clusters.addElement(cluster);
                        t++;
                    }
                }
                alreadyTaken++;
                L.removeElementAt(max);
                Ps.removeElementAt(max);
                Px[(int) Index.elementAt(max)] = -1; //so it cant be choosen again
                Index.removeElementAt(max);
            } while (L.size() > 0);
            k = t;
            muMean = 0;
            max = 0;
            for (int i = 0; i < ndatos; i++) {
                if (Px[i] >= 0) {
                    muMean += Px[i];
                    if (Px[i] > Px[max]) {
                        max = i;
                    }
                }
            }
            muMean = muMean / ndatos;
        }
        for (int i = 0; i < t; i++) {
            cluster = (Cluster) Clusters.elementAt(i);
            if (cluster.C.size() < T) {
                C0.C.addAll(cluster.C);
            }
        }
        Clusters.add(0, C0);

        //assign identifier to each cluster
        for (int i = 0; i < Clusters.size(); i++) {
            ((Cluster) Clusters.elementAt(i)).setNumber(i);
        }

        return Clusters;
    }

    /**
     * <p>
     * This method refines the initial clusters obtained by clusterInitiation()
     * </p>
     *
     * @param Clusters The set of clusters to be refined
     * @return A refined set of clusters
     */
    protected Vector refineClusters(Vector Clusters) {
        FreqList[] obs;
        String[] values;
        String inst;
        Cluster cluster;
        ValueFreq val;
        double confident = 0.05;
        double NS_denom;
        int totalFreqs;
        int nearestCluster, isAt, index;
        double exp, observed, D, I, H, tmp, minNS;
        Vector[] Eck = new Vector[nvariables];
        Vector[] Ekc = new Vector[nvariables];
        FreqListPair atr_clust = new FreqListPair();
        FreqListPair[] acj_xk = new FreqListPair[nvariables];
        double[] R = new double[nvariables];
        Vector nextGenClusters;
        int number_of_change, prev_changes;
        Vector foundIndex = new Vector();
        chi = new StatFunc();

        for (int i = 0; i < nvariables; i++) {
            acj_xk[i] = new FreqListPair();
        }

        obs = new FreqList[nvariables];
        for (int i = 0; i < nvariables; i++) {
            obs[i] = new FreqList();
            Eck[i] = new Vector();
            Ekc[i] = new Vector();
        }
        //make the frequency distribution
        for (int i = 0; i < ndatos; i++) {
            inst = input_arff.getData().get(i);
            values = inst.split(",");
            for (int j = 0; j < nvariables; j++) {
                obs[j].AddElement(values[j].trim());
            }
        }
        D = 0;
        //*********************************************************************
        //*********************************************************************
        //***************************BEGIN refinement**************************
        //*********************************************************************
        //*********************************************************************
        number_of_change = 0;
        do {
            //**************************** REVISAR BEGIN ********************************

            //compute Eck
            totalFreqs = 0;
            for (int k = 0; k < nvariables; k++) {
                obs[k].reset();
                Eck[k].clear();
                while (!obs[k].outOfBounds()) {
                    D = 0;
                    foundIndex.clear();
                    for (int j = 0; j < Clusters.size(); j++) {
                        cluster = (Cluster) Clusters.elementAt(j);
                        exp = obs[k].getCurrent().getFreq() * cluster.C.size();
                        exp = (double) exp / ndatos;
                        observed = cluster.getObserved(obs[k].getCurrent().getValue(), k);
                        if (observed > 0) {
                            foundIndex.addElement(cluster.getNumber());
                        }
                        D = D + (double) (observed - exp) * (observed - exp) / exp;
                    }

                    if (D > StatFunc.chiSquarePercentage(confident, Clusters.size() - 1)) {
                        Eck[k].addElement(obs[k].getCurrent());
                        for (int j = 0; j < foundIndex.size(); j++) {
                            index = (int) foundIndex.elementAt(j);
                            atr_clust.AddElement(String.valueOf(index), String.valueOf(obs[k].getCurrent().getValue()));
                            acj_xk[k].AddElement(String.valueOf(index), String.valueOf(obs[k].getCurrent().getValue()));
                            totalFreqs++;
                        }
                    }
                    obs[k].iterate();
                }
            }
            //check if there was attributes selected for Ekc
            //if not, finish
            prev_changes = number_of_change;
            number_of_change = 0;
            if (totalFreqs != 0) {
                //compute Ekc
                for (int k = 0; k < nvariables; k++) {
                    Ekc[k].clear();
                    for (int j = 0; j < Clusters.size(); j++) {
                        D = 0;
                        cluster = (Cluster) Clusters.elementAt(j);
                        obs[k].reset();
                        while (!obs[k].outOfBounds()) {
                            exp = obs[k].getCurrent().getFreq() * cluster.C.size();
                            exp = (double) exp / ndatos;
                            observed = cluster.getObserved(obs[k].getCurrent().getValue(), k);
                            D = D + (double) (observed - exp) * (observed - exp) / exp;
                            obs[k].iterate();
                        }
                        if (D > StatFunc.chiSquarePercentage(confident, Clusters.size() - 1)) {
                            Ekc[k].addElement(Clusters.elementAt(j));
                        }
                    }
                }

                //**************************** REVISAR END ********************************
                //now, compute the interdependency redundancy measure
                //between Xck and Ck
                for (int k = 0; k < nvariables; k++) {
                    I = 0;
                    H = 0;
                    //compute expected mutual information and entropy
                    for (int u = 0; u < Eck[k].size(); u++) {
                        for (int s = 0; s < Ekc[k].size(); s++) {
                            cluster = (Cluster) Ekc[k].elementAt(s);
                            val = (ValueFreq) Eck[k].elementAt(u);
                            tmp = (double) atr_clust.getPairFreq(String.valueOf(cluster.getNumber()), String.valueOf(val.getValue())) / totalFreqs;
                            if (tmp > 0) {
                                H -= (double) tmp * Math.log(tmp) / Math.log(2);
                                tmp = (double) tmp * Math.log(tmp / ((double) val.getFreq() * cluster.C.size() / (totalFreqs * totalFreqs))) / Math.log(2);
                                I += tmp;
                            }
                        }
                    }
                    if (I != 0 && H != 0) {
                        R[k] = (double) I / H;
                    } else {
                        R[k] = 0;
                    }
                }

                NS_denom = 0;
                for (int k = 0; k < nvariables; k++) {
                    NS_denom += R[k];
                }
                NS_denom *= nvariables;

                nextGenClusters = (Vector) Clusters.clone();
                for (int i = 0; i < Clusters.size(); i++) {
                    cluster = (Cluster) Clusters.elementAt(i);
                    for (int j = 0; j < cluster.C.size(); j++) {
                        inst = (String) cluster.C.elementAt(j);
                        minNS = Double.MAX_VALUE;
                        nearestCluster = 0; //the dummy cluster C0
                        for (int u = 1; u < Clusters.size(); u++) {
                            tmp = NS(inst, u, cluster.C.size(), R, acj_xk, Ekc, NS_denom);
                            if (i != 0 && u == i && tmp != -1) {
                                tmp = tmp / Cfactor;
                            }
                            if (tmp != -1 && tmp < minNS) {
                                nearestCluster = u;
                                minNS = tmp;
                            }

                        }
                        if (nearestCluster != i) {
                            //move the element to destination cluster
                            isAt = ((Cluster) nextGenClusters.elementAt(i)).C.indexOf(inst);
                            ((Cluster) nextGenClusters.elementAt(i)).C.removeElementAt(isAt);
                            ((Cluster) nextGenClusters.elementAt(nearestCluster)).addInstance(inst);
                            number_of_change++;
                        }
                    }
                }

                Clusters.clear();
                Clusters = nextGenClusters;
            }
        } while (number_of_change > 0 && Math.abs(number_of_change - prev_changes) >= min_change_num);
        //*********************************************************************
        //*********************************************************************
        //***************************END refinement****************************
        //*********************************************************************
        //*********************************************************************
        //for(int i=0)
        return Clusters;
    }

    protected double NS(String inst, int numCluster, int sizeCluster, double[] R, FreqListPair[] acj_xk, Vector[] Ekc, double NS_denom) {
        double prob;
        double temp;
        String xk;
        double sum_Pcond;
        double mutualI;
        double NSvalue;
        Cluster cluster;
        String[] attributeValues;

        attributeValues = inst.split(",");

        mutualI = 0;
        for (int k = 0; k < nvariables; k++) {
            xk = attributeValues[k];

            sum_Pcond = 0;
            for (int i = 0; i < Ekc[k].size(); i++) {
                cluster = (Cluster) Ekc[k].elementAt(i);
                sum_Pcond += (double) acj_xk[k].sumPairFreq(String.valueOf(cluster.getNumber()), String.valueOf(xk)) / sizeCluster;
            }
            temp = 0;
            if (sum_Pcond > 0 && sum_Pcond > T) {
                prob = (double) acj_xk[k].sumPairFreq(String.valueOf(numCluster), String.valueOf(xk)) / sizeCluster;
                if (prob > 0) {
                    temp = (double) prob / sum_Pcond;
                    temp = -Math.log(temp) / Math.log(2);
                    temp *= R[k];
                }
            }
            mutualI += temp;
        }
        if (mutualI
                != 0) {
            NSvalue = (double) mutualI / NS_denom;
        } else {
            NSvalue = -1;
        }

        return NSvalue;
    }

    protected double D(String x, Vector S) {
        double dmin;
        double d;

        dmin = Double.MAX_VALUE;
        for (int i = 0; i < S.size(); i++) {
            if (!x.equals((String) S.elementAt(i))) {
                d = dist(x, (String) S.elementAt(i));
                if (d < dmin) {
                    dmin = d;
                }
            }
        }
        return dmin;
    }

    /**
     * <p>
     * Process the training and test files provided in the parameters file to
     * the constructor.
     * </p>
     */
    public ArrayList<String> process() {
        int in = 0;
        int out = 0;
        double minD = 0;
        double dist;
        String i1, i2;
        Vector Clusters = null;
        Cluster c;
        int selectedCluster = 0;
        int centroid = 0;

        Vector tree;
        double[][] I;
        double[] Px;
        try {

            ndatos = input_arff.getData().size();
            nvariables = input_arff.getNomeAtributo().size();
            totalMissing = 0;

            //Create clusters for all instances without data missing
            I = computeMutualInformation();
            tree = computeTree(I);
            Px = computePx(tree);
            if (totalMissing != ndatos && totalMissing != 0) {
                Clusters = clusterInitation(Px);
//                System.out.println(Clusters.size());
                for (int i = 0; i < Clusters.size(); i++) {
                    c = (Cluster) Clusters.elementAt(i);
                }
                Clusters = refineClusters(Clusters);
            } else {
                Cluster C0 = new Cluster();
                Clusters = new Vector();
                for (int i = 0; i < ndatos; i++) {
                    String inst = input_arff.getData().get(i);
                    C0.C.addElement(inst);
                }
                Clusters.addElement(C0);
            }
//            for(Object obj : Clusters){
//                System.out.print(((Cluster)obj).C.size()+" ");
//            }
//            System.out.println("");

            ArrayList<String> data = new ArrayList<>();
            //process current dataset
            for (int i = 0; i < ndatos; i++) {
                String inst = input_arff.getData().get(i);

                if (inst.contains("?")) {
                    String line = "";
                    String attributeValues[] = inst.split(",");
                    for (int j = 0; j < nvariables; j++) {
                        String value = attributeValues[j].trim();
                        if (value.equals("?")) {

                            //missing data, we must find the cluster this
                            //instance fits better
                            minD = Double.MAX_VALUE;
                            for (int u = 0; u < Clusters.size(); u++) {
                                c = (Cluster) Clusters.elementAt(u);
                                dist = D(inst, c.C);
                                if (dist < minD) {
                                    selectedCluster = u;
                                    minD = dist;
                                }
                            }
                            //now, find the nearest element of the cluster
                            c = (Cluster) Clusters.elementAt(selectedCluster);
                            minD = Double.MAX_VALUE;
                            dist = 0;
                            for (int l = 0; l < c.C.size(); l++) {
                                i2 = (String) c.C.elementAt(l);
                                dist = dist(inst, i2);
                                if (i2.split(",")[j].trim().equals("?")) {
                                    dist += nvariables;
                                }
                                if (dist < minD) {
                                    minD = dist;
                                    centroid = l;
                                }
                            }

                            //use the nearest attribute as reference
                            i1 = (String) c.C.elementAt(centroid);
                            if (i1.split(",")[j].trim().equals("?")) {
                                value = "?";
                            } else {
                                value = i1.split(",")[j].trim();
                            }
                        }
                        line += "," + value;
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

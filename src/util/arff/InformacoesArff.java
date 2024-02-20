package util.arff;

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

import java.util.ArrayList;
import java.util.TreeMap;

public class InformacoesArff {
/**
 * Class InformacoesArff
 * Get data from an Arff file
 * @author Fábio Lobato (fabio.lobato@ufopa.edu.br)
 * @version 1.0
 */ 

    private ArrayList<String> nomeAtributo = new ArrayList<>();
    private ArrayList<String> tipoAtributo = new ArrayList<>();
    private ArrayList<Integer> possiveisClasses = new ArrayList<>();
    private ArrayList<String> cabecalho = new ArrayList<>();
    private TreeMap<Integer, String> dateFormat = new TreeMap<>();
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<String> linhaAtributo = new ArrayList<>();
    private final String nomeBase;

    public InformacoesArff(ArrayList<String> cabecalho, String nomeBase, ArrayList<String> linhaAtributo, ArrayList<String> nomeAtributo, ArrayList<String> tipoAtributo, ArrayList<Integer> possiveisClasses,
            TreeMap<Integer, String> dateFormat, ArrayList<String> data) {
        this.linhaAtributo = linhaAtributo;
        this.cabecalho = cabecalho;
        this.nomeBase = nomeBase;
        this.nomeAtributo = nomeAtributo;
        this.tipoAtributo = tipoAtributo;
        this.possiveisClasses = possiveisClasses;
        this.dateFormat = dateFormat;
        this.data = data;


    }

    public ArrayList<String> getData() {
        return data;
    }

    public TreeMap<Integer, String> getDateFormat() {
        return dateFormat;
    }

    public ArrayList<String> getNomeAtributo() {
        return nomeAtributo;
    }

    public ArrayList<Integer> getPossiveisClasses() {
        return possiveisClasses;
    }

    public ArrayList<String> getTipoAtributo() {
        return tipoAtributo;
    }

    public String getnomeBase() {
        return nomeBase;
    }

    public ArrayList<String> getCabecalho() {
        return cabecalho;
    }

    public ArrayList<String> getLinhaAtributo() {
        return linhaAtributo;
    }
}


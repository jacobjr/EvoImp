package br.ufpa.linc.util.arff;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeitorArff {
/**
 * Class LeitorArff
 * Read an Arff file
 * @author Fábio Lobato (fabio.lobato@ufopa.edu.br)
 * @version 1.0
 */
    
    private final File file;

    public LeitorArff(File file) {
        this.file = file;
    }

    public InformacoesArff lerArff() {
        ArrayList<String> nomeAtributo = new ArrayList<>();
        String nomeBase = "";
        ArrayList<String> cabecalho = new ArrayList<>();
        ArrayList<String> tipoAtributo = new ArrayList<>();
        ArrayList<Integer> possiveisClasses = new ArrayList<>();
        TreeMap<Integer, String> dateFormat = new TreeMap<>();
        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> linhaAtributo = new ArrayList<>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;

            Matcher comment;
            Matcher relation;
            Matcher attribute;
            Matcher classe;
            Matcher attDate;
            Matcher mdata;

            while ((line = bf.readLine()) != null) {
                comment = pComentario.matcher(line);
                relation = pRelation.matcher(line);
                attribute = pAttribute.matcher(line);
                classe = pAttributeNominal.matcher(line);
                attDate = pAttributeDate.matcher(line);
                mdata = pData.matcher(line);

                if (comment.matches()) {
                    cabecalho.add(line);
                } else if (relation.matches()) {
                    nomeBase = relation.group(1);
                } else if (classe.matches()) {
                    linhaAtributo.add(classe.group(0));
                    nomeAtributo.add(classe.group(1));
                    tipoAtributo.add(classe.group(2));
                    possiveisClasses.add(linhaAtributo.size() - 1);
                } else if (attribute.matches()) {
                    linhaAtributo.add(attribute.group(0));
                    nomeAtributo.add(attribute.group(1));
                    tipoAtributo.add(attribute.group(2));
                } else if (attDate.matches()) {
                    linhaAtributo.add(attDate.group(0));
                    nomeAtributo.add(attDate.group(1));
                    tipoAtributo.add(attDate.group(2));
                    dateFormat.put(tipoAtributo.indexOf(attDate.group(2)), attDate.group(3));
                } else if (mdata.matches()) {
                    break;
                }
            }

            while ((line = bf.readLine()) != null) {
                line = line.trim();
                if (line.contains(",")) {
                    data.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException e) {
            // do nothing
        }

        InformacoesArff infoArff = new InformacoesArff(cabecalho, nomeBase, linhaAtributo, nomeAtributo, tipoAtributo, possiveisClasses, dateFormat, data);
        return infoArff;
    }
    private static final Pattern pComentario = Pattern.compile("^\\s*%.*$");
    private static final Pattern pRelation = Pattern.compile("^\\s*@relation\\s+(\\S+|[\"'].+[\"'])(\\s+)?(%.*$)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern pAttribute = Pattern.compile("^\\s*@attribute\\s+(\\S+|[\"'].+[\"'])\\s+(\\S+)(\\s\\S+)?(\\s+)?(%.*$)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern pAttributeNominal = Pattern.compile("^\\s*@attribute\\s+(\\S+|[\"'].+[\"'])\\s+(\\{.+\\})(\\s+)?(%.*$)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern pAttributeDate = Pattern.compile("^\\s*@attribute\\s+(\\S+|[\"'].+[\"'])\\s+(date)\\s+(\\S+|[\"'].+[\"'])(\\s+)?(%.*$)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern pData = Pattern.compile("^\\s*@data(\\s+%.*$)?", Pattern.CASE_INSENSITIVE);
}
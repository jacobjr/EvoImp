# EvoImp
Multiple Imputation of Multi-label Classification Data With a Genetic Algorithm

***
#### **About the paper**
> Article submitted to the journal Plos One, in the year 2023. This work proposed an method in multi-label learning and evaluated its performance using six synthetic databases, considering various missing values distribution scenarios.


#### **Authors (original paper)**
<table>
  <tr>
    <td align="center"><a href="http://lattes.cnpq.br/4510520291728075"><img style="width: 99px; height:auto;" src="http://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4125840Z1" width="100px;" alt=""/><br /><sub><b>Antonio F. L. Jacob Jr.</b></sub></a><br /><sub><b>UEMA | UFMA</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/5415578583738595"><img style="width: 99px; height:auto;" src="https://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4367095H1" width="100px;" alt=""/><br /><sub><b>Fabrício A. do Carmo</b></sub></a><br /><sub><b>UEMA</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/4073088744952858"><img style="width: 100px; height:auto;" src="https://lincproguema.com/misc/adamo.png" width="100px;" alt=""/><br /><sub><b>Ádamo L. Santana</b></sub></a><br /><sub><b> Fuji Electric Co., Japan</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/0660692009750374"><img style="width: 100px; height:auto;" src="http://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4509325E9" width="110px;" alt=""/><br /><sub><b>Ewaldo Santana</b></sub></a><br /><sub><b>UEMA | UFMA</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/8320014491229434"><img style="width: 108px; height:auto;" src="http://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4450672H1" width="100px;" alt=""/><br /><sub><b>Fábio M. F. Lobato</b></sub></a><br /><sub><b>UFOPA | UEMA</b></sub></a></td>
  </tr>
<table>
  
  
<br>

# Abstract

Missing data is a prevalent problem that requires attention, as most data analysis techniques are unable to handle it. This is particularly critical in Multi-Label Classification (MLC), in which only a few studies have investigated missing data in this application domain. MLC differs from Single-Label Classification (SLC) by allowing an instance to be associated with multiple classes. Movie classification is a didactic example since it can be ``drama'' and ``bibliography'' simultaneously. One of the most usual missing data treatment methods is data imputation, which seeks plausible values to fill in the missing ones. In this scenario, we propose a novel imputation method based on a multi-objective genetic algorithm for optimizing multiple data imputations called Multiple Imputation of Multi-label Classification data with a genetic algorithm, or simply EvoImp. We applied the proposed method in multi-label learning and evaluated its performance using six synthetic databases, considering various missing values distribution scenarios. The method was compared with other state-of-the-art imputation strategies, such as K-Means Imputation (KMI) and weighted K-Nearest Neighbors Imputation (WKNNI). The results proved that the proposed method outperformed the baseline in all the scenarios by achieving the best evaluation measures considering the Exact Match, Accuracy, and Hamming Loss. The superior results were constant in different dataset domains and sizes, demonstrating the EvoImp robustness. Thus, EvoImp represents a feasible solution to missing data treatment for multi-label learning.

#  If you use any of the resources available here, to cite this work, please use:

> Paper
Jacob Junior, A. F. L., do Carmo, F. A., de Santana, A. L., Santana, E. E. C., & Lobato, F. M. F. (2024). EvoImp: Multiple Imputation of Multi-label Classification data with a genetic algorithm. Plos one, 19(1), e0297147. https://doi.org/10.1371/journal.pone.0297147

> Dataset
Antonio F. L. Jacob Jr., Fabrício A. do Carmo, Ádamo L. de Santana, Ewaldo Santana, & Fábio M. F. Lobato. (2023). Multi-Label Datasets with Missing Values [Data set]. Zenodo. https://doi.org/10.5281/zenodo.7748933

~~~

------------------------------------------
***Directory description***
------------------------------------------
~~~

- src          --> This folder contains the Java code of GAMultImp and the libraries used.
- supp          --> This folder contains additional data cited in the article, such as baseline tests.
  
~~~
  
  
---

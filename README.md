elki
====

ELKI is an open source (AGPLv3) data mining software written in Java. The focus of ELKI is research in algorithms, with an emphasis on unsupervised methods in cluster analysis and outlier detection.

In order to achieve high performance and scalability, ELKI offers many data index structures such as the R*-tree that can provide major performance gains.

ELKI is designed to be easy to extend for researchers and students in this domain, and welcomes contributions in particular of new methods.

ELKI aims at providing a large collection of highly parameterizable algorithms, in order to allow easy and fair evaluation and benchmarking of algorithms. 

What this repository is
=======================
This is ELKI 0.6 built with modern build tools and distributed as a stand-along jar file - like other open-source projects. All dependencies are external and managed by Maven. The jar file created out of this project contains only the ELKI files. This project also contains all test cases that are part of ELKI.

Where is the original souce code?
=================================
The original source code is available at: http://elki.dbs.ifi.lmu.de/browser/elki/trunk?order=name


Where can I learn more about ELKI?
==================================
You need to go to the project home page at http://elki.dbs.ifi.lmu.de/wiki
The most important documentation pages are: 

Tutorial (http://elki.dbs.ifi.lmu.de/wiki/Tutorial)
Searchable JavaDoc (http://elki.dbs.ifi.lmu.de/wiki/JavaDoc)
FAQ (http://elki.dbs.ifi.lmu.de/wiki/FAQ)
InputFormat (http://elki.dbs.ifi.lmu.de/wiki/InputFormat)
DataTypes (http://elki.dbs.ifi.lmu.de/wiki/DataTypes)
DistanceFunctions (http://elki.dbs.ifi.lmu.de/wiki/DistanceFunctions)
DataSets (http://elki.dbs.ifi.lmu.de/wiki/DataSets)
Development (http://elki.dbs.ifi.lmu.de/wiki/Development)
Parameterization (http://elki.dbs.ifi.lmu.de/wiki/Parameterization)
Visualization (http://elki.dbs.ifi.lmu.de/wiki/Visualization)
Benchmarking (http://elki.dbs.ifi.lmu.de/wiki/Benchmarking)
and the list of 
Algorithms (http://elki.dbs.ifi.lmu.de/wiki/Algorithms)
and Related Publications (http://elki.dbs.ifi.lmu.de/wiki/RelatedPublications).

Why this repository was created?
================================
ELKI adds a lot of value to the Machine Learning knowledge-base and algorithms. Though the team is actively working in the academic community to enhance the project, it is not distributed in a form suitable for consumption in the industry. The jar file did not work for me as-is and the source files along with external dependencies are mixed up within the same jar file.

This project aims to separate out the dependencies and sources so that it can be used standalone. This way it be be embedded within other Java and R applications to make the algoritm more amenable to the rest of the world.



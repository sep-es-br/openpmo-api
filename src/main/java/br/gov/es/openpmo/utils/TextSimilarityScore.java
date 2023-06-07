package br.gov.es.openpmo.utils;

@FunctionalInterface
public interface TextSimilarityScore {

  double execute(
    String text1,
    String text2
  );

}

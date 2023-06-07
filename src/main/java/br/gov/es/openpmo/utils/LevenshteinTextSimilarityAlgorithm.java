package br.gov.es.openpmo.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class LevenshteinTextSimilarityAlgorithm implements TextSimilarityScore {

  @Override
  public double execute(
    final String text1,
    final String text2
  ) {
    if (text1 == null || text2 == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }

    final String left = normalize(text1);
    final String right = normalize(text2);

    final double maxLength = Double.max(
      left.length(),
      right.length()
    );

    final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    if (maxLength == 0) return 1.0;
    return (maxLength - levenshteinDistance.apply(left, right)) / maxLength;
  }

  private static String normalize(final CharSequence text) {
    final String normalizedText = Normalizer.normalize(
      text,
      Normalizer.Form.NFD
    );
    return normalizedText
      .toLowerCase(Locale.ROOT)
      .replaceAll("[^\\p{ASCII}]", "")
      .replaceAll(" ", "");
  }

}

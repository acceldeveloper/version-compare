package com.g00fy2.versioncompare;

import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * author   Thomas Wirth
 * date     12.01.18
 * website  github.com/G00fY2
 */

final class VersionComparator {

  // position of SemVer version part
  static final int MAJOR = 0;
  static final int MINOR = 1;
  static final int PATCH = 2;

  // weighting of the PreRelease suffixes
  private static final int PRE_ALPHA = 0;
  private static final int ALPHA = 1;
  private static final int BETA = 2;
  private static final int RELEASE_CANDIDATE = 3;
  private static final int UNKNOWN = 4;

  // regex to find numeric characters
  @NotNull static final Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]+");

  static int compareSubversionNumbers(@NotNull final List<Integer> subversionsA,
      @NotNull final List<Integer> subversionsB) {
    final int versionASubversionsCount = subversionsA.size();
    final int versionBSubversionsCount = subversionsB.size();

    for (int i = 0; i < Math.max(versionASubversionsCount, versionBSubversionsCount); ++i) {
      if ((i < versionASubversionsCount ? subversionsA.get(i) : 0) > (i < versionBSubversionsCount ? subversionsB.get(i)
          : 0)) {
        return 1;
      } else if ((i < versionASubversionsCount ? subversionsA.get(i) : 0) < (i < versionBSubversionsCount ? subversionsB
          .get(i) : 0)) {
        return -1;
      }
    }
    return 0;
  }

  static int compareSuffix(@NotNull final String suffixA, @NotNull final String suffixB) {
    if (suffixA.length() > 0 || suffixB.length() > 0) {
      int preReleaseQualifierA = preReleaseQualifier(suffixA);
      int preReleaseQualifierB = preReleaseQualifier(suffixB);
      // compare pre release priority
      if (preReleaseQualifierA > preReleaseQualifierB) {
        return 1;
      } else if (preReleaseQualifierA < preReleaseQualifierB) {
        return -1;
      } else if (preReleaseQualifierA != UNKNOWN && preReleaseQualifierB != UNKNOWN) {
        int suffixVersionA = preReleaseVersionInfo(suffixA.split("\\p{P}"));
        int suffixVersionB = preReleaseVersionInfo(suffixB.split("\\p{P}"));
        if (suffixVersionA > suffixVersionB) {
          return 1;
        } else if (suffixVersionA < suffixVersionB) {
          return -1;
        } else {
          return 0;
        }
      }
    }
    return 0;
  }

  private static int preReleaseQualifier(@NotNull String suffix) {
    if (suffix.length() > 0) {
      suffix = suffix.toLowerCase();
      if (suffix.contains("pre") && suffix.contains("alpha")) return PRE_ALPHA;
      if (suffix.contains("alpha")) return ALPHA;
      if (suffix.contains("beta")) return BETA;
      if (suffix.contains("rc")) return RELEASE_CANDIDATE;
    }
    return UNKNOWN;
  }

  private static int preReleaseVersionInfo(@NotNull String[] preReleaseSuffixes) {
    // TODO: handle numbers before preReleaseQualifier
    for (String suffix : preReleaseSuffixes) {
      if (NUMERIC_PATTERN.matcher(suffix).find()) {
        StringBuilder versionNumber = new StringBuilder();
        for (int i = 0, lastNumIndex = 0; i < suffix.length() && (lastNumIndex == 0 || lastNumIndex + 1 == i); i++) {
          if (NUMERIC_PATTERN.matcher(String.valueOf(suffix.charAt(i))).matches()) {
            lastNumIndex = i;
            versionNumber.append(suffix.charAt(i));
          }
        }
        return Integer.valueOf(versionNumber.toString());
      }
    }
    return 0;
  }
}

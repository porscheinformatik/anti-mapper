# anti-mapper 1.3

## anti-mapper-1.3.1

* Some code cleanup.

## anti-mapper-1.3.0

* Added `Hint.UNMODIFIABLE` for the transformer to produce unmodifiable collections and maps.
  This hint replaces the two `*unmodifiableArrayList` functions. 

# anti-mapper 1.2

## anti-mapper-1.2.0

* Removed Referer. You won't need it.
* Entity automatically gets added to hints in AbstractMerger and AbstractMapper, now.

# anti-mapper 1.1

## anti-mapper-1.1.0

* Simplifies the mapping with deleted-flags (see sample).
* Renamed the `transformInternal` and `mergeInternal` methods to `transformNonNull` and 
  `mergeNonNull` in the abstract mapper classes. Added `transformNull` and `mergeNull` methods.
  
* Renamed to anti-mapper to avoid collisions with existing happymapper library.
* Renamed the package `at.porscheinformatik.happy.mapper` to `at.porscheinformatik.antimapper`.
* Renamed the module from `at.porscheinformatik:happy-mapper` to `at.porscheinformatik:anti-mapper`.

* No more RELEASE suffixes in versions.

# happy-mapper 1.0

## happy-mapper-1.0.3-RELEASE

* Fixes some rare special cases.

## happy-mapper-1.0.2-RELEASE

* Adds sources and JavaDocs to build

## happy-mapper-1.0.1-RELEASE

* Optimizes the rescuing of target values with list mappings.

## happy-mapper-1.0.0-RELEASE

Initial release.

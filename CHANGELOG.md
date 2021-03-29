# anti-mapper 1.6

## anti-mapper 1.6.2

* Updates to build and test dependencies

## anti-mapper 1.6.1

* Added Hint.KEEP_MISSING to merge operations. Entities in collections won't be deleted if they are missing in the DTOs.

## anti-mapper 1.6.0

* `Hints.optionalHint` now returns an `Optional`. This is breaking change, but fairly easy to fix. Use `Hint.hintOrNull` as direct replacement.
* Added methods for `LinkedHashSet`s.

# anti-mapper 1.5

## anti-mapper 1.5.0

* Removed deprecated methods.

# anti-mapper 1.4

## anti-mapper 1.4.8

* Updated README.
* Additional tests.

## anti-mapper 1.4.7

* Fix for #2, mapper ignores Map Key when comparing 
 
## anti-mapper 1.4.6

* StreamTransformer to simplify the interface.
* GroupTransformer to simplify the interface.
* StreamMerger to simplify the interface.
* GroupMerger to simplify the interface.
* Breaking change: Some methods in the MapperUtils now use Streams instead of Iterables.

## anit-mapper-1.4.5

* Adds the possibility to specified default hints for AbstractTransfomers, AbstractMergers and AbstractMappers.
* Adds an AntiMapper class with the ability to specify a Mapper with an fluent interface and lambdas.

## anit-mapper-1.4.4

* Adds Optional to some interfaces

## anti-mapper-1.4.3

* [Issue #1](https://github.com/porscheinformatik/anti-mapper/issues/1): Removed entries will be moved to the end of the list.
* Updates to the documentation.

## anti-mapper-1.4.2

* Adds transformGroupedMap methods.
* Adds Hint.OR_EMPTY. Tells the collection and map transformers and mergers never to return null.

## anti-mapper-1.4.1

* Raised test coverage to 73,2% - all major paths are covered, now.
* Fixes some bugs detected with the tests.
 
## anti-mapper-1.4.0

* Renamed group to `at.porscheinformatik.anti-mapper`. The correct dependency is now:

    <groupId>at.porscheinformatik.anti-mapper</groupId>
    <artifactId>anti-mapper</artifactId>
    <version>1.4.0</version>

* Some code cleanup.

# anti-mapper 1.3

## anti-mapper-1.3.0

* Added `Hint.UNMODIFIABLE` for the transformer to produce unmodifiable collections and maps.
  This hint replaces the two `*UnmodifiableArrayList` functions. 

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

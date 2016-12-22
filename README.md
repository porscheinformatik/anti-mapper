# `anti-mapper`

`anti-mapper` is a Java library, that assists the manual mapping of entities and DTOs.

In contrast to complex mapping frameworks, the `anti-mapper` comes with absolutely **no reflection** and guarantees **zero magic**. It offers **no configuration** at all and shines with a **learing curve flat as a pancake**.

Nevertheless, it is capable to boost your **"I am faster doing it manually"-approach**, because it helps you where it gets complicated: **mapping sets, lists and maps**. Additionally, it unifies your mapping efforts by offering **consistent interfaces** and **base classes**.

## Introduction 

DTOs and entities are part of a design pattern used in enterprise applications. Mapping these objects is a common practice. In Java, you have two possibilities to implement this: either do it manually or use one of the countless libraries.

* Doing it manually is a boring task. It is always the same procedure but nevertheless the implementations fall apart and quickly become irregular, error-prone and hard to maintain.
* Using a library simplifies and unifies the default cases, but usually makes special cases harder to solve. Most often you cannot simply implement theses special cases, you have to configure the mapper instead. And this can be a real pain.

`anti-mapper` offers a third possibility, the **assisted manual mapping**. That's why it is called `anti-mapper`. It is no
classic mapping library. That's exactly the functionality it is missing.  

> **Happy one:** _"It let's you implement the direct mappings manually and just helps with the complex stuff like: collections, sets, lists and maps. It offers simple interfaces like a_ `Transformer` _and a_ `Merger` _which will keep your implementations uniform and consistent. There is no need for any configuration. Everything is pure Java code, checked by the compiler."_


> **Grumpy one:** _"But OMG, what if I add a property to the entity and the DTO, I'm sure, I will forget the mapping. No reflection! What a stupid library!"_
 
> **Happy one:** _"You will test your new property at least once, won't you. You will notice that the mapping is missing. If you don't, it's not the mappers fault."_

> **Grumpy one:** _"I'm faster doing it manually."_
 
> **Happy one:** _"You are mapping it manually. You cannot be faster either way."_

> **Grumpy one:** _"It will never map my lists the right way!"_
 
> **Happy one:** _"It will. It uses a diff-algorithm for lists and reuses entities for Hibernate's sake. It can even handle deleted flags. There is no reason not to use the list mappings. But the best thing is: if you want to implement it in your style, it does not hinder you to do so."_

> **Grumpy one:** _"It's not worth to learn it."_

> **Happy one:** _"There is one recipe that fits all. Just read on."_

## Basic Concepts

### `DTO` and `Entity`

`anti-mapper` commonly uses the terms _entity_ and _DTO_.

For `anti-mapper` an _entity_ is an existing object that has to be modified when being mapped into. The most common case 
for this is a database entity, that's why it is called _entity_. In `anti-mapper` the process of mapping into an 
existing object called _merging_.

A _DTO_ on the other hand, is an object that will always be newly created when mapped into. The most common case for 
this is a DTO used in client-server applications. In `anti-mapper` the process of mapping into a newly created object
is called _transforming_.

### `Transformer`, `Merger` and `Mapper`

The mapping of **Entity** → **DTO** is performed by a `Transformer`. It maps an object (most often an _entity_) into a **newly created** object (most often a _DTO_). 

The mapping of **DTO** → **Entity** is performed by a `Merger`. It maps an object (most often a _DTO_) into an **existing** object (most often an _entity_).

The mapping of **Entity** → **DTO** → **Entity** is performed by a `Mapper`. It is a combination of a `Transformer` and a `Merger`.

### `Hints`

`Hints` are an array of objects that can be passed to transform and merge functions. Hints provide a context for mapping operations that make it simple to access default values or pass parent objects.

## How-To

### `Transformer`

If you want to map an object into a newly created object, then you implement a `Transformer`. 

Create a new class and extend `AbstractTransformer`. Make it a singleton - that means, you should always use the same instance for transforming. Write it thread-safe - that means, you class should only contain final fields!

Call the class something like `*Mapper`, even if it is just a Transformer. This is convenient and makes it easier to find and recognize mapper classes.

A `Transformer` is an interface with one method to implement:

* `DTO_TYPE transform(ENTITY_TYPE entity, Object... hints);`

Additionally, it offers a lot of default methods for transforming streams, collections, sets, lists and maps.

The `AbstractTransformer`, you are using, encapsulates the null-checks. Usually, you just have to implement one method:

    @Override
    protected ParentDTO transformNonNull(ParentEntity entity, Object[] hints)
    {

Start transforming by creating your target object. Final values are an easy task.

        ParentDTO dto = new ParentDTO(entity.getId());

If you want, you can add the DTO to the hints, just in case any child mapper needs it.

        hints = Hints.join(hints, dto);
        
Then set the values. Name and type conversions are trivial.
        
        dto.setName(entity.getKey());
        
        ZoneId timezone = Hints.hint(hints, ZoneId.class);
        dto.setTimestamp(entity.getTimestamp().toInstant().atZone(timezone).toLocalDateTime());
        
This is a call to a collective transform the `Transformer` interface provides. It transforms a collection into a grouped list. 

        dto.setChilds(childMapper.transformToGroupedArrayLists(entity.getChilds(), child -> child.getType(), hints));
        
That's it, return the object.

        return dto;
    }
    
Have a look at the [ParentMapper-Sample](https://github.com/porscheinformatik/anti-mapper/blob/master/src/test/java/at/porscheinformatik/antimapper/sample/parentchild/ParentMapper.java)!

### `Merger`

If you want to map an object into an existing object, then you implement a `Merger`. 

Create a new class and extends `AbstractMerger`. Make it a singleton - that means, you should always use the same instance for transforming. Write it thread-safe - that means, you class should only contain final fields!

Call the class something like `*Mapper`, even if it is just a Merger. This is convenient and makes it easier to find and recognize mapper classes.

A `Merger` is an interface with two methods to implement:

* `ENTITY_TYPE merge(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints);`
* `boolean isUniqueKeyMatching(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints);`

Additionally, it offers a lot of default methods for merging streams, collections, sets, lists and maps.

The `AbstractMerger`, you are using, encapsulates the null-checks. Start by implementing the merge function:

    @Override
    protected ChildEntity mergeNonNull(ChildDTO dto, ChildEntity entity, Object[] hints)
    {

Neither the DTO nor the entity is null. Set the values. Name and type conversions are trivial.

        entity.setKey(dto.getName());
        entity.setType(ChildType.valueOf(dto.getType()));

To set the parent, use the hints.

        entity.setParent(Hints.hint(hints, ParentEntity.class));

That's it, return the object.

        return entity;
    }

If the entity is null, the AbstractMerger calls the create function before the merge. Implement it and set the final values.

    @Override
    protected ChildEntity create(ChildDTO dto, Object[] hints)
    {
        return new ChildEntity(dto.getId());
    }

The `isUniqueKeyMatching` method is used to find the right entity in collections and maps. The matching function usually just checks the ID.

    @Override
    public boolean isUniqueKeyMatching(ChildDTO dto, ChildEntity entity, Object... hints)
    {
        return Objects.equals(dto.getId(), entity.getId());
    }

And yes, this is enough, even if you map multiple DTOs with null as ID, because matched DTOs/Entities will not be matched twice.

Have a look at the [ChildMapper-Sample](https://github.com/porscheinformatik/anti-mapper/blob/master/src/test/java/at/porscheinformatik/antimapper/sample/parentchild/ChildMapper.java)!

### `Mapper`

If you want a two-way mapping, then you implement a `Mapper`. It's the same as implementing a `Transformer` and a `Merger` in the same class.

> That's all you need to know - as I said, flat as a pancake. 

## Best Practices

### Mapping collections and maps

The `Transformer` and `Merger` interfaces contain a lot of methods for mapping collections and maps.

First, the `Transformer`. It contains the following methods:

* for transforming streams: `transformEach`, `transformToStream`.
* for transforming collection: `transformToCollection`, `transformToHashSet`, `transformToTreeSet`, `transformToArrayList`.
* for transforming maps: `transformToMap`, `transformToHashMap`.
* for transforming grouped maps: `transformToGroupedMap`, `transformToGroupedHashSets`, `transformToGroupedTreeSets`, `transformToGroupedArrayLists`, `transformGroupedMapToGroupedMap`, `transformGroupedMapToGroupedHashSets`, `transformGroupedMapToGroupedTreeSets`, `transformGroupedMapToGroupedArrayLists`.
    
The resulting maps and collections will not contain any null values. If you need them, pass the `Hint.KEEP_NULL` hint. If the resulting collection should be unmodifiable (e.g. for a cache), pass the `Hint.UNMODIFIABLE` hint.

Next, the Merger. It basically distinguishes between ordered and mixed collections. Lists will keep the order when being mapped, sets will ignore the order. The mixed method just uses the `isUniqueKeyMatching` method to find the entity, the ordered method additionally uses a diff algorithm, too.

* for merging collections: `mergeIntoMixedCollection`, `mergeIntoOrderedCollection`.    
* for merging sets (mixed): `mergeIntoHashSet`, `mergeIntoTreeSet`.    
* for merging lists (ordered): `mergeIntoArrayList`.
* for merging maps into sets or lists: `mergeMapIntoMixedCollection`, `mergeMapIntoOrderedCollection`, `mergeMapIntoTreeSet`, `mergeMapIntoArrayList`.    
* for merging grouped maps into sets or lists: `mergeGroupedMapIntoMixedCollection`, `mergeGroupedMapIntoOrderedCollection`, `mergeGroupedMapIntoHashSet`, `mergeGroupedMapIntoTreeSet`, `mergeGroupedMapIntoArrayList`.
    
For keeping null values in the resulting collections, you need to pass the `Hint.KEEP_NULL` hint. If you pass the `Hint.UNMODIFIABLE` the maps and collections are assumed to be unmodifiable, and will always be new instances.

If you need to process all entities in the collection after merging, implement the `afterMergeIntoCollection` method.
    
### `isUniqueKeyMatching` method

This method is used by the mapping method that map collections of DTOs to collections of entities. It helps to determine which DTO matches which entity. Despite it is just a little method, it can be a little bit tricky to implement.

First of all, the method is never called with null values, this is checked beforehand.

If your objects have an unique id, the first step is to check for equality. If the id is the same, return true.

    if (Objects.equals(dto.getId(), entity.getId())) {
        return true;
    } 

This even works if your have multiple DTOs with null as id (this usually happens when you add multiple new childs). Objects that have already been matched, will not be matched another time. There is a strict one-to-one relationship between DTOs and entities. If you return true for a DTO/entity pair, neither object will take part in another match.

Most of the time, checking the unique id is enough.

    ...
    return false;     

If your objects have some functional keys you may want to check these, too. This is only necessary if you are not sure, that your DTO contains the id, even if the entity exists already.

    if (Objects.equals(dto.getKey(), entity.getKey())) {
        return true;
    }

If your matching method is wrong, the less bad thing that can happen, is that too many mappings will take place. This happens if you always return `true`. The worse thing may be, that entities will never be updated and any DTO will be added to the entities. This happens if you always return `false`.  

### `afterMergeIntoCollection`

Sometimes it is necessary to update all entities of a collection, after the merge has finished. You will need this, e.g. if you want to update an ordinal value.

The merger contains a method that is called after collection merges, this is a simple implementation that updates the ordinal:

    public void afterMergeIntoCollection(Collection<ChildEntity> entities, Object... hints) {
        int ordinal = 0;
        for (ChildEntity entity : entities)
        {
            ordinal = Math.max(ordinal, entity.getOrdinal());
            entity.setOrdinal(ordinal++);
        }
    }

### Deleted-Flags and deleted-timestamps

To work with deleted-flags or deleted-timestamps, have a look at the [DeletedFlag Sample](https://github.com/porscheinformatik/anti-mapper/tree/master/src/test/java/at/porscheinformatik/antimapper/sample/deletedflag).

It does not map entities that have been deleted, and sets the deleted-flag, if a DTO is missing.

You can find the trick in the [ChildMapper](https://github.com/porscheinformatik/anti-mapper/blob/master/src/test/java/at/porscheinformatik/antimapper/sample/deletedflag/ChildMapper.java):

    @Override 
    protected ChildDTO transformNonNull(ChildEntity entity, Object[] hints) 
    { 
        if (entity.isDeleted()) 
        { 
            return null; 
        } 
        return new ChildDTO(entity.getId(), entity.getName()); 
    } 
    
    @Override 
    protected ChildEntity mergeNull(ChildEntity entity, Object[] hints) 
    { 
        entity.setDeleted(true); 
        return entity; 
    }

Null values will automatically be filtered from the resulting collections.

# Installation

Make sure you have [Apache Maven](https://maven.apache.org/). Just type:

    mvn install



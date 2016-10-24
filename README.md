# Anti-Mapper

Entity <-> DTO mapping in Java is a bit of a problem. 

## Introduction 

You have two possibilities: either do it manually or use one of the countless libraries.

Doing it manually is a boring task. It is always the same procedure but nevertheless the implementations fall apart
and quickly become irregular, error-prone and hard to maintain.

Using a library simplifies and unifies the default cases, but usually makes special cases harder to solve. Most often
you cannot simply implement theses special cases, you have to configure the mapper instead. And this can be a real pain.

Anti-Mapper offers a third possibility, the **assisted manual mapping**. That's why it is called Anti-Mapper. It is no
classic mapping library. That's exactly the functionality it is missing.  

**Happy one:** _"It let's you implement the direct mappings manually and just helps with the complex stuff like:_
_collections, sets, lists and maps._
_It offers simple interfaces like a_ `Transformer` _and a_ `Merger` _which will keep your implementations uniform_
_and consistent. There is no need for any configuration. Everything is pure Java code, checked by the compiler."_


**Grumpy one:** _"But OMG, what if I add a property to the entity and the DTO, I'm sure, I will forget the mapping. No reflection! What a stupid library!"_
 
**Happy one:** _"No. Because you will test your new property at least once. You will notice that. If you don't, it's not the mappers fault."_

**Grumpy one:** _"I'm faster when I map it manually."_
 
**Happy one:** _"No. You are mapping it manually. You cannot be faster either way."_

**Grumpy one:** _"It will never map my lists the right way!"_
 
**Happy one:** _"It will. It uses a diff-algorithm for lists and reuses entities for Hibernate's sake. It can even handle_
_deleted flags. There is no reason not to use the list mappings. But the best thing is: if you want to implement it in_
_your style, it does not hinder you to do it."_

**Grumpy one:** _"It's not worth to learn it."_

**Happy one:** _"There is one recipe that fits all. Just read on."_

## Basic Concepts

### DTO and Entity

Anti-Mapper commonly uses the terms _entity_ and _DTO_.

### Entity (in the scope of Anti-Mapper)

For Anti-Mapper an _entity_ is an existing object that has to be modified when being mapped into. The most common case 
for this is a database entity, that's why it is called _entity_. In Anti-Mapper the process of mapping into an 
existing object called _merging_.

### DTO (in the scope of Anti-Mapper)

A _DTO_ on the other hand, is an object that will always be newly created when mapped into. The most common case for 
this is a DTO used in client-server applications. In Anti-Mapper the process of mapping into a newly created object
is called _transforming_.

### `Transformer`

A `Transformer` maps an object (most often an _entity_) into a newly created object (most often a _DTO_). 

### `Merger`

A `Merger` maps an object (most often a _DTO_) into an existing object (most often an _entity_).

### `Mapper`

A `Mapper` is a combination of a `Transformer` and a `Merger`. If you implement a mapper you usually call it
`SomethingLikeMapper`, no matter whether it is a `Transformer`, `Merger` or both.

### `Referer`

A `Referer` is a special case for mapping into _entities_, when it is enough to just set the ID.

### `Hints`

`Hints` are a list of objects that can be passed to transform and merge functions. Hints provide a context for mapping
that make it simple to access default values or pass parent objects.

## How-To

### `Transformer` and `AbstractTransformer`

If you want to map an object into a newly created object, then you implement a `Transformer`. You can either 
implement the interface or extend the `AbstractTransformer`. Make it a singleton - that means, you should always 
use the same instance for transforming. Write it thread-safe (don't use fields)!

Call the class something like `*Mapper`. This is convenient and it makes it easy to find and recognize mapper classes.

A `Transformer` is an interface with one method: 

* `DTO_TYPE transform(ENTITY_TYPE entity, Object... hints);`

This is the only method you have to implement for a working transformer. The interface offers a lot of default methods
for transforming collections, sets, lists and maps.

A typical `Transformer` looks like this:

    @Override
    public ParentDTO transform(ParentEntity entity, Object... hints)
    {

First, check for null. This is mandatory. You will have to do it in every transformer. You can use the 
`AbstractTransformer` to avoid this.

        if (entity == null)
        {
            return null;
        }
        
Next, start transforming by creating your target object. Final values are an easy task.

        ParentDTO dto = new ParentDTO(entity.getId());
         
Add the entity and the DTO to the hints, just in case any child mapper needs it.

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

### `Merger` and `AbstractMerger`

If you want to map an object into an existing object, then you implement a `Merger`. You can either 
implement the interface or extend the `AbstractMerger`. Make it a singleton - that means, you should always 
use the same instance for merging. Write it thread-safe (don't use fields)!

Call the class something like `*Mapper`. This is convenient and it makes it easy to find and recognize mapper classes.

A `Merger` is an interface with two methods:

* `ENTITY_TYPE merge(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints);`
* `boolean isUniqueKeyMatching(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints);`

The `merge` method puts the values of the DTO in to the entity. The `isUniqueKeyMatching` method is used to
find the right entity in collections and maps.

A typical `Merger` looks like this:

    @Override
    public ChildEntity merge(ChildDTO dto, ChildEntity entity, Object... hints)
    {
    
First, check for null. This is mandatory. You will have to do it in every merger. You can use the 
`AbstractMerger` to avoid this.
    
        if (dto == null)
        {
            return null;
        }

Next, check if you have to create a new entity. You can use the `AbstractMerger` to avoid this.

        if ((entity == null) || (!Objects.equals(dto.getId(), entity.getId())))
        {
            entity = new ChildEntity();
            entity.setId(dto.getId());
        }

Then set the values. Name and type conversions are trivial.

        entity.setKey(dto.getName());
        entity.setType(ChildType.valueOf(dto.getType()));

To set the parent, use the hints.

        entity.setParent(Hints.hint(hints, ParentEntity.class));

That's it, return the object.

        return entity;
    }

The matching function usually just checks the ID.

    @Override
    public boolean isUniqueKeyMatching(ChildDTO dto, ChildEntity entity, Object... hints)
    {
        return Objects.equals(dto.getId(), entity.getId());
    }

And yes, this is enough, even if you map multiple DTOs with null as ID, because matched DTOs/Entity will not be matched
twice.

Have a look at the [ChildMapper-Sample](https://github.com/porscheinformatik/anti-mapper/blob/master/src/test/java/at/porscheinformatik/antimapper/sample/parentchild/ChildMapper.java)!

### `Mapper` and `AbstractMapper`

If you want a two-way mapping, then you implement a `Mapper`. It's the same as implementing a `Transformer` and a 
`Merger` in the same class.

### More documentation to come

* `public void afterMergeIntoCollection(Collection<ChildEntity> entities, Object... hints)`
* Collections, Sets, Lists, Maps
* Best practices

# Installation

	mvn install



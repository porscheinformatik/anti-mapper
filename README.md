# Happy Mapper

Entity <-> DTO mapping in Java is a bit of a problem. 

## Introduction 

You have two possibilities: either do it manually or use one of the countless libraries.

Doing it manually is a boring task. It is always the same procedure but nevertheless the implementations fall apart
and quickly become irregular, error-prone and hard to maintain.

Using a library simplifies and unifies the default cases, but usually makes special cases harder to solve. Most often
you cannot simply implement theses special cases, you have to configure the mapper instead. And this can be a real pain.

Happy Mapper offers a third possibility, the assisted manual mapping

It let's you implement the direct mappings manually and just helps with the complex mapping of collections, sets, lists 
and maps. It offers simple interfaces like a `Transformer` and a `Merger` which will keep your implementations 
uniform and consistent. There is no need for any configuration. Everything is pure Java code, that will be checked
by the compiler.

## Basic Concepts

### DTO and Entity

Happy Mapper commonly uses the terms _entity_ and _DTO_.

### Entity (in the scope of Happy Mapper)

For Happy Mapper an _entity_ is an existing object that has to be modified when being mapped into. The most common case 
for this is a database entity, that's why it is called _entity_. In Happy Mapper the process of mapping into an 
existing object called _merging_.

### DTO (in the scope of Happy Mapper)

A _DTO_ on the other hand, is an object that will always be newly created when mapped into. The most common case for 
this is a DTO used in client-server applications. In Happy Mapper the process of mapping into a newly created object
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

'Hints' are a list of objects that can be passed to transform and merge functions. Hints provide a context for mapping
that make it simple to access default values or pass parent objects.

## How-To

### `Transformer`

You implement a `Transformer` if you want to map an object into a newly create object. You make it a singleton.

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
         
Add the entity and the DTO to the hints, just in case the childMapper needs it.

        hints = Hints.join(hints, entity, dto);
        
Then set the values. Name and type conversions are trivial.
        
        dto.setName(entity.getKey());
        
        ZoneId timezone = Hints.hint(hints, ZoneId.class);
        dto.setTimestamp(entity.getTimestamp().toInstant().atZone(timezone).toLocalDateTime());
        
This is a call to a collective transform the `Transformer` interface provides. It transforms a collection into a grouped list. 

        dto.setChilds(childMapper.transformToGroupedArrayLists(entity.getChilds(), child -> child.getType(), hints));
        
That's it, return the object.

        return dto;
    }

### `Merger`

You implement a `Merger` if you want to map an object into an existing object. You make it a singleton.

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

### `Mapper`

You implement a `Mapper` if you want two-way mapping. It's the same as implementing a `Transformer` and a 
`Merger` in the same class.

### More documentation to come

* `public void afterMergeIntoCollection(Collection<ChildEntity> entities, Object... hints)`
* Collections, Sets, Lists, Maps
* Best practices





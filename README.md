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

A `Merger` maps an object (most often a _DTO_) into an existing object (most often an _entity_). This process is

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

A `Transformer` is an interface with one method: `DTO_TYPE transform(ENTITY_TYPE entity, Object... hints);`
This is the only method you have to implement for a working transformer. The interface offers a lot of default methods
for transforming collections, sets, lists and maps.

A typical transformer looks like this:

    @Override
    public ParentDTO transform(ParentEntity entity, Object... hints)
    {

First, check for null. This is mandatory. You will have to do it in every transformer. You can use the 
`AbstractTransformer` to avoid this.

        if (entity == null)
        {
            return null;
        }
        
Next, start transforming.

        ParentDTO dto = new ParentDTO();
         
Add the entity and the DTO to the hints, just in case the childMapper needs it.

        hints = Hints.join(hints, entity, dto);
        
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        
This is a call to a collective transform of Happy Mapper. It transforms a collection into a grouped list. 

        dto.setChilds(childMapper.transformToGroupedArrayLists(entity.getChilds(), child -> child.getType(), hints));
        
That's it, return the object.

        return dto;
    }




- TBD -





package cn.jrymos.spring.custom.injection.ccc.model;

import cn.jrymos.spring.custom.injection.ccc.CccCollection;
import cn.jrymos.spring.custom.injection.ccc.CccMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
@Getter
@RequiredArgsConstructor
public class XxxService {

    private final Map<String, Animal> animalMap;
    private final Set<Animal> animalSet;

    @CccMap(keyMethod = "getClass")
    private final Map<Class<? extends Animal>, Animal> map1;
    @CccMap(value = {"duck", "cat"})
    private final Map<String, Animal> map2;
    @CccMap(value = {"bird", "duck"}, keyMethod = "getClass")
    private final Map<Class<? extends Animal>, Animal> map3;
    @CccMap(value = {"water", "cat"})
    private final Map<String, Object> map4;


    @CccCollection
    private final Collection<Animal> empty;
    @CccCollection("water")
    private final Collection<Water> collection;
    @CccCollection("water")
    private final List<Water> list;
    @CccCollection({"water", "cat", "bird"})
    private final Set<Object> set;
    @CccCollection({"bird", "duck", "cat"})
    private final TreeSet<Animal> treeSet;
}

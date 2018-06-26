// Copyright 2018 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.wiki.webservice.data.config.metric;

import com.yahoo.bard.webservice.data.config.metric.makers.*;
import com.yahoo.bard.webservice.data.dimension.DimensionDictionary;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;
import com.yahoo.bard.webservice.data.time.DefaultTimeGrain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;

import javax.inject.Singleton;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Metric maker dictionary.
 * <p>
 * map metric maker name to metric maker instance
 */
@Singleton
public class MetricMakerDictionary {

    private static final Logger LOG = LoggerFactory.getLogger(MetricMakerDictionary.class);

    /**
     * Maps from metric maker names to metric makers.
     */
    private final LinkedHashMap<String, MetricMaker> nameToMetricMaker;

    private MetricDictionary metricDictionary = null;
    private DimensionDictionary dimensionDictionary = null;

    /**
     * Constructor.
     */
    public MetricMakerDictionary() {
        nameToMetricMaker = new LinkedHashMap<>();
    }

    /**
     * Constructor, initial map from all maker names to maker instances in dictionary.
     *
     * @param metricMakers        a list of metric makers
     * @param metricDictionary    metric dictionary as parameter for makers
     * @param dimensionDictionary dimension dictionary as parameter for makers
     */
    public MetricMakerDictionary(LinkedHashSet<WikiMetricMakerTemplate> metricMakers,
                                 MetricDictionary metricDictionary,
                                 DimensionDictionary dimensionDictionary) {

        this.nameToMetricMaker = new LinkedHashMap<>();
        this.metricDictionary = metricDictionary;
        this.dimensionDictionary = dimensionDictionary;

        for (WikiMetricMakerTemplate maker : metricMakers) {

            try {
                Class<?> makerClass = Class.forName(maker.getClassPath());
                Class<?>[] params = makerClass.getDeclaredConstructors()[0].getParameterTypes();

                DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
                String[] parameterNames = discoverer.getParameterNames(makerClass.getDeclaredConstructors()[0]);

                Object[] args = IntStream.range(0, params.length)
                        .mapToObj(i -> parseParams(params[i].getSimpleName(), parameterNames[i], maker))
                        .toArray();

                add(maker.getName(), (MetricMaker) makerClass.getConstructors()[0].newInstance(args));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Find a metric maker given a metric maker Name.
     *
     * @param metricMakerName Name to search
     * @return the first metric maker found (if exists)
     */
    public MetricMaker findByName(String metricMakerName) {
        return nameToMetricMaker.get(metricMakerName.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Get all metric makers available in metric maker dictionary.
     *
     * @return a set of metric makers
     */
    public Set<MetricMaker> findAll() {
        return Collections.unmodifiableSet(new HashSet<>(nameToMetricMaker.values()));
    }

    /**
     * Adds the specified element to the dictionary if it is not already present.
     *
     * @param name        key to add to dictionary
     * @param metricMaker value to add to dictionary
     * @return <tt>true</tt> if the dictionary did not already contain the specified metric maker
     */
    public boolean add(String name, MetricMaker metricMaker) {
        if (nameToMetricMaker.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return false;
        }
        MetricMaker metricMakers = nameToMetricMaker.put(name.toLowerCase(Locale.ENGLISH), metricMaker);
        if (metricMakers != null) {
            // should never happen unless multiple loaders are running in race-condition
            ConcurrentModificationException e = new ConcurrentModificationException();
            LOG.error("Multiple loaders updating MetricMakerDictionary", e);
            throw e;
        }
        return true;
    }

    /**
     * Adds all of the metric makers in the specified collection to the dictionary.
     *
     * @param metricMakers collection of metric makers to add
     * @return <tt>true</tt> if the dictionary changed as a result of the call
     */
    public boolean addAll(Collection<MetricMaker> metricMakers) {
        boolean flag = false;
        for (MetricMaker metricMaker : metricMakers) {
            flag = add(metricMaker.toString(), metricMaker) || flag;
        }
        return flag;
    }

    @Override
    public String toString() {
        return "MetricMaker Dictionary: " + nameToMetricMaker;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nameToMetricMaker);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MetricMakerDictionary) {
            MetricMakerDictionary that = (MetricMakerDictionary) obj;
            return nameToMetricMaker.equals(that.nameToMetricMaker);
        }
        return false;
    }

    /**
     * Parse parameters for maker's constructor based on parameter's name and type.
     *
     * @param paramType type of the parameter in maker's constructor
     * @param paramName name of the parameter in maker's constructor
     * @param maker     the maker template used to find parameter's value by name
     * @return the value of parameter (can be any type)
     */
    private Object parseParams(String paramType, String paramName, WikiMetricMakerTemplate maker) {
        if ("MetricDictionary".equals(paramType)) {
            return metricDictionary;
        }
        if ("DimensionDictionary".equals(paramType)) {
            return dimensionDictionary;
        }
        if ("ZonelessTimeGrain".equals(paramType)) {
            return DefaultTimeGrain.valueOf(maker.getParams().get(paramName));
        }
        if ("int".equals(paramType)) {
            return Integer.parseInt(maker.getParams().get(paramName));
        }
        if ("double".equals(paramType)) {
            return Double.parseDouble(maker.getParams().get(paramName));
        }
        return null;
    }
}

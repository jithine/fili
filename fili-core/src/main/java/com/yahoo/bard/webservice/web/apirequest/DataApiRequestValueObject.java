// Copyright 2019 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest;

import com.yahoo.bard.webservice.data.dimension.Dimension;
import com.yahoo.bard.webservice.data.dimension.DimensionField;
import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.time.Granularity;
import com.yahoo.bard.webservice.druid.model.orderby.OrderByColumn;
import com.yahoo.bard.webservice.table.LogicalTable;
import com.yahoo.bard.webservice.util.UnmodifiableLinkedHashMap;
import com.yahoo.bard.webservice.util.UnmodifiableLinkedHashSet;
import com.yahoo.bard.webservice.web.ApiHaving;
import com.yahoo.bard.webservice.web.ResponseFormatType;
import com.yahoo.bard.webservice.web.filters.ApiFilters;
import com.yahoo.bard.webservice.web.filters.UnmodifiableApiFilters;
import com.yahoo.bard.webservice.web.util.PaginationParameters;

import com.google.common.collect.ImmutableList;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * And immutable POJO implementation of {@link DataApiRequest} contract. All data is provided through the constructor
 * and set, with very minor or no transformations occurring on the data. Unlike the old implementation
 * ({@link com.yahoo.bard.webservice.web.apirequest.DataApiRequestImpl}), this implementation does not build any of
 * its components and all components must be fully built at creation time.
 */
public class DataApiRequestValueObject implements DataApiRequest {

    private final LogicalTable table;
    private final Granularity granularity;
    private final LinkedHashSet<Dimension> dimensions;
    private final LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> perDimensionFields;
    private final LinkedHashSet<LogicalMetric> metrics;
    private final List<Interval> intervals;
    private final ApiFilters apiFilters;
    private final LinkedHashMap<LogicalMetric, Set<ApiHaving>> havings;
    private final LinkedHashSet<OrderByColumn> allSorts;
    private final Integer count;
    private final Integer topN;
    private final ResponseFormatType format;
    private final String downloadFilename;
    private final DateTimeZone timeZone;
    private final Long asyncAfter;
    private final PaginationParameters paginationParameters;

    /**
     * Constructor.
     *
     * @param table  Logical table the query should run against
     * @param granularity  Granularity of the query
     * @param dimensions  The grouping dimensions for the query
     * @param perDimensionFields  The mapping of dimension to fields for that dimension that must be present in the
     *                            response
     * @param metrics  The metrics of the query
     * @param intervals  The time intervals this query reports on
     * @param apiFilters  The dimension filters for this query
     * @param havings  The metric filters for this query
     * @param allSorts  The sorts for this query, include the sort on dateTime if present
     * @param count  The count for this query
     * @param topN  The topN for this query
     * @param format  The data format the response should be returned as (e.g. JSON, CSV, ...)
     * @param downloadFilename  The name of the file the response should be downloaded as. The presence of this
     *                          parameter indicates the response must be returned as a file for download by the client
     * @param timeZone  The timezone this query should run for
     * @param asyncAfter  The time limit after which the results must be returned asynchronously
     * @param paginationParameters  The parameters specifying the length of a response page and which response page to
     *                              return
     */
    public DataApiRequestValueObject(
            LogicalTable table,
            Granularity granularity,
            LinkedHashSet<Dimension> dimensions,
            LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> perDimensionFields,
            LinkedHashSet<LogicalMetric> metrics,
            List<Interval> intervals,
            ApiFilters apiFilters,
            LinkedHashMap<LogicalMetric, Set<ApiHaving>> havings,
            LinkedHashSet<OrderByColumn> allSorts,
            Integer count,
            Integer topN,
            ResponseFormatType format,
            String downloadFilename,
            DateTimeZone timeZone,
            Long asyncAfter,
            PaginationParameters paginationParameters
    ) {
        this.table = table;
        this.granularity = granularity;
        this.dimensions = UnmodifiableLinkedHashSet.of(dimensions);
        this.perDimensionFields = UnmodifiableLinkedHashMap.of(perDimensionFields);
        this.metrics = UnmodifiableLinkedHashSet.of(metrics);
        this.intervals = ImmutableList.copyOf(intervals);
        this.apiFilters = UnmodifiableApiFilters.of(new ApiFilters(apiFilters));
        this.havings = UnmodifiableLinkedHashMap.of(havings);
        this.allSorts = UnmodifiableLinkedHashSet.of(allSorts);
        this.count = count;
        this.topN = topN;
        this.format = format;
        this.downloadFilename = downloadFilename;
        this.timeZone = timeZone;
        this.asyncAfter = asyncAfter;
        this.paginationParameters = paginationParameters;
    }

    // *******************************************
    // ************** STANDARD GETS **************
    // *******************************************

    @Override
    public LogicalTable getTable() {
        return table;
    }

    @Override
    public Granularity getGranularity() {
        return granularity;
    }

    @Override
    public Set<Dimension> getDimensions() {
        return dimensions;
    }

    @Override
    public LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> getDimensionFields() {
        return perDimensionFields;
    }

    @Override
    public Set<LogicalMetric> getLogicalMetrics() {
        return metrics;
    }

    @Override
    public List<Interval> getIntervals() {
        return intervals;
    }

    @Override
    public ApiFilters getApiFilters() {
        return apiFilters;
    }

    @Override
    public LinkedHashMap<LogicalMetric, Set<ApiHaving>> getHavings() {
        return havings;
    }

    @Override
    public LinkedHashSet<OrderByColumn> getSorts() {
        return DataApiRequest.extractStandardSorts(getAllSorts());
    }

    @Override
    public Optional<OrderByColumn> getDateTimeSort() {
        return DataApiRequest.extractDateTimeSort(getAllSorts());
    }

    @Override
    public LinkedHashSet<OrderByColumn> getAllSorts() {
        return allSorts;
    }

    @Override
    public Optional<Integer> getCount() {
        return Optional.ofNullable(count);
    }


    @Override
    public Optional<Integer> getTopN() {
        return Optional.ofNullable(topN);
    }

    @Override
    public ResponseFormatType getFormat() {
        return format;
    }

    @Override
    public Optional<String> getDownloadFilename() {
        return Optional.ofNullable(downloadFilename);
    }

    @Override
    public DateTimeZone getTimeZone() {
        return timeZone;
    }

    @Override
    public Long getAsyncAfter() {
        return asyncAfter;
    }

    @Override
    public Optional<PaginationParameters> getPaginationParameters() {
        return Optional.ofNullable(paginationParameters);
    }

    //*************************************
    //************** WITHERS **************
    //*************************************

    @Override
    public DataApiRequest withTable(LogicalTable table) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withGranularity(Granularity granularity) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withDimensions(LinkedHashSet<Dimension> dimensions) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withPerDimensionFields(
            LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> perDimensionFields
    ) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withLogicalMetrics(LinkedHashSet<LogicalMetric> logicalMetrics) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                logicalMetrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withIntervals(List<Interval> intervals) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withFilters(ApiFilters filters) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                filters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withHavings(LinkedHashMap<LogicalMetric, Set<ApiHaving>> havings) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withSorts(LinkedHashSet<OrderByColumn> sorts) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                DataApiRequest.combineSorts(getDateTimeSort().orElse(null), sorts),
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withTimeSort(OrderByColumn timeSort) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                DataApiRequest.combineSorts(timeSort, getSorts()),
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withAllSorts(LinkedHashSet<OrderByColumn> allSorts) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withTimeZone(DateTimeZone timeZone) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withTopN(Integer topN) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withCount(Integer count) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withPaginationParameters(PaginationParameters paginationParameters) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withFormat(ResponseFormatType format) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withDownloadFilename(String downloadFilename) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }

    @Override
    public DataApiRequest withAsyncAfter(long asyncAfter) {
        return new DataApiRequestValueObject(
                table,
                granularity,
                dimensions,
                perDimensionFields,
                metrics,
                intervals,
                apiFilters,
                havings,
                allSorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZone,
                asyncAfter,
                paginationParameters
        );
    }
}

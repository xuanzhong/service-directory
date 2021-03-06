/**
 * Copyright 2014 Cisco Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cisco.oss.foundation.directory.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.cisco.oss.foundation.directory.proto.QueryServiceProtocol.QueryCommand;

/**
 * The ServiceInstance query container for filtering ServiceInstances.
 *
 * Application uses it to filter the desired ServiceInstances when querying the ServiceInstances in ServiceDirectory.
 * It provides a method for the application to add a QueryCriterion. The resulted QueryCriteia follows the logical "and" operation.
 * QueryCriterion only operates on the metadata of the ServiceInstance.
 *
 *
 */
public class ServiceInstanceQuery {

    /**
     * The criteria.
     */
    List<QueryCriterion> criteria ;

    /**
     * Constructor.
     */
    public ServiceInstanceQuery(){

    }

    /**
     * Get a metadata value equal QueryCriterion.
     *
     * Add a QueryCriterion to check ServiceInstance has metadata value
     * equal to target.
     *
     * @param key
     *         the metadata key.
     * @param value
     *         the metadata value should equal to.
     * @return
     *         the ServiceInstanceQuery.
     */
    public ServiceInstanceQuery getEqualQueryCriterion(String key, String value){
        QueryCriterion c = new EqualQueryCriterion(key, value);
        addQueryCriterion(c);
        return this;
    }

    /**
     * Get a metadata value not equal QueryCriterion.
     *
     * Add a QueryCriterion to check ServiceInstance has metadata value
     * not equal to target.
     *
     * @param key
     *         the metadata key.
     * @param value
     *         the metadata value should not equal to.
     * @return
     *         the ServiceInstanceQuery.
     */
    public ServiceInstanceQuery getNotEqualQueryCriterion(String key, String value){
        QueryCriterion c = new NotEqualQueryCriterion(key, value);
        addQueryCriterion(c);
        return this;
    }

    /**
     * Get a metadata value match regex pattern QueryCriterion.
     *
     * Add a QueryCriterion to check ServiceInstance has metadata value
     * match the target regex pattern.
     *
     * @param key
     *         the metadata key.
     * @param pattern
     *         the target regex pattern that metadata should match.
     * @return
     *         the ServiceInstanceQuery.
     */
    public ServiceInstanceQuery getPatternQueryCriterion(String key, String pattern){
        QueryCriterion c = new PatternQueryCriterion(key, pattern);
        addQueryCriterion(c);
        return this;
    }

    /**
     * Get a metadata contain QueryCriterion.
     *
     * Add a QueryCriterion to check ServiceInstance has the
     * the specified metadata.
     *
     * @param key
     *         the metadata key.
     * @return
     *         the ServiceInstanceQuery.
     */
    public ServiceInstanceQuery getContainQueryCriterion(String key){
        QueryCriterion c = new ContainQueryCriterion(key);
        addQueryCriterion(c);
        return this;
    }

    /**
     * Get a metadata not contain QueryCriterion.
     *
     * Add a QueryCriterion to check ServiceInstance doesn't have the
     * the specified metadata.
     *
     * @param key
     *         the metadata key.
     * @return
     *         the ServiceInstanceQuery.
     */
    public ServiceInstanceQuery getNotContainQueryCriterion(String key){
        QueryCriterion c = new NotContainQueryCriterion(key);
        addQueryCriterion(c);
        return this;
    }

    /**
     * Get a metadata value in String list QueryCriterion.
     *
     * Add a QueryCriterion to check ServiceInstance has metadata value
     * in the target String list.
     *
     * @param key
     *         the metadata key.
     * @param list
     *         the target String list that metadata value should be in.
     * @return
     *         the ServiceInstanceQuery.
     */
    public ServiceInstanceQuery getInQueryCriterion(String key, List<String> list){
        QueryCriterion c = new InQueryCriterion(key, list);
        addQueryCriterion(c);
        return this;
    }

    /**
     * Get a metadata value not in String list QueryCriterion.
     *
     * Add a QueryCriterion to check ServiceInstance has metadata value
     * not in the target String list.
     *
     * @param key
     *         the metadata key.
     * @param list
     *         the target String list that metadata value should not be in.
     * @return
     *         the ServiceInstanceQuery.
     */
    public ServiceInstanceQuery getNotInQueryCriterion(String key, List<String> list){
        QueryCriterion c = new NotInQueryCriterion(key, list);
        addQueryCriterion(c);
        return this;
    }

    /**
     * Add a QueryCriterion.
     *
     * @param criterion
     *         the QueryCriterion.
     */
    public void addQueryCriterion(QueryCriterion criterion){
        if( criteria == null){
            criteria = new ArrayList<QueryCriterion>();
        }
        criteria.add(criterion);
    }

    /**
     * Get the QueryCriteria.
     * This method is only used in the ServiceInstanceQueryHelper.
     *
     * @return
     *         the list of QueryCriterion.
     */
    public List<QueryCriterion> getCriteria(){
        return this.criteria;
    }

    /**
     * Logical not equal QueryCriterion.
     *
     * Check whether the ServiceInstance has the same metadata value doesn't equal to the criterion.
     *
     *
     */
    public static class NotEqualQueryCriterion implements QueryCriterion, StringCommand{

        public static final String OP = "not equal";

        /**
         * The metadata key.
         */
        private String key;

        /**
         * the metadata value String equal criterion.
         */
        private String criterion ;

        /**
         * EqualQueryCriterion Constructor.
         *
         * @param key        The key of the ServiceInstance metadata.
         * @param criterion    The String of the metadata value equals to.
         */
        public NotEqualQueryCriterion(String key, String criterion){
            this.key = key;
            this.criterion = criterion;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatch(Map<String, String> metadataMap) {
            if(metadataMap == null){
                return false;
            }
            String metavalue = metadataMap.get(key);

            if(metavalue == null){
                return false;
            }

            if(! metavalue.equals(criterion)){
                return true;
            }

            return false;
        }

        @Override
        public String getMetadataKey() {
            return key;
        }

        @Override
        public String toString(){
            return "operation=not equals, key=" + key + ", criterion=" + criterion;
        }

        public String getCriterion(){
            return criterion;
        }

        @Override
        public QueryCommand getStringCommand() {
            List<String> values = new ArrayList<String>();
            values.add(criterion);
            QueryCommand c = new QueryCommand(key, OP, values);
            return c;
        }
    }

    /**
     * Logical equal QueryCriterion.
     *
     * Check whether the ServiceInstance has the same metadata value as the criterion.
     *
     *
     */
    public static class EqualQueryCriterion implements QueryCriterion, StringCommand{

        public final static String OP = "equal";

        /**
         * The metadata key.
         */
        private String key;

        /**
         * the metadata value String equal criterion.
         */
        private String criterion ;

        /**
         * EqualQueryCriterion Constructor.
         *
         * @param key        The key of the ServiceInstance metadata.
         * @param criterion    The String of the metadata value equals to.
         */
        public EqualQueryCriterion(String key, String criterion){
            this.key = key;
            this.criterion = criterion;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatch(Map<String, String> metadataMap) {
            if(metadataMap == null){
                return false;
            }
            String metavalue = metadataMap.get(key);
            if(metavalue != null){
                return metavalue.equals(criterion);
            }
            return false;
        }

        @Override
        public String getMetadataKey() {
            return key;
        }

        @Override
        public String toString(){
            return "operation=equals, key=" + key + ", criterion=" + criterion;
        }

        public String getCriterion(){
            return criterion;
        }

        @Override
        public QueryCommand getStringCommand() {
            List<String> values = new ArrayList<String>();
            values.add(criterion);
            QueryCommand c = new QueryCommand(key, OP, values);
            return c;
        }
    }

    /**
     * Regex pattern match QueryCriterion.
     *
     * Check whether the ServiceInstance has the metadata matching the pattern.
     *
     *
     */
    public static class PatternQueryCriterion implements QueryCriterion, StringCommand{

        public static final String OP = "like";
        /**
         * Metadata key.
         */
        private String key;

        /**
         * the metadata value String regex pattern criterion.
         */
        private String criterion ;

        /**
         * PatternQueryCriterion Constructor.
         *
         * @param key        The key of the metadata.
         * @param criterion    The pattern to match against the metadata value.
         */
        public PatternQueryCriterion(String key, String criterion){
            this.key = key;
            this.criterion = criterion;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatch(Map<String, String> metadataMap) {
            if(metadataMap == null){
                return false;
            }
            String metavalue = metadataMap.get(key);
            if(metavalue != null){
                return metavalue.matches(criterion);
            }
            return false;
        }

        @Override
        public String getMetadataKey() {
            return key;
        }

        @Override
        public String toString(){
            return "operation=matches, key=" + key + ", criterion=" + criterion;
        }

        public String getCriterion(){
            return criterion;
        }

        @Override
        public QueryCommand getStringCommand() {
            List<String> values = new ArrayList<String>();
            values.add(criterion);
            QueryCommand c = new QueryCommand(key, OP, values);
            return c;
        }
    }

    /**
     * Logical "has" QueryCriterion.
     *
     * Check whether the ServiceInstance contains the specified metadata.
     *
     *
     */
    public static class ContainQueryCriterion implements QueryCriterion, StringCommand{

        public static final String OP = "contain";

        /**
         * the metadata key.
         */
        private String key;

        /**
         * ContainQueryCriterion Constructor.
         *
         * @param key    the key of the metadata.
         */
        public ContainQueryCriterion(String key){
            this.key = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatch(Map<String, String> metadataMap) {
            return metadataMap.containsKey(key);
        }

        @Override
        public String getMetadataKey() {
            return key;
        }

        @Override
        public String toString(){
            return "operation=contains, key=" + key + ", criterion=null";
        }

        @Override
        public QueryCommand getStringCommand() {
            QueryCommand c = new QueryCommand(key, OP, null);
            return c;
        }
    }

    /**
     * Logical doesn't "has" QueryCriterion.
     *
     * Check whether the ServiceInstance doesn't contains the specified metadata.
     *
     *
     */
    public static class NotContainQueryCriterion implements QueryCriterion, StringCommand{

        public static final String OP = "not contain";

        /**
         * the metadata key.
         */
        private String key;

        /**
         * ContainQueryCriterion Constructor.
         *
         * @param key    the key of the metadata.
         */
        public NotContainQueryCriterion(String key){
            this.key = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatch(Map<String, String> metadataMap) {
            return ! metadataMap.containsKey(key);
        }

        @Override
        public String getMetadataKey() {
            return key;
        }

        @Override
        public String toString(){
            return "operation=not contains, key=" + key + ", criterion=null";
        }

        @Override
        public QueryCommand getStringCommand() {
            QueryCommand c = new QueryCommand(key, OP, null);
            return c;
        }
    }

    /**
     * Metadata value in list QueryCriterion.
     *
     * It check the value of the metatdata in a target String list.
     *
     *
     */
    public static class InQueryCriterion implements QueryCriterion, StringCommand{

        public static final String OP = "in";
        /**
         * the metadata key.
         */
        private String key;

        /**
         * The target String list.
         */
        private List<String> list;

        /**
         * ContainQueryCriterion Constructor.
         *
         * @param key    the key of the metadata.
         * @param list     the target String list.
         */
        public InQueryCriterion(String key, List<String> list){
            this.key = key;
            this.list = list;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatch(Map<String, String> metadataMap) {
            if(metadataMap == null){
                return false;
            }
            String value = metadataMap.get(key);
            for(String target : list){
                if(value.equals(target)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getMetadataKey() {
            return key;
        }

        @Override
        public String toString(){
            return "operation=in, key=" + key + ", criterion=" + Arrays.toString(list.toArray());
        }

        public List<String> getCriterion(){
            return list;
        }

        @Override
        public QueryCommand getStringCommand() {
            QueryCommand c = new QueryCommand(key, OP, list);
            return c;
        }
    }

    /**
     * Metadata value not in list QueryCriterion.
     *
     * It check the value of the metatdata not in a target String list.
     *
     *
     */
    public static class NotInQueryCriterion implements QueryCriterion, StringCommand{

        public static final String OP = "not in";

        /**
         * the metadata key.
         */
        private String key;

        /**
         * The target String list.
         */
        private List<String> list;

        /**
         * ContainQueryCriterion Constructor.
         *
         * @param key    the key of the metadata.
         * @param list     the target String list.
         */
        public NotInQueryCriterion(String key, List<String> list){
            this.key = key;
            this.list = list;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatch(Map<String, String> metadataMap) {
            if(metadataMap == null){
                return false;
            }
            String value = metadataMap.get(key);
            if(value == null ){
                return false;
            }

            for(String target : list){
                if(value.equals(target)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public String getMetadataKey() {
            return key;
        }

        @Override
        public String toString(){
            return "operation=not in, key=" + key + ", criterion=" + Arrays.toString(list.toArray());
        }

        public List<String> getCriterion(){
            return list;
        }

        @Override
        public QueryCommand getStringCommand() {
            QueryCommand c = new QueryCommand(key, OP, list);
            return c;
        }
    }
}
